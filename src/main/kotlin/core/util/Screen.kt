package core.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(val label: String, val icon: ImageVector?) {
    SplashScreen(label = "Splash Screen", icon = null),
    LoginScreen(label = "Login", icon = null),
    PaymentsTypeListScreen(label = "Home", icon = Icons.Outlined.List),
    ConfigScreen(label = "Settings", icon = Icons.Outlined.Person),
}