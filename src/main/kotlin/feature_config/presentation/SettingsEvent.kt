package feature_config.presentation

import androidx.compose.material.Colors

sealed interface SettingsEvent {
    data class ChangeTheme(val themeSelected: Colors) : SettingsEvent
    object Logout : SettingsEvent
}