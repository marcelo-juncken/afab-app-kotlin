import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import core.presentation.ui.theme.Dimensions.CheckboxHeight
import core.presentation.ui.theme.Dimensions.CheckboxWidth
import core.presentation.ui.theme.Dimensions.SpaceSmall
import core.presentation.ui.theme.Dimensions.SpaceoZeroed
import java.awt.Cursor

@Composable
fun StandardCheckboxWithText(
    text: String,
    rowModifier: Modifier = Modifier,
    checkBoxModifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    isChecked: Boolean,
    isCheckboxEnabled: Boolean = true,
    isRowEnabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(enabled = isRowEnabled) { onCheckedChange(!isChecked) }
            .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
            .then(rowModifier),
    ) {

        Checkbox(
            checked = isChecked,
            enabled = isCheckboxEnabled,
            onCheckedChange = { onCheckedChange(it) },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colors.primary,
                uncheckedColor = MaterialTheme.colors.primary,
                disabledColor = MaterialTheme.colors.onBackground,
                checkmarkColor = MaterialTheme.colors.onPrimary
            ),
            modifier = Modifier
                .width(CheckboxWidth)
                .height(CheckboxHeight)
                .padding(SpaceoZeroed)
                .then(checkBoxModifier)
        )
        Spacer(modifier = Modifier.width(SpaceSmall))

        Text(
            text = text,
            modifier = Modifier
                .padding(SpaceoZeroed)
                .then(textModifier),
            color = MaterialTheme.colors.onBackground
        )
    }
}