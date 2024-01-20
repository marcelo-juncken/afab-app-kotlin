package feature_config.domain.use_case

import androidx.compose.material.Colors
import core.presentation.ui.theme.DarkColorPalettes
import core.presentation.ui.theme.LightColorPalettes
import feature_auth.data.local.TokenStorage

class SaveThemeUseCase(
    private val tokenStorage: TokenStorage,
) {
    suspend operator fun invoke(
        theme: Colors,
    ) {
        val lightColorPalette =
            LightColorPalettes.values().firstOrNull {
                it.toColor().primary == theme.primary && it.toColor().onPrimary == theme.onPrimary && it.toColor().isLight == theme.isLight
            }?.name

        lightColorPalette?.let { palette ->
            tokenStorage.saveTheme(palette)
            return
        }

        val darkColorPalette =
            DarkColorPalettes.values().firstOrNull {
                it.toColor().primary == theme.primary && it.toColor().onPrimary == theme.onPrimary && it.toColor().isLight == theme.isLight
            }?.name

        darkColorPalette?.let { palette ->
            tokenStorage.saveTheme(palette)
            return
        }
    }
}
