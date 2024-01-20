package feature_auth.presentation.util

import core.util.Error

sealed class AuthError : Error() {
    object FieldEmpty : AuthError()

    object InvalidName : AuthError()
    object NameTooShort : AuthError()
    object NameTooLong : AuthError()

    object InvalidEmail : AuthError()

    object PasswordTooShort: AuthError()
    object PasswordTooLong: AuthError()
    object InvalidPassword : AuthError()
    object InvalidPasswordMatch: AuthError()
}

