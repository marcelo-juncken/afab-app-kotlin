package feature_auth.data.dto

import core.util.Constants.ZONE_ID
import feature_auth.data.dto.Device
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.time.ZoneId

data class DomainAppUser(
    @BsonId
    val _id: ObjectId = ObjectId(),
    val auth0UserId: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val devices: MutableList<Device> = mutableListOf(),
    val accessCount : Long = 0L,
    val createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of(ZONE_ID)),
    val updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.of(ZONE_ID)),
    val roles: Set<String> = emptySet(),
    val isActive: Boolean = true,
    val profilePictureUrl: String? = null,
)
