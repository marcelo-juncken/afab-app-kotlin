package feature_splash

import androidx.compose.material.Colors

sealed interface SplashScreenEvent {
    data class LoadTheme(val theme: Colors) : SplashScreenEvent
}

