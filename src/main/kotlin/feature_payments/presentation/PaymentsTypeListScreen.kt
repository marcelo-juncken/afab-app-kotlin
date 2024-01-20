package feature_payments.presentation

import StandardCheckboxWithText
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import core.data.PaymentTemplateItem
import core.presentation.components.*
import core.presentation.ui.theme.Dimensions.ColorButtonsHeight
import core.presentation.ui.theme.Dimensions.ColorPickerButtonSize
import core.presentation.ui.theme.Dimensions.bottomNavBarHeight
import core.presentation.ui.theme.Dimensions.SpaceLarge
import core.presentation.ui.theme.Dimensions.SpaceMedium
import core.presentation.ui.theme.Dimensions.SpaceSmall
import core.presentation.ui.theme.Dimensions.SpaceVerySmall
import core.presentation.util.DateMask
import core.presentation.util.UiEvent
import core.util.Constants.MAX_DATE_LENGTH
import core.util.Constants.MAX_TEMPLATE_ITEMS
import core.util.StringResources.CANCEL_BUTTON
import core.util.StringResources.CREATE_DATE
import core.util.StringResources.EMPTY_FIELD
import core.util.StringResources.END_DATE
import core.util.StringResources.ERROR_LIST_FULL
import core.util.StringResources.ERROR_MAX_LENGTH
import core.util.StringResources.INITIAL_DATE
import core.util.StringResources.INITIAL_DATE_LATER_THAN_CREATED_DATE
import core.util.StringResources.INITIAL_DATE_LATER_THAN_FINAL_DATE
import core.util.StringResources.INVALID_DATE
import core.util.StringResources.INVALID_JOB
import core.util.StringResources.INVALID_YEAR
import core.util.StringResources.JOB_LIST_IS_EMPTY
import core.util.StringResources.LIMIT_BY_CREATE_DATE
import core.util.StringResources.TEXT_CASH
import core.util.StringResources.TEXT_CREATE
import core.util.StringResources.TEXT_EXEC
import core.util.StringResources.TEXT_GERAL
import core.util.StringResources.TEXT_POS
import core.util.StringResources.TEXT_PROD
import core.util.StringResources.TEXT_SPLIT
import core.util.StringResources.USE_ALTERNATIVE_CODE
import feature_payments.domain.models.JobOrder
import feature_payments.presentation.states.CheckboxesState
import feature_payments.presentation.states.ColorsState
import feature_payments.presentation.states.TextFieldFormState
import feature_payments.presentation.util.InputError
import feature_payments.presentation.util.PaidType
import kotlinx.coroutines.flow.collectLatest
import java.awt.Cursor

