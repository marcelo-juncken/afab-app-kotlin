import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import core.presentation.components.Navigation
import core.presentation.components.StandardScaffold
import core.presentation.components.rememberNavController
import core.presentation.ui.theme.CustomTheme
import core.presentation.ui.theme.LightColorPalettes
import core.util.Constants.APP_NAME
import core.util.Constants.MINIMUM_HEIGHT
import core.util.Constants.MINIMUM_WIDTH
import core.util.Screen
import di.myModule
import feature_config.presentation.SettingsViewModel
import feature_payments.presentation.PaymentsFormEvent
import feature_payments.presentation.PaymentsViewModel
import org.koin.core.context.startKoin
import org.koin.fileProperties
import org.koin.java.KoinJavaComponent.inject
import java.awt.Dimension
import java.io.FileOutputStream
import java.io.PrintStream

@Composable
fun MyApp() {

    val navController by rememberNavController(startDestination = Screen.SplashScreen.name)
    val currentScreen by remember {
        navController.currentScreen
    }

    val settingsViewModel: SettingsViewModel by inject(SettingsViewModel::class.java)
    val paymentsViewModel: PaymentsViewModel by inject(PaymentsViewModel::class.java)

    val scaffoldState = rememberScaffoldState()
    val scrollState = rememberScrollState()

    val themeSelected = remember { mutableStateOf(LightColorPalettes.LightRedBluePalette.toColor()) }

    CustomTheme(themeSelected.value) {
        StandardScaffold(
            scaffoldState = scaffoldState,
            modifier = Modifier.fillMaxSize(),
            showBottomBar = currentScreen != Screen.SplashScreen.name && currentScreen != Screen.LoginScreen.name,
            selected = { currentScreen == it },
            isFabVisible = currentScreen == Screen.PaymentsTypeListScreen.name,
            onFabClick = {
                if (currentScreen == Screen.PaymentsTypeListScreen.name) paymentsViewModel.onEvent(PaymentsFormEvent.Submit)
            },
            onNavigation = {
                navController.navigate(it)
            }
        ) {
            Navigation(
                navController = navController,
                scaffoldState = scaffoldState,
                scrollState = scrollState,
                paymentsViewModel = paymentsViewModel,
                settingsViewModel = settingsViewModel,
                onLoadedTheme = { themeSelected.value = it },
                onPaletteSelected = { themeSelected.value = it })
        }
    }
}

fun main() = application {

    System.setOut(PrintStream(FileOutputStream("app_log.txt"), true))
    System.setErr(PrintStream(FileOutputStream("app_error_log.txt"), true))

    startKoin {
        fileProperties("/auth0.properties")
        fileProperties("/mongo.properties")
        modules(myModule)
    }

    Window(onCloseRequest = ::exitApplication, title = APP_NAME, resizable = false) {
        window.minimumSize = Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT)

        MyApp()
    }
}
