package feature_auth.domain.use_case

import core.util.Resource
import core.util.SimpleResource
import feature_auth.data.dto.DomainAppUser
import feature_auth.data.local.DeviceIdProvider
import feature_auth.data.local.TokenStorage
import feature_auth.domain.repository.AppUserRepository
import feature_auth.domain.repository.AuthRepository
import feature_auth.presentation.util.getPublicKeyFromPrivateKey
import feature_auth.presentation.util.publicKeyToString
import feature_auth.presentation.util.stringToPublicKey

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val appUserRepository: AppUserRepository,
    private val tokenStorage: TokenStorage,
) {
    suspend operator fun invoke(): SimpleResource {

        val user = getUserResult() ?: return Resource.Error("Erro ao carregar dados do usuário.")

        if (!user.isActive) return Resource.Error("Acesso não permitido.")

        val currentDeviceId = DeviceIdProvider.currentDeviceId
        if (!isCurrentDeviceValid(user, currentDeviceId)) {
            return Resource.Error("Você atingiu o número máximo de dispositivos.")
        }

        val publicKey = getPublicKey(user, currentDeviceId)
        val updatedDevices = DeviceIdProvider.updateDeviceList(user.devices, publicKey).let { result ->
            if (result is Resource.Error) return Resource.failed(result.errorMessage)
            result.data
        } ?: return Resource.failed("Erro ao sincronizar dispositivo.")

        appUserRepository.updateUserFieldsById(
            user._id,
            DomainAppUser::devices to updatedDevices,
            DomainAppUser::accessCount to user.accessCount + 1
        )
        return Resource.succeeded
    }

    private suspend fun getUserResult(): DomainAppUser? {
        val auth0LoginResult = authRepository.loginUserWithPkce()
        if (auth0LoginResult is Resource.Error) return null

        val auth0Id = auth0LoginResult.data ?: return null

        val appUserResult = appUserRepository.findUserByAuth0Id(auth0Id)
        if (appUserResult is Resource.Error) return null

        return appUserResult.data
    }

    private fun isCurrentDeviceValid(user: DomainAppUser, currentDeviceId: String): Boolean {
        val deviceCount = user.devices.size
        val hasCurrentDevice = user.devices.any { it.deviceId == currentDeviceId }
        return deviceCount < 2 || hasCurrentDevice
    }

    private fun getPublicKey(user: DomainAppUser, currentDeviceId: String): String {
        val storedPublicKeyString = user.devices.firstOrNull { it.deviceId == currentDeviceId }?.publicKey ?: ""
        if (!tokenStorage.hasPrivateKey() || storedPublicKeyString.isBlank()) return generateAndSaveNewKeyPair()

        val privateKey = tokenStorage.loadPrivateKey()
        val publicKeyFromPrivateKey = getPublicKeyFromPrivateKey(privateKey)
        val storedPublicKey = stringToPublicKey(storedPublicKeyString)
        return if (storedPublicKey == publicKeyFromPrivateKey) storedPublicKeyString else generateAndSaveNewKeyPair()
    }

    private fun generateAndSaveNewKeyPair(): String {
        val newKeyPair = tokenStorage.generateRSAKeyPair()
        tokenStorage.saveEncryptedPrivateKey(newKeyPair.private)
        return newKeyPair.public.publicKeyToString()
    }
}