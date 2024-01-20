package core.presentation.components

import androidx.compose.foundation.ScrollState
import androidx.compose.material.Colors
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import core.presentation.ui.theme.DarkColorPalettes
import core.presentation.ui.theme.LightColorPalettes
import core.util.Screen
import feature_auth.presentation.login.LoginScreen
import feature_auth.presentation.login.LoginViewModel
import feature_auth.presentation.register.RegisterViewModel
import feature_config.presentation.ConfigScreen
import feature_config.presentation.DevicesViewModel
import feature_config.presentation.SettingsViewModel
import feature_payments.presentation.PaymentsTypeListScreen
import feature_payments.presentation.PaymentsViewModel
import feature_splash.SplashScreen
import org.koin.java.KoinJavaComponent.inject


@Composable
fun Navigation(
    navController: NavController,
    scaffoldState: ScaffoldState,
    scrollState: ScrollState,
    paymentsViewModel: PaymentsViewModel,
    settingsViewModel: SettingsViewModel,
    onLoadedTheme: (Colors) -> Unit,
    onPaletteSelected: (Colors) -> Unit,
) {

    NavigationHost(
        navController = navController,
    ) {
        composable(Screen.SplashScreen.name) {
            SplashScreen(
                onLoadedTheme = onLoadedTheme,
                onNavigate = {
                    navController.navigate(it)
                    navController.removeStacks()
                }
            )
        }

        composable(Screen.LoginScreen.name) {
            LoginScreen(
                scaffoldState = scaffoldState,
                scrollState = scrollState,
                onLogin = {
                    settingsViewModel.checkIfUserIsAdmin()
                    paymentsViewModel.getAllTemplates()
                    navController.navigate(Screen.PaymentsTypeListScreen.name)
                    navController.removeStacks()
                }
            )
        }

        composable(Screen.PaymentsTypeListScreen.name) {
            PaymentsTypeListScreen(
                viewModel = paymentsViewModel,
                scaffoldState = scaffoldState,
                scrollState = scrollState,
                onLogOut = {
                    navController.navigate(Screen.LoginScreen.name)
                    navController.removeStacks()
                }
            )
        }

        composable(Screen.ConfigScreen.name) {
            ConfigScreen(
                scaffoldState = scaffoldState,
                scrollState = scrollState,
                settingsViewModel = settingsViewModel,
                onPaletteSelected = { onPaletteSelected(it) },
                onLogOut = {
                    navController.navigate(Screen.LoginScreen.name)
                    navController.removeStacks()
                }
            )
        }

    }.build()
}