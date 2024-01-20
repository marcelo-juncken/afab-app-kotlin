package feature_auth.domain.use_case

import core.util.Resource
import feature_auth.data.dto.DomainAppUser
import feature_auth.data.local.DeviceIdProvider
import feature_auth.data.local.DeviceIdProvider.updateDeviceList
import feature_auth.data.local.TokenStorage
import feature_auth.domain.repository.AppUserRepository
import feature_auth.presentation.util.getPublicKeyFromPrivateKey
import feature_auth.presentation.util.publicKeyToString

class CheckLoggedInUserUseCase(
    private val appUserRepository: AppUserRepository,
    private val tokenStorage: TokenStorage,
) {
    suspend operator fun invoke(updateLastLogin: Boolean = false): Resource<DomainAppUser?> {
        return try {
            if (!tokenStorage.hasPrivateKey()) return Resource.Disconnect()

            if (!tokenStorage.isTimestampValid()) return Resource.Disconnect()

            val publicKey = getPublicKey()

            val deviceId = DeviceIdProvider.currentDeviceId
            val user = appUserRepository.findUserByPublicKey(deviceId = deviceId, publicKey = publicKey).let {
                if (it is Resource.Error) return Resource.Disconnect()
                it.data
            } ?: return Resource.Disconnect()

            if (!user.isActive) return Resource.Disconnect()

            if (updateLastLogin) {
                return updateLastLogin(user = user)
            }

            Resource.Success(data = user)
        } catch (e: Exception) {
            Resource.Disconnect()
        }
    }

    private fun getPublicKey(): String {
        val privateKey = tokenStorage.loadPrivateKey()
        val publicKey = getPublicKeyFromPrivateKey(privateKey = privateKey)
        return publicKey.publicKeyToString()
    }

    private suspend fun updateLastLogin(user: DomainAppUser): Resource<DomainAppUser?> {
        val device = updateDeviceList(devices = user.devices).let { result ->
            if (result is Resource.Error) return Resource.Error(errorMessage = result.errorMessage)
            result.data
        } ?: return Resource.Disconnect()

        val updateResult = appUserRepository.updateUserFieldsById(
            user._id,
            DomainAppUser::devices to device,
            DomainAppUser::accessCount to user.accessCount + 1
        )
        if (updateResult is Resource.Error) return Resource.Error(errorMessage = updateResult.errorMessage)

        val updatedUser = user.copy(devices = device, accessCount = user.accessCount + 1)
        return Resource.Success(data = updatedUser)
    }
}