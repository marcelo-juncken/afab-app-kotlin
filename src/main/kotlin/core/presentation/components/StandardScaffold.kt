package core.presentation.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import core.util.Screen
import java.awt.Cursor

@Composable
fun StandardScaffold(
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    showBottomBar: Boolean = true,
    bottomNavItems: List<Screen> = Screen.values().toList(),
    selected: (String) -> Boolean,
    isFabVisible: Boolean,
    onFabClick: () -> Unit = {},
    onNavigation: (String) -> Unit = {},
    content: @Composable () -> Unit,

    ) {

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar)
                BottomAppBar(
                    cutoutShape = CircleShape,
                    elevation = 5.dp,
                    backgroundColor = MaterialTheme.colors.surface
                ) {
                    BottomNavigation {
                        bottomNavItems.forEach { bottomNavItem ->
                            if (bottomNavItem.icon == null) return@forEach
                            StandardBottomNavItem(
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))),
                                icon = bottomNavItem.icon,
                                contentDescription = bottomNavItem.label,
                                selected = selected(bottomNavItem.name),
                                enabled = true,
                                onClick = { onNavigation(bottomNavItem.name) }
                            )
                        }
                    }
                }
        },
        scaffoldState = scaffoldState,
        floatingActionButton = {
            if (isFabVisible) {
                FloatingActionButton(
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))),
                    onClick = onFabClick,
                    backgroundColor = MaterialTheme.colors.primary,
                    elevation = FloatingActionButtonDefaults.elevation(),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Exportar",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center
    ) {
        content()
    }
}