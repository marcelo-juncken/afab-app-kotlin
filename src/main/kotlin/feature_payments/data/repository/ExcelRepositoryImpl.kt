package feature_payments.data.repository

import com.aspose.cells.*
import core.util.Constants
import core.util.Constants.WORKBOOK_DEFAULT_SHEET
import core.util.Resource
import core.util.SimpleResource
import core.util.StringResources.ERROR_FILE_ALREADY_OPENED
import core.util.StringResources.ERROR_UNKNOWN
import feature_payments.domain.repository.ExcelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileChannel


class ExcelRepositoryImpl : ExcelRepository {
    override var workbook: Workbook? = null
    private val colorStyleMap = mutableMapOf<Int, Style>()

    companion object {
        val CELL_SHADING_FLAG = StyleFlag()
    }

    init {
        CELL_SHADING_FLAG.cellShading = true
    }

    private fun clearWorkbook() {
        workbook?.dispose()
        workbook = null
        colorStyleMap.clear()
    }

    private fun checkIfFileIsOpenedByAnotherProgram(): SimpleResource {
        try {
            val file = File(Constants.WORKBOOK_NAME)
            if (!file.exists()) {
                return Resource.succeeded
            }

            if (!file.canWrite()) return Resource.failed("Arquivo n pode escrever.")
            val randomAccessFile = RandomAccessFile(file, "rw")
            val channel: FileChannel = randomAccessFile.channel

            val lock = channel.tryLock()
                ?: // File is already locked
                return Resource.failed("Arquivo LOCKADO.")
            lock.release()
            println("SUCCESS")
            return Resource.succeeded
        } catch (e: FileNotFoundException) {
            return Resource.failed("Arquivo aberto.")
        } catch (e: Exception) {
            return Resource.failed(e.javaClass.simpleName)
        }
    }

