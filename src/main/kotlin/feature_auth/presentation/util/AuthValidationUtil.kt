package feature_auth.presentation.util

import core.util.Constants.MAX_NAME_LENGTH
import core.util.Constants.MAX_PASSWORD_LENGTH
import core.util.Constants.MIN_NAME_LENGTH
import core.util.Constants.MIN_PASSWORD_LENGTH

object AuthValidationUtil {

    fun validateName(name : String) : AuthError? {
        if(name.isBlank()) return AuthError.FieldEmpty

        if(name.length < MIN_NAME_LENGTH) return AuthError.NameTooShort
        if(name.length > MAX_NAME_LENGTH) return AuthError.NameTooLong

        val nameRegex = "^[\\p{L} .'-]+$".toRegex()
        if(!nameRegex.matches(name)) return AuthError.InvalidName

        return null
    }

    fun validateEmail(email: String): AuthError? {
        if (email.isBlank()) return AuthError.FieldEmpty

        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$".toRegex(RegexOption.IGNORE_CASE)
        if (!emailRegex.matches(email)) return AuthError.InvalidEmail

        return null
    }

    fun validatePassword(password: String): AuthError? {
        if (password.isBlank()) return AuthError.FieldEmpty

        if (password.length < MIN_PASSWORD_LENGTH) return AuthError.PasswordTooShort
        if (password.length > MAX_PASSWORD_LENGTH) return AuthError.PasswordTooLong

        val passwordPattern =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@!#$%^&+=])(?=\\S+$).{$MIN_PASSWORD_LENGTH,$MAX_PASSWORD_LENGTH}$".toRegex()

        if(!passwordPattern.matches(password)) return AuthError.InvalidPassword

        return null
    }

    fun validatePasswordMatches(password : String, confirmPassword : String) : AuthError? {
        if (password != confirmPassword) return AuthError.InvalidPasswordMatch
        return null
    }

}