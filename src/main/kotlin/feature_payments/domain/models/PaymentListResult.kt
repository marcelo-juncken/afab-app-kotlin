package feature_payments.domain.models

import core.util.Resource
import feature_payments.presentation.util.InputError

data class PaymentListResult(
    val dtIniError: InputError? = null,
    val dtEndError: InputError? = null,
    val dtCreatedError: InputError? = null,
    val jobError: InputError? = null,
    val cbCreateSheetError: InputError? = null,
    val paymentResult: Resource<List<List<Any>>>? = null,
    val cashResult: Resource<List<List<Any>>>? = null
)