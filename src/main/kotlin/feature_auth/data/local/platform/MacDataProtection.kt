package feature_auth.data.local.platform

import feature_auth.data.local.DataProtection

class MacDataProtection : DataProtection {
    // Implement protectData and unprotectData methods using a suitable encryption library
    override fun protectData(data: String): String {
        return ""
    }

    override fun unprotectData(protectedData: String): String {
        return ""
    }
}

//
//import com.auth0.jwt.JWT
//import com.auth0.jwt.algorithms.Algorithm
//import com.auth0.jwt.interfaces.DecodedJWT
//import java.nio.charset.StandardCharsets
//import java.nio.file.Files
//import java.nio.file.Paths
//import java.security.KeyFactory
//import java.security.interfaces.RSAPrivateKey
//import java.security.interfaces.RSAPublicKey
//import java.security.spec.PKCS8EncodedKeySpec
//import java.security.spec.X509EncodedKeySpec
//import java.time.Instant
//import java.util.*
//
//
//class WindowsTokenStorage : TokenStorage {
//    private val tokensPath = Paths.get(System.getProperty("user.home"), ".myapp", "tokens")
//    private val publicKey = loadPublicKey("C:/Users/Marcelo/public_key.pem")
//    private val privateKey = loadPrivateKey("C:/Users/Marcelo/private_key.pem")
//    private val algorithm = Algorithm.RSA256(publicKey, privateKey)
//
//    init {
//        Files.createDirectories(tokensPath.parent)
//    }
//
//    override fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Long, sub : String) {
//        val jwtToken = JWT.create()
//            .withClaim("access_token", accessToken)
//            .withClaim("refresh_token", refreshToken)
//            .withClaim("sub", sub)
//            .withExpiresAt(Date(Instant.now().plusSeconds(expiresIn).toEpochMilli()))
//            .sign(algorithm)
//        Files.write(tokensPath, jwtToken.toByteArray())
//    }
//
//    override fun getTokens(): Tokens? {
//        if (Files.exists(tokensPath)) {
//            val jwtToken = String(Files.readAllBytes(tokensPath), StandardCharsets.UTF_8)
//            return try {
//                val verifier = JWT.require(algorithm).build()
//                val decodedJWT: DecodedJWT = verifier.verify(jwtToken)
//
//                val sub = decodedJWT.getClaim("sub").asString() ?: return null
//                val accessToken = decodedJWT.getClaim("access_token").asString() ?: return null
//                val refreshToken = decodedJWT.getClaim("refresh_token").asString() ?: return null
//                val expirationTimestamp = decodedJWT.expiresAt?.time ?: return null
//
//                Tokens(accessToken, refreshToken, expirationTimestamp, sub)
//            } catch (e: com.auth0.jwt.exceptions.TokenExpiredException) {
//                // Token has expired, return null to indicate the user needs to log in again
//                null
//            }
//        }
//        return null
//    }
//
//    private fun loadPrivateKey(privateKeyPath: String): RSAPrivateKey {
//        val privateKeyContent = String(Files.readAllBytes(Paths.get(privateKeyPath)))
//            .replace("-----BEGIN PRIVATE KEY-----", "")
//            .replace("-----END PRIVATE KEY-----", "")
//            .replace("\\s+".toRegex(), "")
//        val keySpec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent))
//        val keyFactory = KeyFactory.getInstance("RSA")
//        return keyFactory.generatePrivate(keySpec) as RSAPrivateKey
//    }
//
//    private fun loadPublicKey(publicKeyPath: String): RSAPublicKey {
//        val publicKeyContent = String(Files.readAllBytes(Paths.get(publicKeyPath)))
//            .replace("-----BEGIN PUBLIC KEY-----", "")
//            .replace("-----END PUBLIC KEY-----", "")
//            .replace("\\s+".toRegex(), "")
//        val keySpec = X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent))
//        val keyFactory = KeyFactory.getInstance("RSA")
//        return keyFactory.generatePublic(keySpec) as RSAPublicKey
//    }
//
//    override fun clearTokens() {
//        Files.deleteIfExists(tokensPath)
//    }
//
//    override fun isLoggedIn(): Boolean {
//        return getTokens() != null
//    }
//
//}