package core.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import core.presentation.ui.theme.Dimensions.SpaceMediumHigh
import core.presentation.ui.theme.Dimensions.SpaceSmall
import core.util.Constants.MINIMUM_HEIGHT
import core.util.Constants.MINIMUM_WIDTH
import core.util.StringResources.HINT_SEARCH_DROPDOWN
import core.util.StringResources.JOB_IS_EMPTY
import feature_payments.domain.models.JobOrder
import feature_payments.presentation.util.Client
import java.awt.Cursor

@Composable
fun StandardDropdown(list: List<String>?) {

    if (list == null) return

    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }
    val selectedItem = remember { mutableStateOf(list[selectedIndex]) }

    val rotation: Float by animateFloatAsState(if (expanded) -180f else 0f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black)
            .clickable { expanded = true }
            .focusable()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(selectedItem.value, modifier = Modifier.weight(1f), color = MaterialTheme.colors.onBackground)
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.rotate(rotation),
                tint = MaterialTheme.colors.primary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            list.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        selectedItem.value = item
                        selectedIndex = index
                        expanded = false
                    }
                ) {
                    Text(item, color = MaterialTheme.colors.onBackground)
                }
            }
        }
    }
}

@Composable
fun clientIcon(client: Client?) {
    val bitmap = remember(client) {
        mutableStateOf<ImageBitmap?>(null)
    }

    LaunchedEffect(client) {
        client?.fileName?.let { fileName ->
            bitmap.value = useResource(fileName) { loadImageBitmap(it) }
        }
    }

    bitmap.value?.let {
        Icon(
            bitmap = it,
            modifier = Modifier.size(24.dp),
            contentDescription = client?.contentDescription,
            tint = MaterialTheme.colors.primary
        )
    }
}


@Composable
fun AutoCompleteDropdown(
    list: List<JobOrder>,
    currentJobName: String,
    searchHint: String? = null,
    error: String = "",
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelectedItem: (JobOrder?, String) -> Unit,
) {

    var searchText by remember(currentJobName) { mutableStateOf(currentJobName) }

    val filteredList by remember(list, searchText) {
        derivedStateOf {
            list.filter { "${it.jobNumber} - ${it.jobName}".contains(searchText, ignoreCase = true) }
        }
    }

    val focusRequester = remember { FocusRequester() }

    val rotation: Float by animateFloatAsState(if (expanded) -180f else 0f)

    var selectedImage by remember {
        mutableStateOf<Client?>(null)
    }

    LaunchedEffect(currentJobName) {
        selectedImage = returnSelectedJobOrder(list, currentJobName)?.jobClient
    }

    Box(modifier = Modifier.then(modifier)) {
        Column {

            OutlinedTextField(
                isError = error != "",
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = MaterialTheme.colors.onSurface
                ),
                value = searchText,
                onValueChange = { value ->
                    onExpandedChange(true)
                    searchText = value
                },
                label = { Text(text = searchHint ?: HINT_SEARCH_DROPDOWN, color = MaterialTheme.colors.onBackground) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        onExpandedChange(it.isFocused)
                        if (!it.isFocused) {
                            selectedImage = returnSelectedJobOrder(list, searchText)?.jobClient
                            onSelectedItem(returnSelectedJobOrder(list, searchText), searchText)
                        }
                    },
                textStyle = MaterialTheme.typography.body1,
                maxLines = 1,
                leadingIcon = selectedImage?.let {
                    { clientIcon(selectedImage) }
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (searchText.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    searchText = ""
                                    focusRequester.requestFocus()
                                    selectedImage = null
                                    onExpandedChange(true)
                                },
                                modifier = Modifier.pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                onExpandedChange(rotation == 0f)
                            },
                            modifier = Modifier.pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                modifier = Modifier.rotate(rotation),
                                contentDescription = "Close",
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    }
                }
            )
            if (error.isNotEmpty() && !expanded) {
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
        DropdownMenu(
            expanded = expanded,
            focusable = false,
            onDismissRequest = {
                onExpandedChange(false)
                selectedImage = returnSelectedJobOrder(list, searchText)?.jobClient
                onSelectedItem(returnSelectedJobOrder(list, searchText), searchText)
            },
            modifier = Modifier.width((MINIMUM_WIDTH.dp / 2) - (SpaceSmall + SpaceMediumHigh))
                .heightIn(max = MINIMUM_HEIGHT.dp / 2)
        ) {
            if (filteredList.isEmpty()) {
                Text(JOB_IS_EMPTY, color = MaterialTheme.colors.onBackground)
            } else {
                filteredList.forEach { jobOrder ->
                    val item = "${jobOrder.jobNumber} - ${jobOrder.jobName}"
                    DropdownMenuItem(
                        onClick = {
                            searchText = item
                            selectedImage = returnSelectedJobOrder(list, searchText)?.jobClient
                            onSelectedItem(jobOrder, searchText)
                            onExpandedChange(false)
                        }
                    ) {
                        Text(item, color = MaterialTheme.colors.onBackground)
                    }
                }
            }
        }
    }

}

private fun returnSelectedJobOrder(list: List<JobOrder>, searchText: String): JobOrder? {
    return list.firstOrNull { "${it.jobNumber} - ${it.jobName}" == searchText }
}
