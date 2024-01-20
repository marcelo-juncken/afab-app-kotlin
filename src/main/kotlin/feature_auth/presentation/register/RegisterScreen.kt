package feature_auth.presentation.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import core.presentation.components.StandardOutlinedTextField
import core.presentation.ui.theme.DarkBg
import core.presentation.ui.theme.Dimensions
import core.presentation.ui.theme.Dimensions.SpaceMedium
import core.presentation.util.UiEvent
import core.util.Constants
import core.util.StringResources
import feature_auth.presentation.util.AuthError
import feature_config.presentation.DevicesViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.java.KoinJavaComponent
import java.awt.Cursor

@Composable
fun RegisterScreen(
    scaffoldState: ScaffoldState,
    scrollState: ScrollState,
    onLogout: () -> Unit,
) {
    val viewModel: RegisterViewModel by remember {
        KoinJavaComponent.inject(RegisterViewModel::class.java)
    }

    val (firstName, setFirstName) = remember { mutableStateOf("") }
    val (lastName, setLastName) = remember { mutableStateOf("") }
    val (email, setEmail) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }
    val (confirmPassword, setConfirmPassword) = remember { mutableStateOf("") }
    val (isPasswordVisible, setIsPasswordVisible) = remember { mutableStateOf(false) }
    val (isConfirmPasswordVisible, setIsConfirmPasswordVisible) = remember { mutableStateOf(false) }
    val textFieldsErrorState = viewModel.textFieldsErrorState.collectAsState()
    val loadingState = viewModel.loadingState.collectAsState()

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
        Modifier
            .fillMaxSize()
            .padding(bottom = SpaceMedium)
            .verticalScroll(scrollState)
    ) {

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Center)
                .width(Constants.MINIMUM_WIDTH.dp / 2),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            val textFieldData = listOf(
                Pair(firstName, setFirstName) to StringResources.HINT_FIELD_FIRST_NAME,
                Pair(lastName, setLastName) to StringResources.HINT_FIELD_LAST_NAME,
                Pair(email, setEmail) to StringResources.HINT_FIELD_EMAIL
            )

            val passwordFieldData = listOf(
                Triple(password, setPassword, isPasswordVisible) to StringResources.HINT_FIELD_PASSWORD,
                Triple(
                    confirmPassword,
                    setConfirmPassword,
                    isConfirmPasswordVisible
                ) to StringResources.HINT_FIELD_CONFIRM_PASSWORD
            )

            textFieldData.forEach { (state, hint) ->
                StandardOutlinedTextField(
                    hint = hint,
                    text = state.first,
                    onValueChange = state.second,
                    error = when (textFieldsErrorState.value.getFieldError(hint)) {
                        is AuthError.FieldEmpty -> StringResources.EMPTY_FIELD
                        is AuthError.NameTooShort -> StringResources.ERROR_NAME_TOO_SHORT
                        is AuthError.NameTooLong -> StringResources.ERROR_NAME_TOO_LONG
                        is AuthError.InvalidName -> StringResources.ERROR_INVALID_NAME
                        is AuthError.InvalidEmail -> StringResources.ERROR_INVALID_EMAIL
                        else -> ""
                    }
                )
                Spacer(modifier = Modifier.height(Dimensions.SpaceSmall))
            }

            passwordFieldData.forEach { (state, hint) ->
                StandardOutlinedTextField(
                    hint = hint,
                    text = state.first,
                    visualTransformation = if (state.third) VisualTransformation.None else PasswordVisualTransformation(),
                    onValueChange = state.second,
                    keyboardType = KeyboardType.Password,
                    isPasswordVisible = state.third,
                    onPasswordToggleClick = { isVisible ->
                        if (hint == StringResources.HINT_FIELD_PASSWORD) {
                            setIsPasswordVisible(isVisible)
                        } else {
                            setIsConfirmPasswordVisible(isVisible)
                        }
                    },
                    error = when (textFieldsErrorState.value.getFieldError(hint)) {
                        is AuthError.FieldEmpty -> StringResources.EMPTY_FIELD
                        is AuthError.PasswordTooShort -> StringResources.ERROR_PASSWORD_TOO_SHORT
                        is AuthError.PasswordTooLong -> StringResources.ERROR_PASSWORD_TOO_LONG
                        is AuthError.InvalidPassword -> StringResources.ERROR_INVALID_PASSWORD
                        is AuthError.InvalidPasswordMatch -> StringResources.ERROR_INVALID_PASSWORD_MATCH
                        else -> ""
                    }
                )
                Spacer(modifier = Modifier.height(Dimensions.SpaceSmall))
            }

            Button(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = Dimensions.SpaceMedium)
                    .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))),
                shape = RoundedCornerShape(2.dp),
                border = BorderStroke(2.dp, Color.Black),
                enabled = !loadingState.value,
                onClick = {
                    viewModel.onEvent(
                        RegisterFormEvent.Submit(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword
                        )
                    )
                }
            ) {
                Text(
                    text = StringResources.BUTTON_REGISTER,
                    modifier = Modifier.padding(Dimensions.SpaceSmall),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }

        if (loadingState.value) CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
        )
    }
}