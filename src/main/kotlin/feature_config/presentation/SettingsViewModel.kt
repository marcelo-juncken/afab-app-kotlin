package feature_config.presentation

import androidx.compose.ui.graphics.Color
import core.presentation.ui.theme.DarkColorPalettes
import core.presentation.ui.theme.LightColorPalettes
import core.presentation.util.UiEvent
import feature_auth.domain.use_case.CheckAdminRoleUseCase
import feature_auth.domain.use_case.LogoutUserUseCase
import feature_config.domain.use_case.SaveThemeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val logoutUserUseCase: LogoutUserUseCase,
    private val checkAdminRoleUseCase: CheckAdminRoleUseCase,
    private val saveThemeUseCase: SaveThemeUseCase,
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val _isAdminState = MutableStateFlow(false)
    val isAdminState = _isAdminState.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _lightColorPalettesState = MutableStateFlow(LightColorPalettes.values().sortedBy { it.primary.getHue() })
    val lightColorPalettesState = _lightColorPalettesState.asStateFlow()

    private val _darkColorPalettesState = MutableStateFlow(DarkColorPalettes.values().sortedBy { it.primary.getHue() })
    val darkColorPalettesState = _darkColorPalettesState.asStateFlow()

    init {
        checkIfUserIsAdmin()
    }

    fun checkIfUserIsAdmin() {
        launch(Dispatchers.IO) {
            _loadingState.update { true }

            _isAdminState.update { checkAdminRoleUseCase() }

            _loadingState.update { false }
        }
    }

    fun onEvent(event: SettingsEvent) {
        launch(Dispatchers.IO) {
            when (event) {
                SettingsEvent.Logout -> {
                    logoutUserUseCase()
                    _eventFlow.emit(UiEvent.OnLogout)
                }
                is SettingsEvent.ChangeTheme -> {
                    saveThemeUseCase(event.themeSelected)
                }
            }
        }
    }
}

fun Color.getHue(): Float {
    val hsb =
        java.awt.Color.RGBtoHSB((this.red * 255).toInt(), (this.green * 255).toInt(), (this.blue * 255).toInt(), null)
    return hsb[0]
}