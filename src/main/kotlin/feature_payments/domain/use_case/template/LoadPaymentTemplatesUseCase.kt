package feature_payments.domain.use_case.template

import core.data.PaymentTemplateItem
import core.data.PaymentTemplateResult
import core.util.Resource
import feature_auth.data.local.DeviceIdProvider
import feature_auth.domain.repository.AppUserRepository
import feature_auth.domain.use_case.CheckLoggedInUserUseCase
import feature_payments.domain.repository.PaymentTemplateRepository

class LoadPaymentTemplatesUseCase(
    private val appUserRepository: AppUserRepository,
    private val checkLoggedInUserUseCase: CheckLoggedInUserUseCase,
    private val PaymentTemplateRepository: PaymentTemplateRepository,
) {
    suspend operator fun invoke(): Resource<List<PaymentTemplateItem>> {
        val userLogged = checkLoggedInUserUseCase()
        if (userLogged is Resource.Error) return Resource.Error(errorMessage = userLogged.errorMessage)
        if (userLogged is Resource.Disconnect) return Resource.Disconnect()

        val currentDevice = DeviceIdProvider.currentDeviceId
        val userPublicKey = userLogged.data?.devices?.firstOrNull { it.deviceId == currentDevice }?.publicKey
            ?: return Resource.Error(errorMessage = "Erro ao obter chave pública.")

        val userResponse = appUserRepository.findUserByPublicKey(deviceId = currentDevice, publicKey = userPublicKey)

        if (userResponse is Resource.Error) return (Resource.Error(errorMessage = userResponse.errorMessage))

        if (userResponse.data == null) return Resource.Error(errorMessage = "Usuário desconhecido")

        return PaymentTemplateRepository.getUserTemplates(userId = userResponse.data._id)
    }
}
