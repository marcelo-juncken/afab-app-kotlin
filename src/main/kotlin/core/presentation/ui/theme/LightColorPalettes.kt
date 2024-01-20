package core.presentation.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

enum class LightColorPalettes(
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val secondaryVariant: Color = lightColors().secondaryVariant,
    val background: Color,
    val surface: Color,
    val error: Color = lightColors().error,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onError: Color = lightColors().onError,
) {

    LightPupleGreenPalette(
        primary = Color(0xFF8E44AD),
        primaryVariant = Color(0xFF5B2C6F),
        secondary = Color(0xFF27AE60),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.Black,
        onSurface = Color.Black,
    ),

    LightYellowBluePalette(
        primary = Color(0xFF2C3E50),
        primaryVariant = Color(0xFF1B2631),
        secondary = Color(0xFFF1C40F),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black
    ),

    LightRedBluePalette(
        primary = Color(0xFFD90429),
        primaryVariant = Color(0xFF9B1C1C),
        secondary = Color(0xFFAA0078),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.Black,
        onSurface = Color.Black
    ),

    LightOrangeGrayPalette(
        primary = Color(0xFFE67E22),
        primaryVariant = Color(0xFFA84300),
        secondary = Color(0xFF95A5A6),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black
    ),

    LightPinkBrownPalette(
        primary = Color(0xFFE91E63),
        primaryVariant = Color(0xFF880E4F),
        secondary = Color(0xFF795548),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.Black,
        onSurface = Color.Black
    ),

    LightLightBlueOrangePalette(
        primary = Color(0xFF03A9F4),
        primaryVariant = Color(0xFF007AC1),
        secondary = Color(0xFFFF9800),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black
    ),

    LightIndigoTealPalette(
        primary = Color(0xFF3F51B5),
        primaryVariant = Color(0xFF002984),
        secondary = Color(0xFF009688),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black
    ),

    LightCyanAmberPalette(
        primary = Color(0xFF00BCD4),
        primaryVariant = Color(0xFF008394),
        secondary = Color(0xFFFFC107),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black
    ),

    LightGreenPurplePalette(
        primary = Color(0xFF4CAF50),
        primaryVariant = Color(0xFF388E3C),
        onPrimary = Color.White,
        secondary = Color(0xFF9C27B0),
        secondaryVariant = Color(0xFF7B1FA2),
        onSecondary = Color.White,
        background = Color.White,
        onBackground = Color.Black,
        surface = Color.White,
        onSurface = Color.Black
    ),

    LightOrangePalette(
        primary = Color(0xFFf67e7d),
        primaryVariant = Color(0xFFc34a36),
        secondary = Color(0xFF9ddfd3),
        secondaryVariant = Color(0xFF6cacb7),
        background = Color(0xFFf0f0f0),
        surface = Color(0xFFf0f0f0),
        error = Color(0xFFe63946),
        onPrimary = Color.White,
        onSecondary = Color(0xFF1A1821),
        onBackground = Color(0xFF1A1821),
        onSurface = Color(0xFF1A1821),
        onError = Color(0xFFf0f0f0)
    ),
    LightBlueGrayPalette(
        primary = Color(0xFF607D8B),
        primaryVariant = Color(0xFF455A64),
        secondary = Color(0xFFCDDC39),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black
    ),
    LightDeepOrangePalette(
        primary = Color(0xFFFF5722),
        primaryVariant = Color(0xFFE64A19),
        secondary = Color(0xFFE91E63),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.Black,
        onSurface = Color.Black
    ),
    LightTealGreenPalette(
        primary = Color(0xFF009688),
        primaryVariant = Color(0xFF00796B),
        secondary = Color(0xFFCDDC39),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black
    ),
    LightDarkPurplePalette(
        primary = Color(0xFF673AB7),
        primaryVariant = Color(0xFF512DA8),
        secondary = Color(0xFFFFC107),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black
    ),

    LightBlackGoldPalette(
        primary = Color(0xFFFFC107),
        primaryVariant = Color(0xFFC79100),
        secondary = Color(0xFF424242),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.Black,
        onSurface = Color.Black
    ),

    LightDarkBluePalette(
        primary = Color(0xFF173F5F),
        primaryVariant = Color(0xFF0F2C3C),
        secondary = Color(0xFFF4D35E),
        background = Color(0xFFF4F4F4),
        surface = Color(0xFFFFFFFF),
        onPrimary = Color(0xFFFFFFFF),
        onSecondary = Color(0xFF000000),
        onBackground = Color(0xFF000000),
        onSurface = Color(0xFF000000)
    ),

    EarthyTonesPalette(
        primary = Color(0xFF4A4E4D),
        primaryVariant = Color(0xFFA8A39C),
        secondary = Color(0xFFB0B2B2),
        secondaryVariant = Color(0xFFD1CBC4),
        background = Color(0xFFF7F7F7),
        surface = Color(0xFFFFFFFF),
        error = Color(0xFFC75B39),
        onPrimary = Color(0xFFFFFFFF),
        onSecondary = Color(0xFFFFFFFF),
        onBackground = Color(0xFF212121),
        onSurface = Color(0xFF212121),
        onError = Color(0xFFFFFFFF)
    ),
    BlackAndWhitePalette(
        primary = Color.Black,
        primaryVariant = Color.DarkGray,
        secondary = Color.White,
        secondaryVariant = Color.LightGray,
        background = Color.White,
        surface = Color.White,
        error = Color.Red,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black,
        onError = Color.White
    )

    ;

    fun toColor(): Colors {
        return lightColors(
            primary,
            primaryVariant,
            secondary,
            secondaryVariant,
            background,
            surface,
            error,
            onPrimary,
            onSecondary,
            onBackground,
            onSurface,
            onError
        )
    }
}
