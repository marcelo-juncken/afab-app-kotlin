package feature_auth.data.remote

import feature_auth.data.remote.request.AuthorizationCodeRequest
import feature_auth.data.remote.request.RegisterRequest
import feature_auth.data.remote.response.LoginResponse
import feature_auth.data.remote.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.*

interface Auth0Api {

    @POST("oauth/token")
    suspend fun login(
        @Body request: AuthorizationCodeRequest,
    ): Response<LoginResponse>

    @POST("dbconnections/signup")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

}


