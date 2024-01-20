package feature_auth.data.remote.response

data class RegisterResponse(
    val _id: String,
    val email: String,
    val email_verified: Boolean,
    val created_at: String
)