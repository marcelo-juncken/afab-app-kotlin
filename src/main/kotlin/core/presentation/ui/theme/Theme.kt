package core.presentation.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun CustomTheme(themeSelected: Colors, content: @Composable () -> Unit) {
    MaterialTheme(
        colors = themeSelected,
        shapes = Shapes,
        content = content,
        typography = Typography
    )
}