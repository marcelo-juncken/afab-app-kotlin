package core.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtil {
    private val inputFormatter = DateTimeFormatter.ofPattern("ddMMyyyy")
    private val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun transformDateFormat(date: String?): String {
        if (date.isNullOrBlank()) return ""

        val parsedDate = LocalDate.parse(date, inputFormatter)
        return parsedDate.format(outputFormatter)
    }

    fun plusOneDay(date: String?): String {
        if (date.isNullOrBlank()) return ""

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val parsedDate = LocalDate.parse(date, formatter)
        val datePlusOneDay = parsedDate.plusDays(1)
        return datePlusOneDay.format(formatter)
    }

    fun minusOneDay(date: String?): String {
        if (date.isNullOrBlank()) return ""

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val parsedDate = LocalDate.parse(date, formatter)
        val dateMinusOneDay = parsedDate.minusDays(1)
        return dateMinusOneDay.format(formatter)
    }

    fun isDateAfter(initialDate : String, endDate: String): Boolean {

        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        val transformedInitialDate = LocalDate.parse(initialDate, dateFormatter)
        val transformedEndDate = LocalDate.parse(endDate, dateFormatter)

        return transformedInitialDate.isAfter(transformedEndDate)
    }
}
