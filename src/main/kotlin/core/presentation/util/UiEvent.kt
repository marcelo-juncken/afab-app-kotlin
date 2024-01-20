package core.presentation.util

import androidx.compose.material.Colors
import androidx.compose.material.SnackbarDuration
import core.util.Event

sealed class UiEvent : Event() {
    data class ShowSnackbar(val textMessage: String, val duration: SnackbarDuration = SnackbarDuration.Short) : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    data class OpenUrl(val url: String) : UiEvent()
    data class ThemeSaved(val theme: Colors) : UiEvent()

    object OnLogin : UiEvent()
    object OnLogout : UiEvent()
}