package feature_payments.presentation

import core.data.PaymentTemplateItem
import core.data.PaymentTemplateResult
import core.data.SavedTemplatesType
import core.presentation.util.UiEvent
import core.util.Resource
import core.util.StringResources.EMPTY_PAYMENT_AND_CASH
import core.util.StringResources.ERROR_SELECT_AT_LEAST_ONE_OPTION_TO_CREATE
import feature_auth.domain.use_case.LogoutUserUseCase
import feature_payments.data.local.Converters
import feature_payments.data.mapper.*
import feature_payments.domain.models.*
import feature_payments.domain.use_case.data.GetJobsUseCase
import feature_payments.domain.use_case.data.GetPaymentAndCashUseCase
import feature_payments.domain.use_case.excel.ExcelUseCase
import feature_payments.domain.use_case.template.*
import feature_payments.presentation.states.CheckboxesState
import feature_payments.presentation.states.ColorsState
import feature_payments.presentation.states.JobState
import feature_payments.presentation.states.TextFieldFormState
import feature_payments.presentation.util.InputError
import feature_payments.presentation.util.PaidType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PaymentsViewModel(
    private val stateConverters: Converters,
    private val getPaymentAndCashUseCase: GetPaymentAndCashUseCase,
    private val getJobsUseCase: GetJobsUseCase,
    private val excelUseCase: ExcelUseCase,
    private val manageTemplatesUseCase: ManageTemplatesUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private var paymentJob: Job? = null

    private var saveTemplateJob: Job? = null

    private var deleteTemplateJob: Job? = null

    private var editTemplateNameJob: Job? = null

    private val _jobState = MutableStateFlow(JobState("", ""))
    private val jobState = _jobState.asStateFlow()

    private val _jobListState = MutableStateFlow<List<JobOrder>?>(null)
    val jobListState = _jobListState.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val _checkboxesState = MutableStateFlow(CheckboxesState())
    val checkboxesState = _checkboxesState.asStateFlow()

    private val _textFieldFormState = MutableStateFlow(TextFieldFormState())
    val textFieldFormState = _textFieldFormState.asStateFlow()

    private val _cbTextCreateSheetFormState = MutableStateFlow("")
    val cbTextCreateSheetFormState = _cbTextCreateSheetFormState.asStateFlow()

    private val _colorsState = MutableStateFlow(ColorsState())
    val colorsState = _colorsState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _savedTemplateListState = MutableStateFlow<List<PaymentTemplateItem>>(emptyList())
    val savedTemplateListState = _savedTemplateListState.asStateFlow()

    private val _saveDialogEvent = MutableSharedFlow<PaymentTemplateResult?>()
    val saveDialogEvent = _saveDialogEvent.asSharedFlow()

    private val _dialogLoadingState = MutableStateFlow(false)
    val dialogLoadingState = _dialogLoadingState.asStateFlow()

    init {
        launch(Dispatchers.IO) {
            _loadingState.update { true }

            when (val jobListResult = getJobsUseCase()) {
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(jobListResult.errorMessage)
                    )
                }
                is Resource.Success -> {
                    _jobListState.update { jobListResult.data!! }
                }
                is Resource.Disconnect -> {
                    _eventFlow.emit(UiEvent.OnLogout)
                }
            }
            _loadingState.update { false }
        }

        getAllTemplates()
    }

    fun getAllTemplates() {
        launch(Dispatchers.IO) {

            when (val templateListResult = manageTemplatesUseCase.loadPaymentTemplatesUseCase()) {
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(templateListResult.errorMessage)
                    )
                }
                is Resource.Success -> {
                    _savedTemplateListState.update { templateListResult.data ?: emptyList() }
                }
                is Resource.Disconnect -> {
                    _eventFlow.emit(UiEvent.OnLogout)
                }
            }
        }
    }

    fun onEvent(event: PaymentsFormEvent) {
        when (event) {
            is PaymentsFormEvent.JobChanged -> {
                jobChanged(event.job, event.searchText)
            }
            is PaymentsFormEvent.IniDateChanged -> {
                _textFieldFormState.update { textFieldFormState.value.copy(dtIni = event.date) }
            }
            is PaymentsFormEvent.EndDateChanged -> {
                _textFieldFormState.update { textFieldFormState.value.copy(dtEnd = event.date) }
            }
            is PaymentsFormEvent.CreateDateChanged -> {
                _textFieldFormState.update { textFieldFormState.value.copy(dtCreated = event.date) }
            }
            is PaymentsFormEvent.ColorType -> changeColorType(event)

            is PaymentsFormEvent.CbCreateDateToggled -> {
                _checkboxesState.update { checkboxesState.value.copy(useDateCreate = event.isChecked) }
            }

            is PaymentsFormEvent.CbAlternativeCodeToggled -> {
                _checkboxesState.update { checkboxesState.value.copy(useAlternativeCode = event.isChecked) }
            }

            is PaymentsFormEvent.CbCreateGeralToggled -> {
                _checkboxesState.update {
                    checkboxesState.value.copy(
                        createGeral = event.isChecked,
                        createExec = false,
                        createProd = false,
                        createPos = false,
                        sortGeral = false,
                        sortExec = false,
                        sortProd = false,
                        sortPos = false,
                    )
                }
            }

            is PaymentsFormEvent.CbCreateExecToggled -> {
                _checkboxesState.update {
                    checkboxesState.value.copy(
                        createExec = event.isChecked,
                        sortExec = false
                    )
                }
            }

            is PaymentsFormEvent.CbCreatePosToggled -> {
                _checkboxesState.update {
                    checkboxesState.value.copy(
                        createPos = event.isChecked,
                        sortPos = false
                    )
                }
            }

            is PaymentsFormEvent.CbCreateProdToggled -> {
                _checkboxesState.update {
                    checkboxesState.value.copy(
                        createProd = event.isChecked,
                        sortProd = false
                    )
                }
            }

            is PaymentsFormEvent.CbCreateCashToggled -> {
                _checkboxesState.update {
                    checkboxesState.value.copy(
                        createCash = event.isChecked,
                        sortCash = false
                    )
                }
            }

            is PaymentsFormEvent.CbSplitGeralToggled -> {
                _checkboxesState.update { checkboxesState.value.copy(sortGeral = event.isChecked) }
            }

            is PaymentsFormEvent.CbSplitExecToggled -> {
                _checkboxesState.update { checkboxesState.value.copy(sortExec = event.isChecked) }
            }

            is PaymentsFormEvent.CbSplitProdToggled -> {
                _checkboxesState.update { checkboxesState.value.copy(sortProd = event.isChecked) }
            }

            is PaymentsFormEvent.CbSplitPosToggled -> {
                _checkboxesState.update { checkboxesState.value.copy(sortPos = event.isChecked) }
            }

            is PaymentsFormEvent.CbSplitCashToggled -> {
                _checkboxesState.update { checkboxesState.value.copy(sortCash = event.isChecked) }
            }

            is PaymentsFormEvent.SaveTemplate -> {
                saveTemplate(templateName = event.templateName)
            }

            is PaymentsFormEvent.DeleteTemplate -> {
                deleteTemplate(paymentTemplateItem = event.paymentTemplateItem)
            }

            PaymentsFormEvent.CancelSaveTemplate -> {
                saveTemplateJob?.cancel()
                _dialogLoadingState.update { false }
            }

            is PaymentsFormEvent.EditTemplate -> editTemplateName(
                templateItem = event.templateItem,
                templateNewName = event.templateNewName
            )

            is PaymentsFormEvent.LoadTemplate -> loadTemplate(templateItem = event.templateItem)

            PaymentsFormEvent.Submit -> getPaymentList()

            PaymentsFormEvent.Logout -> logoutUserUseCase()
        }
    }

    private fun jobChanged(job: JobOrder?, searchText: String) {
        val isValidJobName = job != null
        _textFieldFormState.update {
            textFieldFormState.value.copy(
                job = searchText
            )
        }
        _jobState.update {
            jobState.value.copy(
                jobNumber = if (isValidJobName) job?.jobNumber else null,
                jobName = if (isValidJobName) job?.jobName else null,
                jobClient = if (isValidJobName) job?.jobClient else null,
            )
        }
    }

    private fun loadTemplate(templateItem: PaymentTemplateItem) {

        if (templateItem.templateType != SavedTemplatesType.PAYMENT.name) {

            launch {
                _eventFlow.emit(
                    UiEvent.ShowSnackbar("Não é possível usar um template de outro local.")
                )
            }
            return
        }

        val loadedJobs =
            stateConverters.fromJsonToState(templateItem.savedStates["jobMap"].toString(), SavedJob::class.java)
                ?.toJobState()

        val loadedTextFields = stateConverters.fromJsonToState(
            templateItem.savedStates["datesMap"].toString(),
            SavedDates::class.java
        )?.toTextFieldStates()

        val loadedCheckboxes = stateConverters.fromJsonToState(
            templateItem.savedStates["checkboxesMap"].toString(),
            SavedCheckboxes::class.java
        )?.toCheckboxesState()

        val loadedColors = stateConverters.fromJsonToState(
            templateItem.savedStates["colorsMap"].toString(),
            SavedColors::class.java
        )?.toColorsState()

        loadedJobs?.let { _jobState.value = it }

        loadedCheckboxes?.let { _checkboxesState.value = it }

        loadedColors?.let { _colorsState.value = it }

        val jobField = if (loadedJobs?.jobNumber != null && loadedJobs.jobName != null) {
            "${loadedJobs.jobNumber} - ${loadedJobs.jobName}"
        } else {
            ""
        }

        loadedTextFields?.let { textFieldsState ->
            _textFieldFormState.update {
                textFieldFormState.value.copy(
                    dtIni = textFieldsState.dtIni,
                    dtEnd = textFieldsState.dtEnd,
                    dtCreated = textFieldsState.dtCreated,
                    dtIniError = textFieldsState.dtIniError,
                    dtCreatedError = textFieldsState.dtCreatedError,
                    dtEndError = textFieldsState.dtEndError,
                    jobError = textFieldsState.jobError,
                    job = jobField
                )
            }
        }
    }

    private fun editTemplateName(templateItem: PaymentTemplateItem, templateNewName: String) {
        editTemplateNameJob?.cancel()

        _loadingState.update { true }

        editTemplateNameJob = launch(Dispatchers.IO) {

            val response = manageTemplatesUseCase.editPaymentNameTemplateUseCase(
                template = templateItem,
                templateName = templateNewName
            )

            when (val result = response.saveTemplateResult) {
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(result.errorMessage)
                    )
                }
                is Resource.Success -> {
                    val currentList = _savedTemplateListState.value
                    val updatedList = currentList.map {
                        if (it._id == response.saveTemplateResult.data?._id) {
                            it.templateName = response.saveTemplateResult.data.templateName
                        }
                        it
                    }
                    _savedTemplateListState.value = updatedList
                    _saveDialogEvent.emit(response)
                }
                null -> {
                    _saveDialogEvent.emit(response)
                }
                is Resource.Disconnect -> {
                    _eventFlow.emit(UiEvent.OnLogout)
                }
            }
            _loadingState.update { false }
        }
    }

    private fun deleteTemplate(paymentTemplateItem: PaymentTemplateItem) {
        deleteTemplateJob?.cancel()

        _loadingState.update { true }

        deleteTemplateJob = launch(Dispatchers.IO) {

            val response = manageTemplatesUseCase.deletePaymentTemplateUseCase(
                template = paymentTemplateItem
            )

            when (response) {
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(response.errorMessage)
                    )
                }
                is Resource.Success -> {
                    val currentList = _savedTemplateListState.value
                    val updatedList = currentList.filter { it != paymentTemplateItem }
                    _savedTemplateListState.value = updatedList
                }
                is Resource.Disconnect -> {
                    _eventFlow.emit(UiEvent.OnLogout)
                }
            }
            _loadingState.update { false }
        }
    }

    private fun saveTemplate(templateName: String) {

        saveTemplateJob?.cancel()

        saveTemplateJob = launch(Dispatchers.IO) {

            _dialogLoadingState.update { true }
            _saveDialogEvent.emit(PaymentTemplateResult())


            val savedStates = mapOf(
                "jobMap" to stateConverters.toJsonFromState(jobState.value.toSavedJob(), SavedJob::class.java),

                "datesMap" to stateConverters.toJsonFromState(
                    textFieldFormState.value.toSavedDates(),
                    SavedDates::class.java
                ),

                "checkboxesMap" to stateConverters.toJsonFromState(
                    checkboxesState.value.toSavedCheckboxes(),
                    SavedCheckboxes::class.java
                ),
                "colorsMap" to stateConverters.toJsonFromState(
                    colorsState.value.toSavedColors(),
                    SavedColors::class.java
                )
            )

            val response = manageTemplatesUseCase.savePaymentTemplateUseCase(
                templateName = templateName.trim(),
                savedStates = savedStates
            )

            when (val result = response.saveTemplateResult) {
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(result.errorMessage)
                    )
                }
                is Resource.Success -> {
                    val currentList = _savedTemplateListState.value
                    val updatedList = currentList + result.data!!
                    _savedTemplateListState.value = updatedList

                    _saveDialogEvent.emit(response)
                }
                null -> {
                    _saveDialogEvent.emit(response)
                }
                is Resource.Disconnect -> {
                    _eventFlow.emit(UiEvent.OnLogout)
                }
            }
            _dialogLoadingState.update { false }
        }
    }

    private fun changeColorType(event: PaymentsFormEvent.ColorType) {

        when (event.type) {
            PaidType.Pagamento -> {
                _colorsState.update {
                    colorsState.value.copy(
                        pagamentoColor = event.color
                    )
                }
            }
            PaidType.AP -> {
                _colorsState.update {
                    colorsState.value.copy(
                        apColor = event.color
                    )
                }
            }
            PaidType.Prestado -> {
                _colorsState.update {
                    colorsState.value.copy(
                        prestadoColor = event.color
                    )
                }
            }
            PaidType.DevSaldo -> {
                _colorsState.update {
                    colorsState.value.copy(
                        devSaldoColor = event.color
                    )
                }
            }
            PaidType.Exec -> {
                _colorsState.update {
                    colorsState.value.copy(
                        execColor = event.color
                    )
                }
            }
            PaidType.Pos -> {
                _colorsState.update {
                    colorsState.value.copy(
                        posColor = event.color
                    )
                }
            }
            PaidType.Prod -> {
                _colorsState.update {
                    colorsState.value.copy(
                        prodColor = event.color
                    )
                }
            }
            null -> Unit
        }
    }

    private fun getPaymentList() {
        paymentJob?.cancel()

        paymentJob = launch(Dispatchers.IO) {
            _loadingState.update { true }

            _textFieldFormState.update {
                textFieldFormState.value.copy(
                    dtIniError = null,
                    dtEndError = null,
                    dtCreatedError = null,
                    jobError = null
                )
            }

            _cbTextCreateSheetFormState.update { "" }

            val jobSearchText = textFieldFormState.value.job.trim()
            val dtIni = textFieldFormState.value.dtIni.trim()
            val dtEnd = textFieldFormState.value.dtEnd.trim()
            val dtCreated = textFieldFormState.value.dtCreated.trim()
            val useDateCreate = checkboxesState.value.useDateCreate
            val useAlternativeCode = checkboxesState.value.useAlternativeCode

            val paymentAndCashList = getPaymentAndCashUseCase(
                jobList = jobListState.value,
                jobSearchText = jobSearchText,
                jobNumber = jobState.value.jobNumber,
                jobName = jobState.value.jobName,
                useDateCreate = useDateCreate,
                useAlternativeCode = useAlternativeCode,
                dtCreated = dtCreated,
                dtIni = dtIni,
                dtEnd = dtEnd,
                createGeral = checkboxesState.value.createGeral,
                createCash = checkboxesState.value.createCash
            )

            val paymentResult = paymentAndCashList.paymentResult
            val cashResult = paymentAndCashList.cashResult

            if (paymentResult == null && cashResult == null) {
                _textFieldFormState.update {
                    textFieldFormState.value.copy(
                        dtIniError = paymentAndCashList.dtIniError,
                        dtEndError = paymentAndCashList.dtEndError,
                        dtCreatedError = paymentAndCashList.dtCreatedError,
                        jobError = paymentAndCashList.jobError
                    )
                }
                if (paymentAndCashList.cbCreateSheetError == InputError.EmptyCreate) {
                    _cbTextCreateSheetFormState.update {
                        ERROR_SELECT_AT_LEAST_ONE_OPTION_TO_CREATE
                    }
                }
                _loadingState.update { false }
                return@launch
            }

            var paymentList: List<List<Any>> = emptyList()
            var cashList: List<List<Any>> = emptyList()

            val paymentSuccess = when (paymentResult) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.ShowSnackbar(paymentResult.errorMessage))
                    false
                }
                is Resource.Success -> {
                    paymentList = paymentResult.data ?: emptyList()
                    true
                }
                is Resource.Disconnect -> {
                    _eventFlow.emit(UiEvent.OnLogout)
                    false
                }

                null -> false
            }

            val cashSuccess = when (cashResult) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.ShowSnackbar(cashResult.errorMessage))
                    false
                }
                is Resource.Success -> {
                    cashList = cashResult.data ?: emptyList()
                    true
                }
                null -> false
                is Resource.Disconnect -> {
                    _eventFlow.emit(UiEvent.OnLogout)
                    false
                }
            }

            if (!paymentSuccess || !cashSuccess) {
                _loadingState.update { false }
                return@launch
            }

            if (cashResult?.data.isNullOrEmpty() && paymentResult?.data.isNullOrEmpty()) {
                _eventFlow.emit(UiEvent.ShowSnackbar(EMPTY_PAYMENT_AND_CASH))
                _loadingState.update { false }
                return@launch
            }

            toExcel(paymentList, cashList)
        }
    }


    private suspend fun toExcel(paymentList: List<List<Any>>, cashList: List<List<Any>>) {

        withContext(Dispatchers.Default) {

            val result = excelUseCase(
                paymentList = paymentList,
                cashList = cashList,
                colors = colorsState.value,
                jobClient = jobState.value.jobClient,
                sheetsToCreate = checkboxesState.value
            )

            when (result) {
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(result.errorMessage)
                    )
                }
                is Resource.Success -> Unit
                is Resource.Disconnect -> {
                    _eventFlow.emit(UiEvent.OnLogout)
                }
            }
            _loadingState.update { false }
        }
    }
}