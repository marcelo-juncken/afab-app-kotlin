package feature_payments.presentation.states

import core.util.Error

data class TextFieldFormState(
    val dtIni: String = "",
    val dtIniError: Error? = null,
    val dtEnd: String = "",
    val dtEndError: Error? = null,
    val dtCreated: String = "",
    val dtCreatedError: Error? = null,
    val job: String = "",
    val jobError: Error? = null,
)
