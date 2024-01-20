package feature_config.domain.use_case

import core.util.Resource
import core.util.SimpleResource
import feature_auth.domain.models.UserDisplayInfo
import feature_auth.domain.models.DeviceDisplayInfo
import feature_auth.domain.repository.AppUserRepository
import feature_auth.domain.use_case.CheckLoggedInUserUseCase

class RemoveUserDeviceUseCase(
    private val appUserRepository: AppUserRepository,
    private val checkLoggedInUserUseCase: CheckLoggedInUserUseCase,
) {
    suspend operator fun invoke(
        user: UserDisplayInfo?,
        device: DeviceDisplayInfo
    ): SimpleResource {
        if(user == null) return Resource.Error("Usuário não encontrado.")

        val userLogged = checkLoggedInUserUseCase()
        if (userLogged is Resource.Error) return Resource.Error(errorMessage = userLogged.errorMessage)
        if (userLogged is Resource.Disconnect) return Resource.Disconnect()
        if (userLogged.data?.roles?.contains("ADMIN") != true) return Resource.Disconnect()

        return appUserRepository.removeDeviceById(user.id, device.deviceId)
    }
}