@Composable
fun PaymentsTypeListScreen(
    scaffoldState: ScaffoldState,
    viewModel: PaymentsViewModel,
    scrollState: ScrollState,
    onLogOut: () -> Unit,
) {

    val cbTextCreateSheetsErrorState = viewModel.cbTextCreateSheetFormState.collectAsState()
    val jobListState = viewModel.jobListState.collectAsState()
    val savedTemplateListState = viewModel.savedTemplateListState.collectAsState()
    val loadingState = viewModel.loadingState.collectAsState()
    val dialogLoadingState = viewModel.dialogLoadingState.collectAsState()
    val checkboxesState = viewModel.checkboxesState.collectAsState()
    val textFieldsState = viewModel.textFieldFormState.collectAsState()
    val colorsState = viewModel.colorsState.collectAsState()

    val (isJobExpanded, setIsJobExpanded) = remember { mutableStateOf(false) }

    val (typeColor, setTypeColor) = remember { mutableStateOf<PaidType?>(null) }

    val (isColorPaletteVisible, setIsColorPaletteVisible) = remember { mutableStateOf(false) }

    var templateSaveError by remember { mutableStateOf("") }

    var templateItem by remember { mutableStateOf<PaymentTemplateItem?>(null) }

    val showDialog = remember { mutableStateOf(false) }

    // TODO: 3/28/2023 criar exemplos no db: uma conta com 2 dispositivos pra mostrar o numero maximo de dispositivo. Uma conta de admin, uma conta normal.
    // TODO: 3/29/2023 testar sem internet

    // TODO: 3/30/2023 ver outra forma de guardar os dados do db
    // TODO: 3/30/2023 ver como faz pra fazer update de programa
    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.textMessage,
                        duration = event.duration
                    )
                }
                is UiEvent.OnLogout -> {
                    viewModel.onEvent(PaymentsFormEvent.Logout)
                    onLogOut()
                }
                else -> Unit
            }
        }
    }

    LaunchedEffect(viewModel.saveDialogEvent) {
        viewModel.saveDialogEvent.collectLatest { event ->
            templateSaveError = when (event?.nameError) {
                is InputError.EmptyField -> EMPTY_FIELD
                is InputError.MaxLengthError -> ERROR_MAX_LENGTH
                else -> {
                    if (event?.saveTemplateResult != null) {
                        showDialog.value = false
                    }
                    ""
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomNavBarHeight)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { setIsJobExpanded(false) }
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        StandardPopup(
            initialName = templateItem?.templateName,
            textName = "Escolha um nome para o template",
            textFieldHint = "Nome",
            showDialog = showDialog.value,
            error = templateSaveError,
            loadingState = dialogLoadingState.value,
            onDismiss = {
                showDialog.value = false
                viewModel.onEvent(PaymentsFormEvent.CancelSaveTemplate)
            },
            onOkClick = { textFieldValue ->
                templateItem?.let { item ->
                    viewModel.onEvent(PaymentsFormEvent.EditTemplate(item, textFieldValue))
                } ?: if (savedTemplateListState.value.size < MAX_TEMPLATE_ITEMS) {
                    viewModel.onEvent(PaymentsFormEvent.SaveTemplate(textFieldValue))
                } else {
                    templateSaveError = ERROR_LIST_FULL
                }
            },
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = SpaceMedium, end = SpaceMedium, top = SpaceMedium, bottom = SpaceVerySmall)
        ) {
            DropdownMenuWithAddButton(
                items = savedTemplateListState.value,
                modifier = Modifier.fillMaxWidth().padding(start = SpaceMedium),
                onItemClick = { viewModel.onEvent(PaymentsFormEvent.LoadTemplate(it)) },
                onAddEditItem = {
                    templateSaveError = ""
                    templateItem = it
                    showDialog.value = true
                },
                onItemDeleted = { viewModel.onEvent(PaymentsFormEvent.DeleteTemplate(it)) }
            )

            Row(modifier = Modifier.padding(top = SpaceVerySmall)) {
                LeftColumn(
                    modifier = Modifier.weight(1f),
                    viewModel = viewModel,
                    scrollState = scrollState,
                    textFields = textFieldsState.value,
                    jobList = jobListState.value,
                    colors = colorsState.value,
                    checkboxes = checkboxesState.value,
                    jobExpanded = isJobExpanded,
                    onJobExpandedChange = setIsJobExpanded,
                    typeSelected = typeColor,
                    onTypeSelectedChange = setTypeColor,
                    isColorPaletteVisible = isColorPaletteVisible,
                    onColorPaletteVisibilityChange = setIsColorPaletteVisible
                )

                RightColumn(
                    modifier = Modifier.weight(1f),
                    viewModel = viewModel,
                    cbTextCreateSheetsError = cbTextCreateSheetsErrorState.value,
                    checkboxes = checkboxesState.value,
                    colors = colorsState.value,
                    isColorPaletteVisible = isColorPaletteVisible,
                    onColorPaletteVisibilityChange = setIsColorPaletteVisible,
                    typeSelected = typeColor,
                    onTypeSelectedChange = setTypeColor
                )
            }
        }
        if (loadingState.value) CircularProgressIndicator(
            modifier = Modifier
        )
    }
}

