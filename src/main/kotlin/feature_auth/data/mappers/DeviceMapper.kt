package feature_auth.data.mappers

import feature_auth.data.dto.Device
import feature_auth.data.dto.DomainAppUser
import feature_auth.domain.models.DeviceDisplayInfo


fun Device.toDeviceDisplay(): DeviceDisplayInfo {
    return DeviceDisplayInfo(
        deviceId = deviceId,
        deviceName = deviceName,
        accessCount = accessCount,
        lastLogin = lastLogin,
        addedDate = addedDate
    )
}

