package feature_auth.presentation.register.models

data class RequestUser(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
)