package feature_payments.domain.repository

import com.aspose.cells.ConsolidationFunction
import com.aspose.cells.Style
import com.aspose.cells.Workbook
import core.util.Resource
import core.util.SimpleResource

interface ExcelRepository {

    var workbook: Workbook?

    suspend fun createWorkbook(): SimpleResource

    suspend fun createSheet(
        sheetName: String? = null
    ): Resource<Int>

    suspend fun saveWorkbook(fileName: String, goToSheetIndex : Int? = null): SimpleResource

    suspend fun addDataToSheet(
        sheetName: String,
        data: List<List<Any>>,
        startRowIndex: Int,
        startColumnIndex: Int,
        fieldNameShown: Boolean = false,
        style: Style? = null
    ): SimpleResource

    suspend fun addFormulaToCell(
        sheetName: String,
        formula: String,
        rowIndex: Int,
        columnIndex: Int,
        style: Style? = null
    ): SimpleResource

    suspend fun addFormulaToRange(
        sheetName: String,
        formula: String,
        fromRowIndex: Int,
        toRowIndex: Int,
        fromColumnIndex: Int,
        toColumnIndex: Int,
        style: Style? = null
    ): SimpleResource

    suspend fun duplicateSheet(
        sourceSheetName: String,
        targetSheetName: String
    ): Resource<Int>

    suspend fun sortSheet(
        sheetNameToSort: String,
        sortOrder: Int,
        sortColumnIndex: Int,
        startRowIndex: Int,
        endRowIndex: Int,
        startColumnIndex: Int,
        endColumnIndex: Int
    ): SimpleResource

    suspend fun applySubtotalToSheet(
        sheetName: String,
        groupByColumnIndex: Int,
        totalColumnIndex: Int,
        startRowIndex: Int,
        endRowIndex: Int,
        startColumnIndex: Int,
        endColumnIndex: Int,
        consolidationFunction: Int = ConsolidationFunction.SUM
    ): SimpleResource

    suspend fun setColumnWidth(
        sheetName: String,
        columnIndex: Int,
        width: Double
    ): SimpleResource

    suspend fun setStyleToRange(
        sheetName: String,
        fromRowIndex: Int,
        toRowIndex: Int? = null,
        fromColumnIndex: Int,
        toColumnIndex: Int? = null,
        style: Style
    ): SimpleResource

    suspend fun filterSheet(
        sheetName: String,
        rangeAsString: String,
        filterByColumnIndex: Int = -1,
        operatorType1: Int = -1,
        criteria1: String = "",
        isAnd: Boolean = true,
        operatorType2: Int = -1,
        criteria2: String = "",
    ): SimpleResource

    suspend fun getLastDataRowIndex(
        sheetName: String,
        columnIndex: Int = -1
    ): Resource<Int>

    suspend fun getLastVisibleRow(
        sheetName: String
    ): Resource<Int>

    suspend fun freezePane(
        sheetName: String,
        row: Int,
        column: Int,
        freezedRows: Int,
        freezedColumns: Int
    ): SimpleResource

    suspend fun copyFilteredData(
        sourceSheetName: String,
        targetSheetName: String,
        fromRowIndex: Int,
        toRowIndex: Int?,
        fromColumnIndex: Int,
        toColumnIndex: Int?,
    ): SimpleResource

    suspend fun tintRowBackground(
        sheetName: String,
        fromRowIndex: Int,
        toRowIndex: Int? = null,
        fromColumnIndex: Int,
        toColumnIndex: Int? = null,
        color: Int
    ): SimpleResource

    suspend fun setSheetTabColor(
        sheetName: String,
        color: Int
    ): SimpleResource
}