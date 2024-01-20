package feature_auth.presentation.register

sealed interface RegisterFormEvent {
    data class Submit(
        val firstName: String,
        val lastName: String,
        val email: String,
        val password: String,
        val confirmPassword: String,
    ) : RegisterFormEvent
}
