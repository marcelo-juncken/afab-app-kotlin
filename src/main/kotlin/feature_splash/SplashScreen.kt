package feature_splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import core.presentation.util.UiEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

@Composable
fun SplashScreen(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    onLoadedTheme: (Colors) -> Unit,
    onNavigate: (String) -> Unit,
) {
    val viewModel: SplashViewModel by remember {
        inject(SplashViewModel::class.java)
    }

    LaunchedEffect(true) {
        withContext(dispatcher) {
            viewModel.splashEventFlow.collect() { event ->
                when (event) {
                    is SplashScreenEvent.LoadTheme -> {
                        onLoadedTheme(event.theme)
                    }
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest() { event ->
            when (event) {
                is UiEvent.Navigate -> {
                    delay(3000)
                    onNavigate(event.route)
                }
                else -> Unit
            }
        }
    }

    SplashAnimation()
}

@Composable
fun SplashAnimation() {
    val gearImage = painterResource("gear.png")
    val lightImage = painterResource("light.png")
    val afabricaImage = painterResource("afabrica.png")

    val gearSize = DpSize(150.dp, 150.dp)
    val lightSize = DpSize(90.dp, 125.dp)
    val logoSize = DpSize(300.dp, 125.dp)

    val lightOffsetX = lightSize.width / 2
    val logoOffsetX = (lightSize.width + lightOffsetX) * 2

    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            initialStartOffset = StartOffset(500),
            animation = tween(
                durationMillis = 3000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val currentAlpha = remember {
        derivedStateOf { if (rotation == 0f) 0f else 0.8f }
    }
    val alpha by animateFloatAsState(
        targetValue = currentAlpha.value,
        animationSpec = tween(
            delayMillis = 1000,
            durationMillis = 1500,
            easing = FastOutSlowInEasing
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxSize().offset(-(lightSize.width * 2)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(gearSize).rotate(rotation),
            painter = gearImage,
            contentDescription = "Gear"
        )

        Image(
            modifier = Modifier.offset(x = lightOffsetX).alpha(alpha)
                .size(lightSize),
            painter = lightImage,
            contentDescription = "Light"
        )

        Image(
            modifier = Modifier.offset(x = logoOffsetX).alpha(alpha)
                .size(logoSize),
            painter = afabricaImage,
            contentDescription = "Logo"
        )
    }
}