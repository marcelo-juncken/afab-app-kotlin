package core.data

import core.util.Resource
import feature_payments.presentation.util.InputError

data class PaymentTemplateResult(
    val nameError: InputError? = null,
    val saveTemplateResult: Resource<PaymentTemplateItem>? = null
)