@Composable
fun LeftColumn(
    modifier: Modifier = Modifier,
    viewModel: PaymentsViewModel,
    scrollState: ScrollState,
    textFields: TextFieldFormState,
    jobList: List<JobOrder>?,
    colors: ColorsState,
    checkboxes: CheckboxesState,
    jobExpanded: Boolean,
    onJobExpandedChange: (Boolean) -> Unit,
    typeSelected: PaidType?,
    onTypeSelectedChange: (PaidType?) -> Unit,
    isColorPaletteVisible: Boolean,
    onColorPaletteVisibilityChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = SpaceVerySmall)
            .verticalScroll(scrollState)
            .then(modifier)
    ) {
        JobDropdownMenu(
            viewModel = viewModel,
            jobList = jobList,
            textFields = textFields,
            jobExpanded = jobExpanded,
            onJobExpandedChange = onJobExpandedChange
        )
        Spacer(modifier = Modifier.height(SpaceVerySmall))

        InitialDateTextField(viewModel = viewModel, textFields = textFields)
        Spacer(modifier = Modifier.height(SpaceMedium))

        EndDateTextField(viewModel = viewModel, textFields = textFields)
        Spacer(modifier = Modifier.height(SpaceMedium))

        UseAlternativeCodeCheckbox(viewModel = viewModel, checkboxesState = checkboxes)
        Spacer(modifier = Modifier.height(SpaceMedium))

        LimitByCreateDateCheckbox(viewModel = viewModel, checkboxesState = checkboxes)
        Spacer(modifier = Modifier.height(SpaceVerySmall))

        CreateDateTextField(viewModel = viewModel, textFields = textFields, checkboxes = checkboxes)
        Spacer(modifier = Modifier.height(SpaceMedium))

        ColorButtons(
            modifier = Modifier,
            colors = colors,
            typeSelected = typeSelected,
            onTypeSelectedChange = onTypeSelectedChange,
            isColorPaletteVisible = isColorPaletteVisible,
            onColorPaletteVisibilityChange = onColorPaletteVisibilityChange,
        )
        Spacer(modifier = Modifier.height(SpaceLarge))

    }
}

@Composable
fun RightColumn(
    modifier: Modifier = Modifier,
    viewModel: PaymentsViewModel,
    cbTextCreateSheetsError: String,
    checkboxes: CheckboxesState,
    colors: ColorsState,
    isColorPaletteVisible: Boolean,
    onColorPaletteVisibilityChange: (Boolean) -> Unit,
    typeSelected: PaidType?,
    onTypeSelectedChange: (PaidType?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = SpaceVerySmall, top = SpaceVerySmall)
            .then(modifier)
    ) {
        CreateSheetsTextError(cbTextCreateSheetsError = cbTextCreateSheetsError)

        CreateSheetsCheckboxes(
            viewModel = viewModel,
            checkboxes = checkboxes,
            cbTextCreateSheetsError = cbTextCreateSheetsError
        )

        SplitSheetsCheckboxes(viewModel = viewModel, checkboxes = checkboxes)
        Spacer(modifier = Modifier.height(SpaceMedium))

        ColorPickerSection(
            viewModel = viewModel,
            colors = colors,
            isColorPaletteVisible = isColorPaletteVisible,
            onColorPaletteVisibilityChange = onColorPaletteVisibilityChange,
            typeSelected = typeSelected,
            onTypeSelectedChange = onTypeSelectedChange
        )
    }
}

