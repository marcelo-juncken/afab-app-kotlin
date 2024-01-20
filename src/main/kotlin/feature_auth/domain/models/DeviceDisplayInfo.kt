package feature_auth.domain.models

import core.util.Constants
import java.time.LocalDateTime
import java.time.ZoneId

data class DeviceDisplayInfo(
    val deviceId: String,
    val deviceName: String,
    val accessCount : Long,
    val lastLogin: LocalDateTime = LocalDateTime.now(ZoneId.of(Constants.ZONE_ID)),
    val addedDate: LocalDateTime,
)