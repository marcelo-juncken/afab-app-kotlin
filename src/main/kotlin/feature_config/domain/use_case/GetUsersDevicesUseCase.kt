package feature_config.domain.use_case

import core.util.Resource
import feature_auth.domain.models.UserDisplayInfo
import feature_auth.domain.repository.AppUserRepository

class GetUsersDevicesUseCase(
    private val appUserRepository: AppUserRepository,
) {
    suspend operator fun invoke(): Resource<List<UserDisplayInfo>> {
        return appUserRepository.getAllUsers()
    }
}