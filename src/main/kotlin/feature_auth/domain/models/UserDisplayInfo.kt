package feature_auth.domain.models

import org.bson.types.ObjectId

data class UserDisplayInfo(
    val id: ObjectId,
    val name: String,
    val email: String,
    val accessCount: Long,
    val devices: MutableList<DeviceDisplayInfo>,
    val profilePictureUrl: String?,
    val isActive: Boolean,
)