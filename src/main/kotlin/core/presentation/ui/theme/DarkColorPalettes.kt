package core.presentation.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color

enum class DarkColorPalettes(
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val secondaryVariant: Color = darkColors().secondaryVariant,
    val background: Color,
    val surface: Color,
    val error: Color = darkColors().error,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onError: Color = darkColors().onError,
) {

    DarkDeepBlueOrangePalette(
        primary = Color(0xFF1565C0),
        primaryVariant = Color(0xFF003C8F),
        secondary = Color(0xFFFFA726),
        background = Color(0xFF212121),
        surface = Color(0xFF424242),
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    ),

    DarkForestGreenPinkPalette(
        primary = Color(0xFF4CAF50),
        primaryVariant = Color(0xFF087F23),
        secondary = Color(0xFFFF80AB),
        background = Color(0xFF212121),
        surface = Color(0xFF424242),
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    ),

    DarkPinkBrownPalette(
        primary = Color(0xFFE91E63),
        primaryVariant = Color(0xFF880E4F),
        onPrimary = Color.White,
        secondary = Color(0xFF009688),
        secondaryVariant = Color(0xFF00796B),
        onSecondary = Color.White,
        background = Color(0xFF121212),
        onBackground = Color.White,
        surface = Color(0xFF1E1E1E),
        onSurface = Color.White,
        error = Color(0xFFFF9800)
    ),

    DarkLightPinkBrownPalette(
        primary = Color(0xFFF06292),
        primaryVariant = Color(0xFFBA2D65),
        onPrimary = Color.White,
        secondary = Color(0xFF009688),
        secondaryVariant = Color(0xFF00796B),
        onSecondary = Color.White,
        background = Color(0xFF121212),
        onBackground = Color.White,
        surface = Color(0xFF1E1E1E),
        onSurface = Color.White,
        error = Color(0xFFFF5252)
    ),


    DarkDarkCyanAmberPalette(
        primary = Color(0xFF009688),
        primaryVariant = Color(0xFF00695C),
        secondary = Color(0xFFFFC107),
        background = Color(0xFF212121),
        surface = Color(0xFF424242),
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    ),

    DarkMaroonLimePalette(
        primary = Color(0xFF880E4F),
        primaryVariant = Color(0xFF560027),
        secondary = Color(0xFFCDDC39),
        background = Color(0xFF212121),
        surface = Color(0xFF424242),
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    ),

    DarkDeepPurpleTealPalette2(
        primary = Color(0xFF673AB6),
        primaryVariant = Color(0xFF512DA8),
        onPrimary = Color.White,
        secondary = Color(0xFF009688),
        secondaryVariant = Color(0xFF00796B),
        onSecondary = Color.White,
        background = Color(0xFF121212),
        onBackground = Color.White,
        surface = Color(0xFF1E1E1E),
        onSurface = Color.White
    ),

    DarkCyanTealAmberPalette(
        primary = Color(0xFF00BCD4),
        primaryVariant = Color(0xFF0097A7),
        secondary = Color(0xFFFFC107),
        background = Color(0xFF121212),
        surface = Color(0xFF1D1D1D),
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    ),

    DarkAmberLightBluePalette(
        primary = Color(0xFFFFC107),
        primaryVariant = Color(0xFFFFA000),
        onPrimary = Color.Black,
        secondary = Color(0xFF03A9F4),
        secondaryVariant = Color(0xFF0288D1),
        onSecondary = Color.White,
        background = Color(0xFF121212),
        onBackground = Color.White,
        surface = Color(0xFF1E1E1E),
        onSurface = Color.White
    ),

    DarkPurpleColorPalette(
        primary = Color(0xFFBB86FC),
        background = Color(0xFF121212),
        onBackground = Color(0xFFFFFFFF),
        onPrimary = Color(0xFF000000),
        surface = Color(0xFF1F1F1F),
        onSurface = Color(0xFFFFFFFF),
        primaryVariant = Color(0xFF3700B3),
        secondary = Color(0xFF03DAC6),
        secondaryVariant = Color(0xFF018786),
        onSecondary = Color(0xFF000000),
    ),

    DarkYellishColorPalette(
        primary = Color(0xFFFFA726), // Orange
        background = Color(0xFF121212), // Dark gray background
        onBackground = Color(0xFFFFFFFF), // White text on the dark gray background
        onPrimary = Color(0xFF000000), // Black text on the orange primary color
        surface = Color(0xFF1E1E1E), // Slightly lighter gray for surfaces
        onSurface = Color(0xFFFFFFFF), // White text on the surface color
        primaryVariant = Color(0xFFFF7043), // Slightly darker orange as primary variant
        secondary = Color(0xFF8C9EFF), // A complementary blue color as secondary
        secondaryVariant = Color(0xFF536DFE), // Slightly darker blue as secondary variant
        onSecondary = Color(0xFF000000) // Black text on the secondary color
    ),

    DarkOrangeColorPalette(
        primary = Orange,
        background = DarkBg,
        onBackground = Orange,
        onPrimary = DarkBg,
        surface = LightBg,
        onSurface = Color(0xFFFFFFFF),
        primaryVariant = OrangeVeryLight,
        secondary = Color(0xFFFFA726),
        secondaryVariant = Color(0xFF536DFE),
        onSecondary = DarkBg,
        error = Color(0xFFe63946)
    ),

    DarkTealPalette(
        primary = Color(0xFF005F6B),
        primaryVariant = Color(0xFF00363B),
        secondary = Color(0xFF00B4A9),
        background = Color(0xFF1c1e1f),
        surface = Color(0xFF434547),
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White,
        onError = Color.White
    ),
    DarkBlueGrayPalette(
        primary = Color(0xFF808080),
        primaryVariant = Color(0xFF292B2D),
        secondary = Color(0xFF00B4A9),
        background = Color(0xFF1c1e1f),
        surface = Color(0xFF434547),
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White,
        onError = Color.White
    ),

    DarkRedGrayPinkPalette(
        primary = Color(0xFFE53935),
        primaryVariant = Color(0xFFB71C1C),
        secondary = Color(0xFFFF80AB),
        background = Color(0xFF212121),
        surface = Color(0xFF424242),
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    ),
    DarkPurpleYellowBluePalette(
        primary = Color(color = 0xFFAF7AC5),
        primaryVariant = Color(0xFF8E44AD),
        secondary = Color(0xFFF4D03F),
        secondaryVariant = Color(0xFFD4AC0D),
        background = Color(0xFF2E4053),
        surface = Color(0xFF283747),
        error = Color(0xFFe63946),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White,
        onError = Color.White
    ),

    DarkBlueCoralRedPalette(
        primary = Color(color = 0xFF3498DB),
        primaryVariant = Color(0xFF2874A6),
        secondary = Color(0xFFFFA07A),
        secondaryVariant = Color(0xFFF08080),
        background = Color(0xFF2C3E50),
        surface = Color(0xFF34495E),
        error = Color(0xFFe63946),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White,
        onError = Color.White
    ),

    DarkBluePurpleRedPalette(
        primary = Color(color = 0xFF5DADE2),
        primaryVariant = Color(0xFF2E86C1),
        secondary = Color(0xFFBB8FCE),
        secondaryVariant = Color(0xFF7D3C98),
        background = Color(0xFF34495E),
        surface = Color(0xFF2C3E50),
        error = Color(0xFFe63946),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White,
        onError = Color.White
    ),

    ;

    fun toColor(): Colors {
        return darkColors(
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
