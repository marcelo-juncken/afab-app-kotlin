package feature_payments.data.mapper

import feature_payments.domain.models.SavedCheckboxes
import feature_payments.domain.models.SavedColors
import feature_payments.domain.models.SavedDates
import feature_payments.domain.models.SavedJob
import feature_payments.presentation.states.CheckboxesState
import feature_payments.presentation.states.ColorsState
import feature_payments.presentation.states.JobState
import feature_payments.presentation.states.TextFieldFormState

fun JobState.toSavedJob(): SavedJob {
    return SavedJob(
        jobNumber = this.jobNumber,
        jobName = this.jobName,
        jobClient = this.jobClient,
    )
}

fun SavedJob.toJobState(): JobState {
    return JobState(
        jobNumber = this.jobNumber,
        jobName = this.jobName,
        jobClient = this.jobClient,
    )
}

fun TextFieldFormState.toSavedDates(): SavedDates {
    return SavedDates(
        dtIni = this.dtIni,
        dtEnd = this.dtEnd,
        dtCreated = this.dtCreated,
    )
}

fun SavedDates.toTextFieldStates(): TextFieldFormState {
    return TextFieldFormState(
        dtIni = this.dtIni,
        dtEnd = this.dtEnd,
        dtCreated = this.dtCreated,
    )
}

fun CheckboxesState.toSavedCheckboxes(): SavedCheckboxes {
    return SavedCheckboxes(
        useDateCreate = useDateCreate,
        useAlternativeCode = useAlternativeCode,
        createGeral = createGeral,
        createExec = createExec,
        createProd = createProd,
        createPos = createPos,
        createCash = createCash,
        sortGeral = sortGeral,
        sortExec = sortExec,
        sortProd = sortProd,
        sortPos = sortPos,
        sortCash = sortCash
    )
}

fun SavedCheckboxes.toCheckboxesState(): CheckboxesState {
    return CheckboxesState(
        useDateCreate = useDateCreate,
        useAlternativeCode = useAlternativeCode,
        createGeral = createGeral,
        createExec = createExec,
        createProd = createProd,
        createPos = createPos,
        createCash = createCash,
        sortGeral = sortGeral,
        sortExec = sortExec,
        sortProd = sortProd,
        sortPos = sortPos,
        sortCash = sortCash
    )
}

fun ColorsState.toSavedColors(): SavedColors {
    return SavedColors(
        pagamentoColor = pagamentoColor,
        apColor = apColor,
        prestadoColor = prestadoColor,
        devSaldoColor = devSaldoColor,
        execColor = execColor,
        prodColor = prodColor,
        posColor = posColor
    )
}

fun SavedColors.toColorsState(): ColorsState {
    return ColorsState(
        pagamentoColor = pagamentoColor,
        apColor = apColor,
        prestadoColor = prestadoColor,
        devSaldoColor = devSaldoColor,
        execColor = execColor,
        prodColor = prodColor,
        posColor = posColor
    )
}