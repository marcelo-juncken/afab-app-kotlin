package feature_config.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.presentation.ui.theme.DarkColorPalettes
import core.presentation.ui.theme.Dimensions
import core.presentation.ui.theme.LightColorPalettes
import core.presentation.util.UiEvent
import core.util.Constants.THEME_PALETTE_COLUMNS
import core.util.StringResources.DARK_THEMES_TEXT
import core.util.StringResources.LIGHT_THEMES_TEXT
import feature_auth.presentation.register.RegisterScreen
import kotlinx.coroutines.flow.collectLatest
import java.awt.Cursor

sealed interface ConfigSubscreen {
    object CreateAccount : ConfigSubscreen
    object ManageDevices : ConfigSubscreen
    object Logout : ConfigSubscreen
    object ThemePalettes : ConfigSubscreen
}

@Composable
fun ConfigScreen(
    scaffoldState: ScaffoldState,
    scrollState: ScrollState,
    settingsViewModel: SettingsViewModel,
    onPaletteSelected: (Colors) -> Unit,
    onLogOut: () -> Unit,
) {

    val lightColorPalettesState = remember {
        settingsViewModel.lightColorPalettesState
    }
    val darkColorPalettesState = remember {
        settingsViewModel.darkColorPalettesState
    }

    val isAdminState = settingsViewModel.isAdminState.collectAsState()

    var subscreenSelected by remember {
        mutableStateOf<ConfigSubscreen>(ConfigSubscreen.ThemePalettes)
    }

    LaunchedEffect(true) {
        settingsViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.textMessage,
                        duration = event.duration
                    )
                }
                UiEvent.OnLogout -> onLogOut()
                else -> Unit
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {

        Row(modifier = Modifier.matchParentSize()) {
            CustomNavigationRail(
                isAdmin = isAdminState.value,
                subscreenSelected = subscreenSelected,
                onSubscreenSelected = { subscreenSelected = it })

            when (subscreenSelected) {
                ConfigSubscreen.ThemePalettes -> {
                    PalettesThemeScreen(
                        lightColorPalettes = lightColorPalettesState.value,
                        darkColorPalettes = darkColorPalettesState.value,
                        onPaletteSelected = {
                            settingsViewModel.onEvent(SettingsEvent.ChangeTheme(it))
                            onPaletteSelected(it)
                        }
                    )
                }
                ConfigSubscreen.CreateAccount -> {
                    if (isAdminState.value) {
                        RegisterScreen(
                            scaffoldState = scaffoldState,
                            scrollState = scrollState,
                            onLogout = { settingsViewModel.onEvent(SettingsEvent.Logout) },
                        )
                    }
                }
                ConfigSubscreen.ManageDevices -> {
                    if (isAdminState.value) {
                        DevicesScreen(
                            scaffoldState = scaffoldState,
                            onLogout = { settingsViewModel.onEvent(SettingsEvent.Logout) },
                        )
                    }
                }
                ConfigSubscreen.Logout -> settingsViewModel.onEvent(SettingsEvent.Logout)
            }
        }
    }
}

