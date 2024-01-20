package feature_payments.domain.use_case.template

class ManageTemplatesUseCase(
    val savePaymentTemplateUseCase: SavePaymentTemplateUseCase,
    val loadPaymentTemplatesUseCase: LoadPaymentTemplatesUseCase,
    val editPaymentNameTemplateUseCase: EditPaymentNameTemplateUseCase,
    val deletePaymentTemplateUseCase: DeletePaymentTemplateUseCase,
)