    override suspend fun createWorkbook(): SimpleResource {
        return try {
            clearWorkbook()
            workbook = Workbook()
            workbook!!.settings.region = 55

            Resource.succeeded
        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_UNKNOWN)
        }
    }

    override suspend fun createSheet(
        sheetName: String?,
    ): Resource<Int> {
        try {
            val sheets = workbook!!.worksheets

            if (sheetName == null) {
                return Resource.Success(sheets.add())
            } else {
                sheets[WORKBOOK_DEFAULT_SHEET]?.let { sheet ->
                    sheet.name = sheetName
                    return Resource.Success(sheet.index)
                }
                sheets[sheetName]?.let {
                    return Resource.Success(it.index)
                }
                return Resource.Success(sheets.add(sheetName).index)
            }
        } catch (e: Exception) {
            return Resource.Error(e.message ?: ERROR_UNKNOWN)
        }
    }

    override suspend fun saveWorkbook(fileName: String, goToSheetIndex: Int?): SimpleResource {
        return try {
            if (goToSheetIndex != null) workbook!!.worksheets[goToSheetIndex].isSelected = true

            workbook!!.calculateFormula()
            workbook!!.save(fileName)

            val file = File(fileName)
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                withContext(Dispatchers.IO) {
                    Desktop.getDesktop().open(file)
                }
            }
            Resource.succeeded
        } catch (e: FileNotFoundException) {
            Resource.Error(ERROR_FILE_ALREADY_OPENED)
        } catch (e: IOException) {
            Resource.Error(e.message ?: ERROR_UNKNOWN)
        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_UNKNOWN)
        } finally {
            clearWorkbook()
        }
    }

    override suspend fun addDataToSheet(
        sheetName: String,
        data: List<List<Any>>,
        startRowIndex: Int,
        startColumnIndex: Int,
        fieldNameShown: Boolean,
        style: Style?,
    ): SimpleResource {
        return try {
            val sheet = workbook!!.worksheets[sheetName]
            sheet.cells.importTwoDimensionArray(
                data.map { it.toTypedArray() }.toTypedArray(),
                startRowIndex,
                startColumnIndex
            )
            if (style != null) {
                return setStyleToRange(
                    sheetName = sheetName,
                    fromRowIndex = startRowIndex,
                    toRowIndex = startRowIndex + data.size,
                    fromColumnIndex = startColumnIndex,
                    toColumnIndex = startColumnIndex + data[0].size,
                    style = style
                )
            }
            Resource.succeeded
        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_UNKNOWN)
        }
    }

    override suspend fun addFormulaToCell(
        sheetName: String,
        formula: String,
        rowIndex: Int,
        columnIndex: Int,
        style: Style?,
    ): SimpleResource {
        return addFormulaToRange(
            sheetName = sheetName,
            formula = formula,
            fromRowIndex = rowIndex,
            toRowIndex = rowIndex,
            fromColumnIndex = columnIndex,
            toColumnIndex = columnIndex,
            style = style
        )
    }

    override suspend fun addFormulaToRange(
        sheetName: String,
        formula: String,
        fromRowIndex: Int,
        toRowIndex: Int,
        fromColumnIndex: Int,
        toColumnIndex: Int,
        style: Style?,
    ): SimpleResource {
        return try {
            val sheet = workbook!!.worksheets[sheetName]
            val firstCell = sheet.cells.get(fromRowIndex, fromColumnIndex)
            firstCell.formula = formula

            for (rowIndex in fromRowIndex..toRowIndex) {
                for (columnIndex in fromColumnIndex..toColumnIndex) {
                    val cell = sheet.cells.get(rowIndex, columnIndex)
                    cell.copy(firstCell)
                }
            }

            if (style != null) {
                return setStyleToRange(
                    sheetName = sheetName,
                    fromRowIndex = fromRowIndex,
                    toRowIndex = toRowIndex,
                    fromColumnIndex = fromColumnIndex,
                    toColumnIndex = toColumnIndex,
                    style = style
                )
            }

            Resource.succeeded
        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_UNKNOWN)
        }
    }

    override suspend fun duplicateSheet(
        sourceSheetName: String,
        targetSheetName: String,
    ): Resource<Int> {
        return try {
            val sheetCollection: WorksheetCollection = workbook!!.worksheets
            val newWorksheetIndex: Int = sheetCollection.addCopy(sourceSheetName)
            workbook!!.worksheets[newWorksheetIndex].name = targetSheetName
            Resource.Success(newWorksheetIndex)
        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_UNKNOWN)
        }
    }

    override suspend fun sortSheet(
        sheetNameToSort: String,
        sortOrder: Int,
        sortColumnIndex: Int,
        startRowIndex: Int,
        endRowIndex: Int,
        startColumnIndex: Int,
        endColumnIndex: Int,
    ): SimpleResource {
        return try {
            val sheet = workbook!!.worksheets[sheetNameToSort]
            val sorter = sheet.workbook.dataSorter
            sorter.order1 = sortOrder
            sorter.key1 = sortColumnIndex

            val cells = sheet.cells

            sorter.sort(cells, startRowIndex, startColumnIndex, endRowIndex, endColumnIndex)
            Resource.succeeded
        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_UNKNOWN)
        }
    }

    override suspend fun applySubtotalToSheet(
        sheetName: String,
        groupByColumnIndex: Int,
        totalColumnIndex: Int,
        startRowIndex: Int,
        endRowIndex: Int,
        startColumnIndex: Int,
        endColumnIndex: Int,
        consolidationFunction: Int,
    ): SimpleResource {
        return try {
            val sheet = workbook!!.worksheets[sheetName]
            val cells = sheet.cells

            val cellArea = CellArea().apply {
                StartRow = startRowIndex
                EndRow = endRowIndex
                StartColumn = startColumnIndex
                EndColumn = endColumnIndex
            }

            cells.subtotal(
                cellArea,
                groupByColumnIndex,
                consolidationFunction,
                intArrayOf(totalColumnIndex),
                true,
                true,
                true
            )
            Resource.succeeded
        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_UNKNOWN)
        }
    }


    override suspend fun setColumnWidth(
        sheetName: String,
        columnIndex: Int,
        width: Double,
    ): SimpleResource {
        return try {
            val sheet = workbook!!.worksheets[sheetName]
            sheet.cells.setColumnWidth(columnIndex, width)
            Resource.succeeded
        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_UNKNOWN)
        }

    }

    override suspend fun setStyleToRange(
        sheetName: String,
        fromRowIndex: Int,
        toRowIndex: Int?,
        fromColumnIndex: Int,
        toColumnIndex: Int?,
        style: Style,
    ): SimpleResource {
        return try {
            val sheet = workbook!!.worksheets[sheetName]
            val fromRow = fromRowIndex + 1
            val toRow = (toRowIndex ?: fromRowIndex) + 1
            val fromColumnAsLetter = CellsHelper.columnIndexToName(fromColumnIndex)
            val toColumnAsLetter = CellsHelper.columnIndexToName(toColumnIndex ?: fromColumnIndex)

            val fromRange = "$fromColumnAsLetter$fromRow"
            val toRange = "$toColumnAsLetter$toRow"

            val range = sheet.cells.createRange(/* upperLeftCell = */ fromRange,/* lowerRightCell = */ toRange)
            range.setStyle(style)
            Resource.succeeded
        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_UNKNOWN)
        }
    }

    override suspend fun filterSheet(
        sheetName: String,
        rangeAsString: String,
        filterByColumnIndex: Int,
        operatorType1: Int,
        criteria1: String,
        isAnd: Boolean,
        operatorType2: Int,
        criteria2: String,

        ): SimpleResource {
        return try {
            val autoFilter = workbook!!.worksheets[sheetName].autoFilter

            autoFilter?.let {
                it.showAll()
                it.range = rangeAsString

                if (filterByColumnIndex >= 0) it.custom(
                    filterByColumnIndex,
                    operatorType1,
                    criteria1,
                    isAnd,
                    operatorType2,
                    criteria2
                )
                it.refresh()
            }

            return Resource.succeeded
        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_UNKNOWN)
        }
    }

    override suspend fun getLastDataRowIndex(
        sheetName: String,
        columnIndex: Int,
    ): Resource<Int> {
        try {
            val cells = workbook!!.worksheets[sheetName].cells

            if (columnIndex >= 0) {
                return Resource.Success(cells.getLastDataRow(columnIndex))
            }
            return Resource.Success(cells.maxDataRow)

        } catch (e: Exception) {
            return Resource.Error(e.message ?: ERROR_UNKNOWN)
        }

    }

    override suspend fun getLastVisibleRow(
        sheetName: String,
    ): Resource<Int> {
        return try {
            val cells = workbook!!.worksheets[sheetName].cells

            var lastVisibleRow: Int = cells.maxDataRow
            for (i in lastVisibleRow downTo 0) {
                if (!cells.rows[i].isHidden) {
                    lastVisibleRow = i
                    break
                }
            }
            Resource.Success(lastVisibleRow)

        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_UNKNOWN)
        }

    }

    override suspend fun freezePane(
        sheetName: String,
        row: Int,
        column: Int,
        freezedRows: Int,
        freezedColumns: Int,
    ): SimpleResource {
        return try {
            val sheet = workbook!!.worksheets[sheetName]
            sheet.freezePanes(row, column, freezedRows, freezedColumns)
            Resource.succeeded
        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_UNKNOWN)
        }
    }

    override suspend fun copyFilteredData(
        sourceSheetName: String,
        targetSheetName: String,
        fromRowIndex: Int,
        toRowIndex: Int?,
        fromColumnIndex: Int,
        toColumnIndex: Int?,
    ): SimpleResource {

        return try {
            val sourceSheet = workbook!!.worksheets[sourceSheetName]
            val targetSheet = workbook!!.worksheets[targetSheetName]

            val fromRow = fromRowIndex + 1
            val toRow = (toRowIndex ?: fromRowIndex) + 1
            val fromColumnAsLetter = CellsHelper.columnIndexToName(fromColumnIndex)
            val toColumnAsLetter = CellsHelper.columnIndexToName(toColumnIndex ?: fromColumnIndex)

            val fromRangeString = "$fromColumnAsLetter$fromRow"
            val toRangeString = "$toColumnAsLetter$toRow"

            val sourceRange = sourceSheet.cells.createRange(fromRangeString, toRangeString)
            val targetRange = targetSheet.cells.createRange(fromRangeString, toRangeString)

            val pasteOptions = PasteOptions().apply {
                onlyVisibleCells = true
            }
            targetRange.copy(sourceRange, pasteOptions)

            targetSheet.cells.deleteBlankRows()

            sourceSheet.autoFitRows()
            targetSheet.autoFitRows()

            Resource.succeeded
        } catch (e: Exception) {
            Resource.failed(e.message)
        }
    }

    override suspend fun tintRowBackground(
        sheetName: String,
        fromRowIndex: Int,
        toRowIndex: Int?,
        fromColumnIndex: Int,
        toColumnIndex: Int?,
        color: Int,
    ): SimpleResource {
        return try {
            val fromRow = fromRowIndex + 1
            val toRow = (toRowIndex ?: fromRowIndex) + 1
            val fromColumn = CellsHelper.columnIndexToName(fromColumnIndex)
            val toColumn = CellsHelper.columnIndexToName(toColumnIndex ?: fromColumnIndex)

            val sheet = workbook!!.worksheets[sheetName]
            val style = addStyleIfMissing(color)

            for (i in fromRow..toRow) {

                val fromRange = "$fromColumn$i"
                val toRange = "$toColumn$i"

                sheet.cells.createRange(fromRange, toRange).applyStyle(style, CELL_SHADING_FLAG)
            }
            Resource.succeeded
        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_UNKNOWN)
        }
    }

    override suspend fun setSheetTabColor(sheetName: String, color: Int): SimpleResource {
        return try {
            val sheet = workbook!!.worksheets[sheetName]

            sheet.tabColor = Color.fromArgb(color)
            Resource.succeeded
        } catch (e: Exception) {
            Resource.Error(e.message ?: ERROR_UNKNOWN)
        }

    }

    private fun addStyleIfMissing(color: Int): Style {

        val existingStyle = colorStyleMap[color]

        if (existingStyle != null) {
            return existingStyle
        }

        val newStyle = workbook!!.createStyle().apply {
            pattern = FillPattern.SOLID
            foregroundArgbColor = color
        }

        colorStyleMap[color] = newStyle

        return newStyle
    }
}