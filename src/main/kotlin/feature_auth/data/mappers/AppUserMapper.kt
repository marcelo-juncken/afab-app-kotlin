package feature_auth.data.mappers

import feature_auth.data.dto.DomainAppUser
import feature_auth.domain.models.UserDisplayInfo

fun DomainAppUser.toAppUserDisplay(): UserDisplayInfo {
    return UserDisplayInfo(
        id = _id,
        name = "$firstname $lastname",
        email = email,
        accessCount = accessCount,
        devices = devices.map { it.toDeviceDisplay() }.toMutableList(),
        profilePictureUrl = profilePictureUrl,
        isActive = isActive
    )
}