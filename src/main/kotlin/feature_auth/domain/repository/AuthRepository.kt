package feature_auth.domain.repository

import feature_auth.data.remote.response.RegisterResponse
import core.util.Resource
import core.util.SimpleResource
import java.security.PublicKey

interface AuthRepository {

    suspend fun registerUser(email: String, password: String): Resource<RegisterResponse>
    suspend fun loginUserWithPkce(): Resource<String>
}
