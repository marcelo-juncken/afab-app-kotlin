package feature_auth.data.dto

import core.util.Constants.ZONE_ID
import java.time.LocalDateTime
import java.time.ZoneId

data class Device(
    val deviceId: String,
    val deviceName: String,
    var publicKey: String?,
    val accessCount: Long = 0L,
    val lastLogin: LocalDateTime = LocalDateTime.now(ZoneId.of(ZONE_ID)),
    val addedDate: LocalDateTime,
)