package core.util

import core.util.Constants.MAX_DATE_LENGTH
import core.util.Constants.MAX_TEMPLATE_NAME_LENGTH
import feature_payments.presentation.util.InputError
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ValidationUtil {
    fun validateTemplateName(templateName: String?): InputError? {
        if (templateName.isNullOrBlank()) return InputError.EmptyField

        if (templateName.length > MAX_TEMPLATE_NAME_LENGTH) return InputError.MaxLengthError

        return null
    }

    fun validateCheckboxes(
        createGeral: Boolean,
        createCash: Boolean,
    ): InputError? {
        if (!createGeral && !createCash) return InputError.EmptyCreate

        return null
    }

    fun validateJob(jobList: List<String>?, jobSearchText: String, jobName: String?, jobNumber: String?): InputError? {

        if (jobList.isNullOrEmpty()) return InputError.EmptyList

        if (jobSearchText.isBlank() && jobSearchText.isBlank()) return InputError.EmptyField

        if (!jobList.contains("$jobNumber - $jobName")) return InputError.InvalidJob

        if (!jobList.contains(jobSearchText)) return InputError.InvalidJob

        return null
    }

    fun validateDate(date: String?): InputError? {

        if (date.isNullOrBlank()) return InputError.EmptyField

        if (date.length != MAX_DATE_LENGTH) return InputError.InvalidDate

        val dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy")

        val result = runCatching {
            LocalDate.parse(date, dateFormatter)
        }

        return when {
            result.isFailure -> InputError.InvalidDate
            result.getOrNull()?.let { it.year <= 2000 || it.year >= 3000 } == true -> InputError.InvalidYear
            else -> null
        }
    }

    fun checkIfInitialDateIsLaterThanEndDate(initialDate: String, endDate: String): InputError? {
        if (DateUtil.isDateAfter(initialDate, endDate)) return InputError.InitialDateLaterThanEndDate

        return null
    }

    fun checkIfInitialDateIsLaterThanCreatedDate(initialDate: String, createdDate: String): InputError? {
        if (DateUtil.isDateAfter(initialDate, createdDate)) return InputError.InitialDateLaterThanCreatedDate

        return null
    }
}