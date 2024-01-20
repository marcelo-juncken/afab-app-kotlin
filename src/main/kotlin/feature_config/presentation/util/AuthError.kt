package feature_config.presentation.util

sealed class AuthError : Error() {
    object FieldEmpty : AuthError()
    object InputTooShort: AuthError()
    object InputTooLong: AuthError()
    object InvalidEmail : AuthError()
    object InvalidPassword : AuthError()
    object InvalidPasswordMatch: AuthError()
}