@Composable
fun CustomNavigationRail(
    isAdmin: Boolean,
    subscreenSelected: ConfigSubscreen,
    onSubscreenSelected: (ConfigSubscreen) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val isHovered by interactionSource.collectIsHoveredAsState()

    val targetWidth = animateDpAsState(
        targetValue = if (isHovered) 200.dp else 58.dp,
        animationSpec = TweenSpec(durationMillis = 100, easing = LinearOutSlowInEasing)
    )

    Surface(elevation = 6.dp, modifier = Modifier.fillMaxHeight(), color = MaterialTheme.colors.surface) {
        Column(
            modifier = Modifier
                .width(targetWidth.value)
                .fillMaxHeight()
                .background(MaterialTheme.colors.background)
                .hoverable(interactionSource = interactionSource),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RailItemRow(
                itemName = "Temas",
                painter = painterResource("themes.png"),
                isSelected = subscreenSelected == ConfigSubscreen.ThemePalettes,
                isExpanded = isHovered,
                onClick = { onSubscreenSelected(ConfigSubscreen.ThemePalettes) }
            )
            if (isAdmin) {
                RailItemRow(
                    itemName = "Criar Contas",
                    icon = Icons.Outlined.AccountBox,
                    isSelected = subscreenSelected == ConfigSubscreen.CreateAccount,
                    isExpanded = isHovered,
                    onClick = { onSubscreenSelected(ConfigSubscreen.CreateAccount) }
                )
                RailItemRow(
                    itemName = "Dispositivos",
                    icon = Icons.Outlined.Build,
                    isSelected = subscreenSelected == ConfigSubscreen.ManageDevices,
                    isExpanded = isHovered,
                    onClick = { onSubscreenSelected(ConfigSubscreen.ManageDevices) }
                )
            }
            RailItemRow(
                itemName = "Deslogar",
                icon = Icons.Outlined.ExitToApp,
                isSelected = subscreenSelected == ConfigSubscreen.Logout,
                isExpanded = isHovered,
                onClick = { onSubscreenSelected(ConfigSubscreen.Logout) }
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}


@Composable
fun RailItemRow(
    itemName: String,
    icon: ImageVector? = null,
    painter: Painter? = null,
    isSelected: Boolean,
    isExpanded: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { onClick() }
            .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
            .background(if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(16.dp))

        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = itemName,
                tint = if (isSelected) MaterialTheme.colors.background else MaterialTheme.colors.primary
            )
        }

        painter?.let {
            Icon(
                painter = it,
                contentDescription = itemName,
                tint = if (isSelected) MaterialTheme.colors.background else MaterialTheme.colors.primary,
                modifier = Modifier.size(24.dp)
            )
        }


        Spacer(modifier = Modifier.width(16.dp))

        AnimatedVisibility(visible = isExpanded) {
            TypingTextEffect(text = itemName, isSelected = isSelected, isExpanded = isExpanded)
        }
    }
}

@Composable
fun PalettesThemeScreen(
    lightColorPalettes: List<LightColorPalettes>,
    darkColorPalettes: List<DarkColorPalettes>,
    onPaletteSelected: (Colors) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
    ) {
        PalettesTheme(
            modifier = Modifier.weight(1f),
            headerText = LIGHT_THEMES_TEXT,
            colorPalettes = lightColorPalettes.map { it.toColor() },
            isLight = MaterialTheme.colors.isLight,
            onPaletteSelected = onPaletteSelected
        )
        PalettesTheme(
            modifier = Modifier.weight(1f),
            headerText = DARK_THEMES_TEXT,
            colorPalettes = darkColorPalettes.map { it.toColor() },
            isLight = !MaterialTheme.colors.isLight,
            onPaletteSelected = onPaletteSelected
        )
    }
}

@Composable
fun PalettesTheme(
    modifier: Modifier = Modifier,
    headerText: String,
    colorPalettes: List<Colors>,
    isLight: Boolean,
    onPaletteSelected: (Colors) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(headerText, color = MaterialTheme.colors.onBackground)
        Spacer(modifier = Modifier.height(4.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            items(colorPalettes.chunked(THEME_PALETTE_COLUMNS)) { colorRow ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    colorRow.forEach { colorPalette ->
                        Box(
                            modifier = Modifier
                                .size(Dimensions.SpaceLargeHigh)
                                .padding(Dimensions.SpaceVerySmall)
                                .padding(
                                    if (colorPalette.primary == MaterialTheme.colors.primary &&
                                        colorPalette.onPrimary == MaterialTheme.colors.onPrimary &&
                                        isLight
                                    ) 4.dp else 0.dp
                                )
                                .drawWithContent {
                                    drawCircle(
                                        color = Color.Black,
                                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                                    )
                                    drawContent()
                                }
                                .background(colorPalette.primary, CircleShape)
                                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
                                .clickable { onPaletteSelected(colorPalette) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TypingTextEffect(text: String, isSelected: Boolean, isExpanded: Boolean, duration: Int = 5) {
    val animatingText = remember { mutableStateOf("") }

    LaunchedEffect(key1 = isExpanded) {
        animatingText.value = ""
        for (i in 1..text.length) {
            animatingText.value = text.substring(0, i)
        }
    }

    if (isExpanded) {
        Text(
            text = animatingText.value,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colors.background else MaterialTheme.colors.primary,
            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
        )
    }
}
