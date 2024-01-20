package core.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import core.util.StringResources.GO_BACK

@Composable
fun StandardToolbar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.surface,
    showBackButton: Boolean = false,
    title: @Composable () -> Unit = {},
    onPopBackStack: () -> Unit = {},
    navActions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = backgroundColor,
        title = title,
        navigationIcon = {
            if (showBackButton) {
                IconButton(
                    onClick = onPopBackStack,
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = GO_BACK,
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            }
        },
        contentColor = Color.White,
        elevation = 0.dp,
        actions = navActions
    )
}