package feature_payments.domain.use_case.template

import core.data.PaymentTemplateItem
import core.data.PaymentTemplateResult
import core.data.SavedTemplatesType
import core.util.Resource
import core.util.ValidationUtil
import feature_auth.data.local.DeviceIdProvider
import feature_auth.domain.repository.AppUserRepository
import feature_auth.domain.use_case.CheckLoggedInUserUseCase
import feature_payments.domain.repository.PaymentTemplateRepository


class SavePaymentTemplateUseCase(
    private val appUserRepository: AppUserRepository,
    private val checkLoggedInUserUseCase: CheckLoggedInUserUseCase,
    private val PaymentTemplateRepository: PaymentTemplateRepository,
) {

    suspend operator fun invoke(
        templateName: String,
        savedStates: Map<String, Any>,
    ): PaymentTemplateResult {

        val templateNameError = ValidationUtil.validateTemplateName(templateName = templateName)
        if (templateNameError != null) return PaymentTemplateResult(nameError = templateNameError)

        val userLogged = checkLoggedInUserUseCase()
        if (userLogged is Resource.Error) return PaymentTemplateResult(saveTemplateResult = Resource.Error(errorMessage = userLogged.errorMessage))
        if (userLogged is Resource.Disconnect) return PaymentTemplateResult(saveTemplateResult = Resource.Disconnect())

        val currentDevice = DeviceIdProvider.currentDeviceId
        val userPublicKey = userLogged.data?.devices?.firstOrNull { it.deviceId == currentDevice }?.publicKey
            ?: return PaymentTemplateResult(saveTemplateResult = Resource.Error("Erro ao obter chave pública"))

        val userResponse = appUserRepository.findUserByPublicKey(deviceId = currentDevice, publicKey = userPublicKey)
        if (userResponse is Resource.Error) return PaymentTemplateResult(
            saveTemplateResult = Resource.Error(
                userResponse.errorMessage
            )
        )

        if (userResponse.data == null)
            return PaymentTemplateResult(saveTemplateResult = Resource.Error(errorMessage = "Usuário desconhecido"))

        val template = PaymentTemplateItem(
            userId = userResponse.data._id,
            templateName = templateName,
            savedStates = savedStates,
            templateType = SavedTemplatesType.PAYMENT.name,
        )

        val saveTemplateResult = PaymentTemplateRepository.upsertPaymentsTemplate(template = template)
        if (saveTemplateResult is Resource.Error) return PaymentTemplateResult(
            saveTemplateResult = Resource.Error(
                errorMessage = saveTemplateResult.errorMessage
            )
        )

        return PaymentTemplateResult(saveTemplateResult = Resource.Success(template))
    }
}