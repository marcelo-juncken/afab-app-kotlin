package feature_splash

import androidx.compose.material.Colors
import core.presentation.util.UiEvent
import core.util.Resource
import core.util.Screen
import feature_auth.domain.use_case.CheckLoggedInUserUseCase
import feature_auth.domain.use_case.LogoutUserUseCase
import feature_auth.domain.use_case.SaveDeviceUseCase
import feature_config.domain.use_case.LoadThemeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel(
    private val saveDeviceUseCase: SaveDeviceUseCase,
    private val checkLoggedInUserUseCase: CheckLoggedInUserUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val loadThemeUseCase: LoadThemeUseCase,
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val _splashEventFlow = MutableSharedFlow<SplashScreenEvent>()
    val splashEventFlow = _splashEventFlow.asSharedFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        launch(Dispatchers.IO) {
            loadTheme()
            saveDeviceUseCase()
            checkIfUserIsLoggedIn()
        }
    }

    private suspend fun loadTheme() {
        withContext(Dispatchers.Default){
            when (val loadedTheme = loadThemeUseCase()) {
                is Colors -> _splashEventFlow.emit(SplashScreenEvent.LoadTheme(loadedTheme))
                null -> Unit
            }
        }
    }

    private suspend fun checkIfUserIsLoggedIn() {
        when (checkLoggedInUserUseCase(updateLastLogin = true)) {
            is Resource.Success -> _eventFlow.emit(UiEvent.Navigate(Screen.PaymentsTypeListScreen.name))
            else -> {
                logoutUserUseCase()
                _eventFlow.emit(UiEvent.Navigate(Screen.LoginScreen.name))
            }
        }
    }
}