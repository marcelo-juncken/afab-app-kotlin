package feature_auth.presentation.login

data class LoginFormState(
    val emailError: Error? = null,
    val passwordError: Error? = null,
)