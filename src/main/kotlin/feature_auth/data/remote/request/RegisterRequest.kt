package feature_auth.data.remote.request

data class RegisterRequest(
    val email: String,
    val password: String,
    val connection : String
)