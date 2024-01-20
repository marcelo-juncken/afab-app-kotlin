package core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import core.presentation.ui.theme.Dimensions.SpaceLargeHigh
import core.presentation.ui.theme.Dimensions.SpaceVerySmall
import core.presentation.ui.theme.paletteColors
import core.util.Constants.COLOR_PICKER_COLUMNS
import org.jetbrains.skiko.Cursor


@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    colorBefore: Color,
    onColorSelected: (Color) -> Unit,
) {
    var selectedColor by remember { mutableStateOf(Color.White) }
    val columnNumbers = COLOR_PICKER_COLUMNS

    val colors by remember {
        mutableStateOf(paletteColors.chunked(columnNumbers))
    }

    LazyColumn(
        modifier = Modifier.then(modifier),
        content = {
            items(items = colors) { rowColors ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowColors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(SpaceLargeHigh)
                                .padding(SpaceVerySmall)
                                .padding(if (colorBefore == color) 4.dp else 0.dp)
                                .drawWithContent {
                                    drawCircle(
                                        color = Color.Black,
                                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                                    )
                                    drawContent()
                                }
                                .background(color, CircleShape)
                                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
                                .clickable {
                                    selectedColor = color
                                    onColorSelected(selectedColor)
                                }
                        )
                    }
                }
            }
        }
    )
}
