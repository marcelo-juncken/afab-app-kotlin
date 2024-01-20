package feature_config.presentation

import feature_auth.domain.models.UserDisplayInfo
import feature_auth.domain.models.DeviceDisplayInfo

sealed interface DevicesEvent {
    data class RemoveDevice(val selectedUser: UserDisplayInfo?, val device: DeviceDisplayInfo) : DevicesEvent
    object CancelRemoveDevice : DevicesEvent
    data class ToggleUserAccess(val selectedUser: UserDisplayInfo?) : DevicesEvent
}