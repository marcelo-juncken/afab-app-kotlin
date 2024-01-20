package feature_payments.domain.use_case.template

import core.data.PaymentTemplateItem
import core.util.Resource
import core.util.SimpleResource
import feature_auth.data.local.DeviceIdProvider
import feature_auth.domain.repository.AppUserRepository
import feature_auth.domain.use_case.CheckLoggedInUserUseCase
import feature_payments.domain.repository.PaymentTemplateRepository


class DeletePaymentTemplateUseCase(
    private val appUserRepository: AppUserRepository,
    private val checkLoggedInUserUseCase: CheckLoggedInUserUseCase,
    private val PaymentTemplateRepository: PaymentTemplateRepository,
) {

    suspend operator fun invoke(
        template: PaymentTemplateItem,
    ): SimpleResource {

        val userLogged = checkLoggedInUserUseCase()
        if (userLogged is Resource.Error) return Resource.Error(errorMessage = userLogged.errorMessage)
        if (userLogged is Resource.Disconnect) return Resource.Disconnect()

        val currentDevice = DeviceIdProvider.currentDeviceId
        val userPublicKey = userLogged.data?.devices?.firstOrNull { it.deviceId == currentDevice }?.publicKey
            ?: return Resource.Error(errorMessage = "Erro ao obter chave pública.")

        val userResponse = appUserRepository.findUserByPublicKey(deviceId = currentDevice, publicKey = userPublicKey)
        if (userResponse is Resource.Error) return Resource.Error(errorMessage = userResponse.errorMessage)

        if (userResponse.data == null) return Resource.Error(errorMessage = "Usuário desconhecido.")

        if (userResponse.data._id != template.userId) return Resource.Error(errorMessage = "Você não tem permissão para deletar template de outro usuário.")

        return PaymentTemplateRepository.deletePaymentsTemplate(templateId = template._id)
    }
}