@Composable
fun JobDropdownMenu(
    viewModel: PaymentsViewModel,
    jobList: List<JobOrder>?,
    textFields: TextFieldFormState,
    jobExpanded: Boolean,
    onJobExpandedChange: (Boolean) -> Unit,
) {
    AutoCompleteDropdown(
        list = jobList ?: emptyList(),
        currentJobName = textFields.job,
        error = when (textFields.jobError) {
            is InputError.EmptyList -> JOB_LIST_IS_EMPTY
            is InputError.EmptyField -> EMPTY_FIELD
            is InputError.InvalidJob -> INVALID_JOB
            else -> ""
        },
        modifier = Modifier.fillMaxWidth(),
        expanded = jobExpanded,
        onExpandedChange = onJobExpandedChange,
        onSelectedItem = { jobOrder, searchText ->
            viewModel.onEvent(PaymentsFormEvent.JobChanged(jobOrder, searchText))
        }
    )
}

@Composable
fun InitialDateTextField(viewModel: PaymentsViewModel, textFields: TextFieldFormState) {
    StandardOutlinedTextField(
        hint = INITIAL_DATE,
        text = textFields.dtIni,
        onValueChange = {
            if (it.isBlank() || it.last().isDigit()) {
                viewModel.onEvent(PaymentsFormEvent.IniDateChanged(it))
            }
        },
        visualTransformation = VisualTransformation.DateMask,
        keyboardType = KeyboardType.Number,
        error = when (textFields.dtIniError) {
            InputError.EmptyField -> EMPTY_FIELD
            InputError.InvalidDate -> INVALID_DATE
            is InputError.InvalidYear -> INVALID_YEAR
            else -> ""
        },
        maxLength = MAX_DATE_LENGTH,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun EndDateTextField(viewModel: PaymentsViewModel, textFields: TextFieldFormState) {
    StandardOutlinedTextField(
        hint = END_DATE,
        text = textFields.dtEnd,
        onValueChange = {
            if (it.isBlank() || it.last().isDigit()) {
                viewModel.onEvent(PaymentsFormEvent.EndDateChanged(it))
            }
        },
        visualTransformation = VisualTransformation.DateMask,
        keyboardType = KeyboardType.Number,
        error = when (textFields.dtEndError) {
            is InputError.EmptyField -> EMPTY_FIELD
            is InputError.InvalidDate -> INVALID_DATE
            is InputError.InvalidYear -> INVALID_YEAR
            is InputError.InitialDateLaterThanEndDate -> INITIAL_DATE_LATER_THAN_FINAL_DATE
            else -> ""
        },
        maxLength = MAX_DATE_LENGTH,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun UseAlternativeCodeCheckbox(viewModel: PaymentsViewModel, checkboxesState: CheckboxesState) {
    StandardCheckboxWithText(
        text = USE_ALTERNATIVE_CODE,
        isChecked = checkboxesState.useAlternativeCode,
        onCheckedChange = { viewModel.onEvent(PaymentsFormEvent.CbAlternativeCodeToggled(it)) },
        rowModifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun LimitByCreateDateCheckbox(viewModel: PaymentsViewModel, checkboxesState: CheckboxesState) {
    StandardCheckboxWithText(
        text = LIMIT_BY_CREATE_DATE,
        isChecked = checkboxesState.useDateCreate,
        onCheckedChange = { viewModel.onEvent(PaymentsFormEvent.CbCreateDateToggled(it)) },
        rowModifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CreateDateTextField(
    viewModel: PaymentsViewModel,
    textFields: TextFieldFormState,
    checkboxes: CheckboxesState,
) {
    if (checkboxes.useDateCreate) {
        StandardOutlinedTextField(
            hint = CREATE_DATE,
            text = textFields.dtCreated,
            onValueChange = {
                if (it.isBlank() || it.last().isDigit()) {
                    viewModel.onEvent(PaymentsFormEvent.CreateDateChanged(it))
                }
            },
            visualTransformation = VisualTransformation.DateMask,
            keyboardType = KeyboardType.Number,
            error = when (textFields.dtCreatedError) {
                is InputError.EmptyField -> EMPTY_FIELD
                is InputError.InvalidDate -> INVALID_DATE
                is InputError.InvalidYear -> INVALID_YEAR
                is InputError.InitialDateLaterThanCreatedDate -> INITIAL_DATE_LATER_THAN_CREATED_DATE
                else -> ""
            },
            maxLength = MAX_DATE_LENGTH,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CreateSheetsTextError(cbTextCreateSheetsError: String) {
    if (cbTextCreateSheetsError != "") {
        Text(text = cbTextCreateSheetsError, color = MaterialTheme.colors.error)
        Spacer(modifier = Modifier.height(SpaceSmall))
    }
}

@Composable
fun CreateSheetsCheckboxes(
    viewModel: PaymentsViewModel,
    checkboxes: CheckboxesState,
    cbTextCreateSheetsError: String,
) {

    Column(
        modifier = Modifier
            .border(
                width = if (cbTextCreateSheetsError != "") 1.dp else 0.dp,
                color = if (cbTextCreateSheetsError != "") MaterialTheme.colors.error else Color.Transparent,
                shape = RoundedCornerShape(5.dp)
            )
            .padding(vertical = 4.dp)
    ) {
        Text(text = TEXT_CREATE, modifier = Modifier.fillMaxWidth(),color = MaterialTheme.colors.onBackground)

        Spacer(modifier = Modifier.height(SpaceSmall))

        Row {
            StandardCheckboxWithText(
                text = TEXT_GERAL,
                rowModifier = Modifier.fillMaxWidth().weight(1f),
                isChecked = checkboxes.createGeral,
                onCheckedChange = { viewModel.onEvent(PaymentsFormEvent.CbCreateGeralToggled(it)) }
            )
            StandardCheckboxWithText(
                text = TEXT_EXEC,
                rowModifier = Modifier.fillMaxWidth().weight(1f),
                isCheckboxEnabled = checkboxes.createGeral,
                isRowEnabled = checkboxes.createGeral,
                isChecked = checkboxes.createExec,
                onCheckedChange = { viewModel.onEvent(PaymentsFormEvent.CbCreateExecToggled(it)) }
            )
            StandardCheckboxWithText(
                text = TEXT_PROD,
                rowModifier = Modifier.fillMaxWidth().weight(1f),
                isCheckboxEnabled = checkboxes.createGeral,
                isRowEnabled = checkboxes.createGeral,
                isChecked = checkboxes.createProd,
                onCheckedChange = { viewModel.onEvent(PaymentsFormEvent.CbCreateProdToggled(it)) }
            )
            StandardCheckboxWithText(
                text = TEXT_POS,
                rowModifier = Modifier.fillMaxWidth().weight(1f),
                isCheckboxEnabled = checkboxes.createGeral,
                isRowEnabled = checkboxes.createGeral,
                isChecked = checkboxes.createPos,
                onCheckedChange = { viewModel.onEvent(PaymentsFormEvent.CbCreatePosToggled(it)) }
            )
            StandardCheckboxWithText(
                text = TEXT_CASH,
                rowModifier = Modifier.fillMaxWidth().weight(1f),
                isChecked = checkboxes.createCash,
                onCheckedChange = { viewModel.onEvent(PaymentsFormEvent.CbCreateCashToggled(it)) }
            )
        }
        Spacer(modifier = Modifier.height(SpaceMedium))
    }
}

@Composable
fun SplitSheetsCheckboxes(
    viewModel: PaymentsViewModel,
    checkboxes: CheckboxesState,
) {
    Text(text = TEXT_SPLIT, modifier = Modifier.fillMaxWidth(),color = MaterialTheme.colors.onBackground)

    Spacer(modifier = Modifier.height(SpaceSmall))

    Row {
        StandardCheckboxWithText(
            text = TEXT_GERAL,
            rowModifier = Modifier.fillMaxWidth().weight(1f),
            isCheckboxEnabled = checkboxes.createGeral,
            isRowEnabled = checkboxes.createGeral,
            isChecked = checkboxes.sortGeral,
            onCheckedChange = { viewModel.onEvent(PaymentsFormEvent.CbSplitGeralToggled(it)) }
        )
        StandardCheckboxWithText(
            text = TEXT_EXEC,
            rowModifier = Modifier.fillMaxWidth().weight(1f),
            isCheckboxEnabled = checkboxes.createExec,
            isRowEnabled = checkboxes.createExec,
            isChecked = checkboxes.sortExec,
            onCheckedChange = { viewModel.onEvent(PaymentsFormEvent.CbSplitExecToggled(it)) }
        )
        StandardCheckboxWithText(
            text = TEXT_PROD,
            rowModifier = Modifier.fillMaxWidth().weight(1f),
            isCheckboxEnabled = checkboxes.createProd,
            isRowEnabled = checkboxes.createProd,
            isChecked = checkboxes.sortProd,
            onCheckedChange = { viewModel.onEvent(PaymentsFormEvent.CbSplitProdToggled(it)) }
        )
        StandardCheckboxWithText(
            text = TEXT_POS,
            rowModifier = Modifier.fillMaxWidth().weight(1f),
            isCheckboxEnabled = checkboxes.createPos,
            isRowEnabled = checkboxes.createPos,
            isChecked = checkboxes.sortPos,
            onCheckedChange = { viewModel.onEvent(PaymentsFormEvent.CbSplitPosToggled(it)) }
        )
        StandardCheckboxWithText(
            text = TEXT_CASH,
            rowModifier = Modifier.fillMaxWidth().weight(1f),
            isCheckboxEnabled = checkboxes.createCash,
            isRowEnabled = checkboxes.createCash,
            isChecked = checkboxes.sortCash,
            onCheckedChange = { viewModel.onEvent(PaymentsFormEvent.CbSplitCashToggled(it)) }
        )
    }
}

@Composable
fun ColorPickerSection(
    viewModel: PaymentsViewModel,
    colors: ColorsState,
    typeSelected: PaidType?,
    isColorPaletteVisible: Boolean,
    onColorPaletteVisibilityChange: (Boolean) -> Unit,
    onTypeSelectedChange: (PaidType?) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        if (isColorPaletteVisible) {
            Text(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = SpaceSmall),
                fontWeight = FontWeight.Bold,
                text = typeSelected!!.type,
                color = MaterialTheme.colors.onBackground
            )
            ColorPicker(
                colorBefore = when (typeSelected) {
                    PaidType.Pagamento -> colors.pagamentoColor
                    PaidType.AP -> colors.apColor
                    PaidType.Prestado -> colors.prestadoColor
                    PaidType.DevSaldo -> colors.devSaldoColor
                    PaidType.Exec -> colors.execColor
                    PaidType.Pos -> colors.posColor
                    PaidType.Prod -> colors.prodColor
                },
                onColorSelected = { color ->
                    viewModel.onEvent(PaymentsFormEvent.ColorType(typeSelected, color))
                    onTypeSelectedChange(null)
                    onColorPaletteVisibilityChange(false)
                }
            )
            Button(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = SpaceMedium)
                    .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))),
                shape = RoundedCornerShape(2.dp),
                border = BorderStroke(2.dp, Color.Black),
                onClick = {
                    onTypeSelectedChange(null)
                    onColorPaletteVisibilityChange(false)
                }
            ) {
                Text(text = CANCEL_BUTTON, fontWeight = FontWeight.Bold,color = MaterialTheme.colors.onPrimary)
            }
        }
    }
}

@Composable
fun ColorPickerButton(
    colorPickerType: PaidType,
    modifier: Modifier = Modifier,
    colors: ColorsState,
    typeSelected: PaidType?,
    onTypeSelectedChange: (PaidType?) -> Unit,
    isColorPaletteVisible: Boolean,
    onColorPaletteVisibilityChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)))
            .clickable {
                if (typeSelected == colorPickerType && isColorPaletteVisible) {
                    onTypeSelectedChange(null)
                    onColorPaletteVisibilityChange(false)
                } else {
                    onTypeSelectedChange(colorPickerType)
                    onColorPaletteVisibilityChange(true)
                }
            }.then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(ColorPickerButtonSize)
                .drawWithContent {
                    drawCircle(
                        color = Color.Black,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                    )
                    drawContent()
                }
                .background(
                    when (colorPickerType) {
                        PaidType.Pagamento -> colors.pagamentoColor
                        PaidType.AP -> colors.apColor
                        PaidType.Prestado -> colors.prestadoColor
                        PaidType.DevSaldo -> colors.devSaldoColor
                        PaidType.Exec -> colors.execColor
                        PaidType.Pos -> colors.posColor
                        PaidType.Prod -> colors.prodColor
                    }, CircleShape
                ),
        )
        Spacer(Modifier.width(SpaceSmall))
        Text(colorPickerType.type, color = MaterialTheme.colors.onBackground)
    }
}

