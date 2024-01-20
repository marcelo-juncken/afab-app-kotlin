package feature_auth.presentation.register.models

import core.util.SimpleResource
import feature_auth.presentation.util.AuthError

data class RegisterResult(
    val firstNameError: AuthError? = null,
    val lastNameError: AuthError? = null,
    val emailError: AuthError? = null,
    val passwordError: AuthError? = null,
    val confirmPasswordError: AuthError? = null,
    val result: SimpleResource? = null,
)
