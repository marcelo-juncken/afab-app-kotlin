package feature_auth.domain.use_case

import feature_auth.data.local.DeviceIdProvider
import feature_auth.data.local.TokenStorage
import feature_auth.domain.repository.AppUserRepository
import feature_auth.presentation.util.getPublicKeyFromPrivateKey
import feature_auth.presentation.util.publicKeyToString
import java.security.PrivateKey

class CheckAdminRoleUseCase(
    private val appUserRepository: AppUserRepository,
    private val tokenStorage: TokenStorage,
) {
    suspend operator fun invoke(): Boolean {
        return try {
            val privateKey: PrivateKey? = tokenStorage.loadPrivateKey()
            val publicKeyFromPrivateKey = getPublicKeyFromPrivateKey(privateKey = privateKey)

            val deviceId = DeviceIdProvider.currentDeviceId
            val userResponse = appUserRepository.findUserByPublicKey(
                deviceId = deviceId,
                publicKey = publicKeyFromPrivateKey.publicKeyToString()
            )

            userResponse.data?.roles?.contains("ADMIN") == true
        } catch (e: Exception) {
            false
        }
    }
}