@Composable
fun ColorButtons(
    modifier: Modifier = Modifier,
    colors: ColorsState,
    typeSelected: PaidType?,
    onTypeSelectedChange: (PaidType?) -> Unit,
    isColorPaletteVisible: Boolean,
    onColorPaletteVisibilityChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ColorButtonsHeight)
            .then(modifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ColorPickerButton(
                colorPickerType = PaidType.Pagamento,
                colors = colors,
                typeSelected = typeSelected,
                onTypeSelectedChange = onTypeSelectedChange,
                isColorPaletteVisible = isColorPaletteVisible,
                onColorPaletteVisibilityChange = onColorPaletteVisibilityChange
            )
            Spacer(modifier = Modifier.height(SpaceVerySmall))
            ColorPickerButton(
                colorPickerType = PaidType.AP,
                colors = colors,
                typeSelected = typeSelected,
                onTypeSelectedChange = onTypeSelectedChange,
                isColorPaletteVisible = isColorPaletteVisible,
                onColorPaletteVisibilityChange = onColorPaletteVisibilityChange
            )
            Spacer(modifier = Modifier.height(SpaceVerySmall))
            ColorPickerButton(
                colorPickerType = PaidType.Prestado,
                colors = colors,
                typeSelected = typeSelected,
                onTypeSelectedChange = onTypeSelectedChange,
                isColorPaletteVisible = isColorPaletteVisible,
                onColorPaletteVisibilityChange = onColorPaletteVisibilityChange
            )
            Spacer(modifier = Modifier.height(SpaceVerySmall))
            ColorPickerButton(
                colorPickerType = PaidType.DevSaldo,
                colors = colors,
                typeSelected = typeSelected,
                onTypeSelectedChange = onTypeSelectedChange,
                isColorPaletteVisible = isColorPaletteVisible,
                onColorPaletteVisibilityChange = onColorPaletteVisibilityChange
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            ColorPickerButton(
                colorPickerType = PaidType.Exec,
                colors = colors,
                typeSelected = typeSelected,
                onTypeSelectedChange = onTypeSelectedChange,
                isColorPaletteVisible = isColorPaletteVisible,
                onColorPaletteVisibilityChange = onColorPaletteVisibilityChange
            )
            ColorPickerButton(
                colorPickerType = PaidType.Prod,
                colors = colors,
                typeSelected = typeSelected,
                onTypeSelectedChange = onTypeSelectedChange,
                isColorPaletteVisible = isColorPaletteVisible,
                onColorPaletteVisibilityChange = onColorPaletteVisibilityChange
            )
            ColorPickerButton(
                colorPickerType = PaidType.Pos,
                colors = colors,
                typeSelected = typeSelected,
                onTypeSelectedChange = onTypeSelectedChange,
                isColorPaletteVisible = isColorPaletteVisible,
                onColorPaletteVisibilityChange = onColorPaletteVisibilityChange
            )
        }
    }
}