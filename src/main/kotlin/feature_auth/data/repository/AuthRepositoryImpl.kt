package feature_auth.data.repository

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.sun.net.httpserver.HttpServer
import core.util.Resource
import core.util.StringResources.ERROR_UNKNOWN
import feature_auth.data.remote.Auth0Api
import feature_auth.data.remote.request.AuthorizationCodeRequest
import feature_auth.data.remote.request.RegisterRequest
import feature_auth.data.remote.response.RegisterResponse
import feature_auth.domain.repository.AuthRepository
import feature_auth.presentation.util.generateCodeChallenge
import feature_auth.presentation.util.generateCodeVerifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLEncoder

class AuthRepositoryImpl(
    private val auth0Api: Auth0Api,
    private val domain: String,
    private val clientId: String,
    private val audience: String,
) : AuthRepository {

    private var receivedAuthorizationCode: String? = null
    private var server: HttpServer? = null

    override suspend fun registerUser(email: String, password: String): Resource<RegisterResponse> {
        try {
            val response = auth0Api.registerUser(
                request = RegisterRequest(
                    email = email,
                    password = password,
                    connection = "Username-Password-Authentication"
                )
            )

            if (!response.isSuccessful) return Resource.Error(
                errorMessage = "Não foi possível registrar o usuário: ${
                    response.errorBody()?.string()
                }"
            )

            val registerResponse = response.body()
                ?: return Resource.Error(errorMessage = "Um erro desconhecido ocorreu. Tente fazer login ou cadastre novamente.")


            return Resource.Success(registerResponse)

        } catch (e: Exception) {
            return Resource.Error(errorMessage = ERROR_UNKNOWN)
        }
    }

    override suspend fun loginUserWithPkce(): Resource<String> {
        try {
            val codeVerifier = generateCodeVerifier()
            val codeChallenge = generateCodeChallenge(codeVerifier)

            val authUrl = buildAuthUrl(codeChallenge, "http://localhost:8080")

            // Start the local server to receive the authorization code
            startLocalServer(8080,
                onAuthorizationCodeReceived = { authorizationCode ->
                    receivedAuthorizationCode = authorizationCode
                }
            )

            openUrlInBrowser(authUrl)

            // Wait for the user to authorize and for the local server to receive the authorization code
            while (receivedAuthorizationCode == null) {
                delay(1000)
            }

            val authorizationCode = receivedAuthorizationCode
                ?: return Resource.Error("Login failed: User didn't provide an authorization code")

            val request = AuthorizationCodeRequest(
                grantType = "authorization_code",
                clientId = clientId,
                codeVerifier = codeVerifier,
                code = authorizationCode,
                redirectUri = "http://localhost:8080"
            )

            val loginResponse = auth0Api.login(request)
            if (!loginResponse.isSuccessful) return Resource.Error(
                "Login failed: ${
                    loginResponse.errorBody()?.string()
                }"
            )

            val user = loginResponse.body()
                ?: return Resource.Error(errorMessage = "Um erro desconhecido ocorreu. Tente fazer login novamente.")

            val accessToken = user.accessToken
            val decodedJWT: DecodedJWT = JWT.decode(accessToken)
            val auth0UserId = decodedJWT.subject


            return Resource.Success(auth0UserId)
        } catch (e: Exception) {
            return Resource.Error(errorMessage = ERROR_UNKNOWN)
        } finally {
            receivedAuthorizationCode = null
            server?.stop(0)
            server = null
        }
    }

    private suspend fun buildAuthUrl(codeChallenge: String, redirectUri: String): String {
        val authorizeUrl = "${domain}/authorize?" +
                "client_id=${clientId}&" +
                "response_type=code&" +
                "scope=openid%20profile%20email%20offline_access&" +
                "audience=${
                    withContext(Dispatchers.IO) {
                        URLEncoder.encode(audience, "UTF-8")
                    }
                }&" +
                "redirect_uri=${
                    withContext(Dispatchers.IO) {
                        URLEncoder.encode(redirectUri, "UTF-8")
                    }
                }&" +
                "code_challenge=${codeChallenge}&" +
                "code_challenge_method=S256&" +
                "prompt=login"

        return authorizeUrl
    }

    private fun startLocalServer(
        port: Int,
        onAuthorizationCodeReceived: (String) -> Unit,
    ) {
        server = HttpServer.create(InetSocketAddress(port), 1)

        server?.createContext("/") { httpExchange ->
            val uri = httpExchange.requestURI
            val query = uri.query

            try {
                if (query != null && query.startsWith("code=")) {
                    val authorizationCode = query.substringAfter("code=").substringBefore("&")
                    onAuthorizationCodeReceived(authorizationCode)

                    val response = "Received authorization code: $authorizationCode"
                    httpExchange.sendResponseHeaders(200, response.length.toLong())
                    httpExchange.responseBody.use { os -> os.write(response.toByteArray()) }
                } else {
                    httpExchange.sendResponseHeaders(404, -1)
                }
            } catch (e: Exception) {
                throw e
            }
        }

        server?.start()
    }

    private fun openUrlInBrowser(url: String): Boolean {
        return if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(url))
            true
        } else {
            // Handle the case where the desktop environment does not support browsing
            false
        }
    }
}