package feature_config.domain.use_case

import androidx.compose.material.Colors
import core.presentation.ui.theme.DarkColorPalettes
import core.presentation.ui.theme.LightColorPalettes
import feature_auth.data.local.TokenStorage

class LoadThemeUseCase(
    private val tokenStorage: TokenStorage,
) {
    suspend operator fun invoke(): Colors?{
        val theme = tokenStorage.loadTheme() ?: return null

        return if (LightColorPalettes.values().any{it.name == theme}) {
            LightColorPalettes.valueOf(theme).toColor()
        } else if (DarkColorPalettes.values().any{it.name == theme}) {
            DarkColorPalettes.valueOf(theme).toColor()
        } else {
            null
        }
    }
}