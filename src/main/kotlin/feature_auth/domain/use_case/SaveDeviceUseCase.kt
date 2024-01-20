package feature_auth.domain.use_case

import core.util.Constants.ZONE_ID
import feature_auth.data.local.DeviceIdProvider
import feature_auth.data.dto.Device
import feature_auth.domain.repository.AppUserRepository
import java.time.LocalDateTime
import java.time.ZoneId

class SaveDeviceUseCase(
    private val appUserRepository: AppUserRepository,
) {
    suspend operator fun invoke(): Boolean {
        return try {

            val device = Device(
                deviceId = DeviceIdProvider.currentDeviceId,
                deviceName = DeviceIdProvider.getDeviceName(),
                addedDate = LocalDateTime.now(ZoneId.of(ZONE_ID)),
                accessCount = 1L,
                publicKey = null
            )

            appUserRepository.upsertDevice(device)

            true
        } catch (e: Exception) {
            false
        }
    }
}