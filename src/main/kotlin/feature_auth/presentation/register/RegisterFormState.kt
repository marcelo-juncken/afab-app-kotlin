package feature_auth.presentation.register

import core.util.Error
import core.util.StringResources

data class RegisterFormState(
    val firstNameError: Error? = null,
    val lastNameError: Error? = null,
    val emailError: Error? = null,
    val passwordError: Error? = null,
    val confirmPasswordError: Error? = null,
) {
    fun getFieldError(hint: String): Error? {
        return when (hint) {
            StringResources.HINT_FIELD_FIRST_NAME -> firstNameError
            StringResources.HINT_FIELD_LAST_NAME -> lastNameError
            StringResources.HINT_FIELD_EMAIL -> emailError
            StringResources.HINT_FIELD_PASSWORD -> passwordError
            StringResources.HINT_FIELD_CONFIRM_PASSWORD -> confirmPasswordError
            else -> null
        }
    }
}