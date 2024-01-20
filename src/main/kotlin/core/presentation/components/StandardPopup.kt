package core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import core.presentation.ui.theme.Dimensions
import core.presentation.ui.theme.Dimensions.SpaceMedium
import core.util.Constants.MAX_TEMPLATE_NAME_LENGTH
import core.util.StringResources
import java.awt.Cursor

@Composable
fun StandardPopup(
    hasTextField: Boolean = true,
    initialName: String?,
    textFieldHint: String? = null,
    textName: String,
    showDialog: Boolean,
    error: String = "",
    loadingState: Boolean,
    onDismiss: () -> Unit,
    onOkClick: (String) -> Unit,
    maxLength: Int = MAX_TEMPLATE_NAME_LENGTH,
) {
    if (showDialog) {
        val textFieldValue = remember { mutableStateOf(initialName ?: "") }
        Popup(focusable = true, onDismissRequest = onDismiss, alignment = Alignment.Center) {
            Surface(
                modifier = Modifier.width(300.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color.Black),

                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(text = textName, fontSize = 18.sp, color = MaterialTheme.colors.onBackground)

                    Spacer(modifier = Modifier.height(SpaceMedium))

                    if (hasTextField) {
                        TextField(
                            value = textFieldValue.value,
                            onValueChange = {
                                if (it.length <= maxLength) {
                                    textFieldValue.value = it
                                }
                            },
                            label = { Text(textFieldHint ?: "", color = MaterialTheme.colors.onBackground) },
                            singleLine = true,
                            isError = error != "",
                        )
                    }

                    TextError(error = error)

                    Spacer(modifier = Modifier.height(SpaceMedium))

                    if (loadingState) {
                        CircularProgressIndicator(modifier = Modifier)
                        Spacer(modifier = Modifier.height(SpaceMedium))
                    }

                    Row {
                        Button(
                            modifier = Modifier.fillMaxWidth()
                                .padding(vertical = Dimensions.SpaceMedium).padding(end = Dimensions.SpaceVerySmall)
                                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
                                .weight(1f),
                            shape = RoundedCornerShape(2.dp),
                            border = BorderStroke(2.dp, Color.Black),
                            onClick = {
                                onOkClick(textFieldValue.value)
                            }
                        ) {
                            Text(
                                text = StringResources.SAVE_BUTTON,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colors.onPrimary
                            )
                        }

                        Button(
                            modifier = Modifier.fillMaxWidth()
                                .padding(vertical = Dimensions.SpaceMedium).padding(start = Dimensions.SpaceVerySmall)
                                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
                                .weight(1f),
                            shape = RoundedCornerShape(2.dp),
                            border = BorderStroke(2.dp, Color.Black),
                            onClick = onDismiss

                        ) {
                            Text(
                                text = StringResources.CANCEL_BUTTON,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colors.onPrimary
                            )
                        }

                    }
                }
            }
        }
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