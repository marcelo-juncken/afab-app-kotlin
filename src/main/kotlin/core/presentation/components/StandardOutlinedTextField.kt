package core.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import core.presentation.ui.theme.Dimensions.IconSizeMedium
import core.util.StringResources.HIDE_PASSWORD
import core.util.StringResources.SHOW_PASSWORD
import java.awt.Cursor

@Composable
fun StandardOutlinedTextField(
    modifier: Modifier = Modifier,
    text: String = "",
    hint: String = "",
    maxLength: Int = 400,
    error: String = "",
    style: TextStyle = TextStyle(
        color = MaterialTheme.colors.onBackground
    ),
    singleLine: Boolean = true,
    maxLines: Int = 1,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isPasswordToggleDisplayed: Boolean = keyboardType == KeyboardType.Password,
    isPasswordVisible: Boolean = false,
    onPasswordToggleClick: (Boolean) -> Unit = {},
    onValueChange: (String) -> Unit,
    isEnabled: Boolean = true,
    focusRequester: FocusRequester = FocusRequester(),
) {

    Column(
        modifier = Modifier
            .then(modifier)
    ) {
        TextField(
            value = text,
            onValueChange = {
                if (it.length <= maxLength) {
                    onValueChange(it)
                }
            },
            maxLines = maxLines,
            label = {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground
                )
            },
            isError = error != "",
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            visualTransformation = visualTransformation,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colors.onSurface,
                leadingIconColor = MaterialTheme.colors.primary,
                trailingIconColor = MaterialTheme.colors.primary,
                cursorColor = MaterialTheme.colors.primary,
                focusedBorderColor = MaterialTheme.colors.primary,
                unfocusedBorderColor = MaterialTheme.colors.onSurface
            ),
            singleLine = singleLine,
            leadingIcon = if (leadingIcon != null) {
                val icon: @Composable () -> Unit = {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onBackground,
                        modifier = Modifier.size(IconSizeMedium)
                    )
                }
                icon
            } else null,
            trailingIcon = if (isPasswordToggleDisplayed) {
                val icon: @Composable () -> Unit = {
                    IconButton(
                        onClick = {
                            onPasswordToggleClick(!isPasswordVisible)
                        },
                        modifier = Modifier
                            .size(24.dp)
                            .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
                    ) {
                        Icon(
                            painter = if (isPasswordVisible) {
                                painterResource("eye-off.svg")
                            } else {
                                painterResource("eye.svg")
                            },
                            contentDescription = if (isPasswordVisible) {
                                HIDE_PASSWORD
                            } else {
                                SHOW_PASSWORD
                            },
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
                icon
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester = focusRequester),
            enabled = isEnabled
        )

        TextError(error = error)
    }
}

@Composable
private fun TextError(
    error: String,
) {
    if (error.isNotEmpty()) {
        Text(
            text = error,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.error,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}


