package feature_auth.presentation.login

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import core.presentation.ui.theme.DarkBg
import core.presentation.ui.theme.Dimensions.SpaceSmall
import core.presentation.util.UiEvent
import core.util.StringResources.BUTTON_LOGIN
import kotlinx.coroutines.flow.collectLatest
import org.koin.java.KoinJavaComponent

@Composable
fun LoginScreen(
    scaffoldState: ScaffoldState,
    scrollState: ScrollState,
    onLogin: () -> Unit,
) {
    val loginViewModel: LoginViewModel by remember {
        KoinJavaComponent.inject(LoginViewModel::class.java)
    }

    val loadingState = loginViewModel.loadingState.collectAsState()

    LaunchedEffect(loginViewModel.eventFlow) {
        loginViewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiEvent.OnLogin -> {
                    onLogin()
                }
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.textMessage,
                        duration = event.duration
                    )
                }
                else -> Unit
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(scrollState)
    ) {

        Button(
            modifier = Modifier
                .align(Alignment.Center),
            shape = Shapes().medium,
            onClick = {
                loginViewModel.onEvent(LoginFormEvent.Submit)
            },
            enabled = !loadingState.value
        ) {
            Text(
                text = BUTTON_LOGIN,
                color = MaterialTheme.colors.onPrimary,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(SpaceSmall)
            )
        }

        if (loadingState.value) CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
        )
    }
}