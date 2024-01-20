package core.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import core.data.PaymentTemplateItem
import java.awt.Cursor


@Composable
fun DropdownMenuWithAddButton(
    items: List<PaymentTemplateItem>,
    textHint: String = "Templates salvos",
    modifier: Modifier = Modifier,
    onItemClick: (PaymentTemplateItem) -> Unit,
    onAddEditItem: (PaymentTemplateItem?) -> Unit,
    onItemDeleted: (PaymentTemplateItem) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    var selectedIndex by remember { mutableStateOf(-1) }

    val rotation: Float by animateFloatAsState(if (expanded) -180f else 0f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colors.primary, RoundedCornerShape(5.dp))
            .clickable { expanded = !expanded }
            .then(modifier)
            .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (selectedIndex == -1) textHint else items[selectedIndex].templateName,
                modifier = Modifier.weight(1f),
                color = if (selectedIndex == -1) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
            )
            IconButton(
                onClick = { onAddEditItem(null) },
            ) {
                Icon(
                    Icons.Outlined.AddCircle,
                    contentDescription = "Add",
                    tint = MaterialTheme.colors.primary,
                )
            }
            IconButton(
                onClick = {
                    expanded = rotation == 0f
                },
            ) {
                Icon(
                    Icons.Outlined.ArrowDropDown,
                    modifier = Modifier.rotate(rotation),
                    contentDescription = "Toggle list visibility",
                    tint = MaterialTheme.colors.primary,
                )
            }

        }

        if (items.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                DropdownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    items.forEachIndexed { index, item ->
                        Column(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            DropdownMenuItem(
                                onClick = {
                                    selectedIndex = index
                                    expanded = false
                                    onItemClick(item)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        item.templateName,
                                        Modifier.weight(1f),
                                        color = MaterialTheme.colors.onBackground
                                    )
                                    IconButton(onClick = {
                                        onAddEditItem(item)
                                    }) {
                                        Icon(
                                            Icons.Filled.Edit,
                                            contentDescription = "Edit item",
                                            tint = MaterialTheme.colors.primary
                                        )
                                    }
                                    IconButton(onClick = {
                                        selectedIndex = -1
                                        onItemDeleted(item)
                                    }) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = "Delete item",
                                            tint = MaterialTheme.colors.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}