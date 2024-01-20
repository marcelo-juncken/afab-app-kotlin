package core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import core.presentation.ui.theme.Dimensions.SpaceSmall

@Composable
fun RowScope.StandardBottomNavItem(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    contentDescription: String? = null,
    selected: (Boolean) = false,
    selectedColor: Color = MaterialTheme.colors.primary,
    unselectedColor: Color = MaterialTheme.colors.onSurface,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {

    BottomNavigationItem(
        modifier = modifier.background(MaterialTheme.colors.surface),
        selected = selected,
        onClick = onClick,
        icon = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceSmall),
                contentAlignment = Alignment.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                    )
                }
            }
        },
        selectedContentColor = selectedColor,
        unselectedContentColor = unselectedColor,
        enabled = enabled
    )
}