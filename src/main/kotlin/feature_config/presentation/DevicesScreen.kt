package feature_config.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import core.presentation.util.UiEvent
import feature_auth.domain.models.DeviceDisplayInfo
import feature_auth.domain.models.UserDisplayInfo
import kotlinx.coroutines.flow.collectLatest
import org.koin.java.KoinJavaComponent
import java.awt.Cursor
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.imageio.ImageIO

@Composable
fun DevicesScreen(
    scaffoldState: ScaffoldState,
    onLogout: () -> Unit,
) {
    val viewModel: DevicesViewModel by remember {
        KoinJavaComponent.inject(DevicesViewModel::class.java)
    }

    val usersState = viewModel.usersState.collectAsState()
    val loadingState = viewModel.loadingState.collectAsState()

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val selectedUser by derivedStateOf { selectedIndex?.let { usersState.value.getOrNull(it) } }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.textMessage,
                        duration = event.duration
                    )
                }
                UiEvent.OnLogout -> {
                    onLogout()
                }
                else -> Unit
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.93f)
                .padding(16.dp)
        ) {

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .border(4.dp, MaterialTheme.colors.onSurface, RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                items(usersState.value.size) { index ->
                    UserImage(
                        enabled = !loadingState.value,
                        users = usersState.value,
                        index = index,
                        selectedUser = selectedUser,
                        onUserSelected = {
                            selectedIndex = if (selectedIndex == it) {
                                -1
                            } else {
                                it
                            }
                        }
                    )
                }

            }
            if (selectedIndex != null) {
                DeviceDetails(
                    user = selectedUser,
                    isButtonEnabled = !loadingState.value,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onRemoveDevice = { user, device ->
                        viewModel.onEvent(DevicesEvent.RemoveDevice(user, device))
                    },
                    onToggleAccess = { user ->
                        viewModel.onEvent(DevicesEvent.ToggleUserAccess(user))
                    }
                )
            }
        }
        if (loadingState.value) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun UserImage(
    enabled: Boolean,
    users: List<UserDisplayInfo>,
    index: Int,
    selectedUser: UserDisplayInfo?,
    onUserSelected: (Int) -> Unit,
) {
    val imageBitmap: ImageBitmap? = base64ToImageBitmap(users[index].profilePictureUrl ?: "")

    val selectedColor = MaterialTheme.colors.primary
    val unselectedColor = MaterialTheme.colors.onBackground

    val color = remember(selectedUser) {
        if (selectedUser == users[index]) selectedColor else unselectedColor
    }

    IconButton(
        onClick = {
            if (enabled) onUserSelected(index)
        },
        modifier = Modifier
            .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(if (enabled) Cursor.HAND_CURSOR else Cursor.DEFAULT_CURSOR)))
            .height(120.dp)
            .width(90.dp)
            .border(2.dp, color, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(if (users[index].isActive) MaterialTheme.colors.surface else MaterialTheme.colors.error)


    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            imageBitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = "Imagem de perfil",
                    modifier = Modifier,
                    contentScale = ContentScale.Crop
                )
                if (!users[index].isActive) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Red.copy(alpha = 0.5f))
                    )
                }
            }
        }
    }
}

fun base64ToImageBitmap(base64String: String): ImageBitmap? {
    return try {
        val imageData = base64String.substringAfter("base64,")

        val decodedBytes = Base64.getDecoder().decode(imageData)

        val inputStream = ByteArrayInputStream(decodedBytes)

        val bufferedImage: BufferedImage? = ImageIO.read(inputStream)

        bufferedImage?.toComposeImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun DeviceDetails(
    modifier: Modifier = Modifier,
    user: UserDisplayInfo?,
    isButtonEnabled: Boolean,
    onRemoveDevice: (UserDisplayInfo?, DeviceDisplayInfo) -> Unit,
    onToggleAccess: (UserDisplayInfo?) -> Unit,
) {
    Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp).fillMaxSize().then(modifier)) {
        user?.let {
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Text(
                        text = "Usuário: ${it.name}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colors.onBackground
                    )
                    Text(
                        text = "Acessos Totais: ${it.accessCount}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colors.onBackground
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().weight(0.2f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (it.isActive) "Ativo" else "Inativo",
                        color = if (it.isActive) Color(0xFF4CAF50) else Color(0xFFF44336),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        enabled = isButtonEnabled,
                        modifier = Modifier
                            .size(24.dp)
                            .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(if (isButtonEnabled) Cursor.HAND_CURSOR else Cursor.DEFAULT_CURSOR))),
                        onClick = {
                            onToggleAccess(user)
                        },
                    ) {
                        Icon(
                            painter = painterResource(if (it.isActive) "lock-open-outline.svg" else "lock-outline.svg"),
                            contentDescription = if (it.isActive) "Desativar usuário" else "Ativar usuário",
                            tint = if (it.isActive) Color(0xFFF44336) else Color(0xFF4CAF50),
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(it.devices) { device ->
                    DeviceItem(device = device, isButtonEnabled = isButtonEnabled, onRemoveDevice = {
                        onRemoveDevice(user, it)
                    })
                }
            }
        }
    }
}

@Composable
fun DeviceItem(device: DeviceDisplayInfo, isButtonEnabled: Boolean, onRemoveDevice: (DeviceDisplayInfo) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(0.9f).padding(16.dp)) {
        Text(
            text = "Id do dispositivo: ${device.deviceId}", maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colors.onBackground
        )
        Text(
            text = "Nome do dispositivo: ${device.deviceName}", maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colors.onBackground
        )
        Text(
            text = "Última vez online: ${formatInstant(device.lastLogin)}", maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colors.onBackground
        )
        Text(
            text = "Data de criação: ${formatInstant(device.addedDate)}", maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colors.onBackground
        )
        Text(
            text = "Acessos: ${device.accessCount}", maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier
                .clip(RoundedCornerShape(2.dp))
                .fillMaxWidth()
                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(if (isButtonEnabled) Cursor.HAND_CURSOR else Cursor.DEFAULT_CURSOR))),
            enabled = isButtonEnabled,
            onClick = { onRemoveDevice(device) }
        ) {
            Text(
                text = "Remover Dispositivo",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onPrimary
            )
        }
    }
}

private fun formatInstant(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return formatter.format(dateTime)
}