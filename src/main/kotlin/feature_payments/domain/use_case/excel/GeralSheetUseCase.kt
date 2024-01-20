package feature_payments.domain.use_case.excel

import Excel.Column
import Excel.Row
import Excel.columnsTitles
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import core.util.Constants.WORKSHEET_NAME_GERAL
import core.util.Resource
import core.util.SimpleResource
import core.util.excel.CellType
import feature_payments.data.datasource.ExcelQueries
import feature_payments.domain.repository.ExcelRepository
import feature_payments.presentation.states.ColorsState
import feature_payments.presentation.util.Client
import feature_payments.presentation.util.PaidType

class GeralSheetUseCase(
    private val excelRepository: ExcelRepository,
) {
    suspend operator fun invoke(paymentList: List<List<Any>>, colors: ColorsState, client: Client?): SimpleResource {

        excelRepository.createWorkbook().let { result ->
            if (result is Resource.Error) return result
        }

        excelRepository.createSheet(sheetName = WORKSHEET_NAME_GERAL).let { result ->
            if (result is Resource.Error) return Resource.failed(result.errorMessage)
        }

        val workbook = excelRepository.workbook

        CellType.styleFactory.createStylesForAllCellTypes(sheet = workbook!!.worksheets[WORKSHEET_NAME_GERAL])

        addHeadersToSheet(sheetName = WORKSHEET_NAME_GERAL).let { result ->
            if (result is Resource.Error) return result
        }

        excelRepository.addDataToSheet(
            sheetName = WORKSHEET_NAME_GERAL,
            data = paymentList,
            startRowIndex = Row.FIRST_DATA.index,
            startColumnIndex = Column.firstColumnIndex,
            fieldNameShown = true
        ).let { result -> if (result is Resource.Error) return result }

        val endRowIndex = excelRepository.getLastDataRowIndex(sheetName = WORKSHEET_NAME_GERAL).let { result ->
            if (result is Resource.Error) return Resource.failed(result.errorMessage)
            result.data!!
        }

        addDifIpcFormula(sheetName = WORKSHEET_NAME_GERAL, endRowIndex = endRowIndex, client = client).let { result ->
            if (result is Resource.Error) return result
        }

        formatColumns(sheetName = WORKSHEET_NAME_GERAL, endRowIndex = endRowIndex).let { result ->
            if (result is Resource.Error) return result
        }


        addSubtotal(sheetName = WORKSHEET_NAME_GERAL, endRowIndex = endRowIndex).let { result ->
            if (result is Resource.Error) return result
        }

        applyColors(
            sheetName = WORKSHEET_NAME_GERAL,
            paymentList = paymentList,
            colors = colors
        ).let { result ->
            if (result is Resource.Error) return result
        }

        return Resource.succeeded
    }

    private suspend fun addHeadersToSheet(sheetName: String): SimpleResource {
        val headerList = listOf(columnsTitles)

        return excelRepository.addDataToSheet(
            sheetName = sheetName,
            data = headerList,
            startRowIndex = Row.HEADER.index,
            startColumnIndex = Column.firstColumnIndex,
            style = CellType.styleFactory.getStyle(
                sheet = excelRepository.workbook!!.worksheets[sheetName],
                cellType = CellType.Header
            )
        )
    }

    private suspend fun applyColors(
        sheetName: String,
        paymentList: List<List<Any>>,
        colors: ColorsState,
    ): SimpleResource {

        val maxRowIndex: Int = paymentList.lastIndex + Row.FIRST_DATA.index

        for (i in Row.FIRST_DATA.index..maxRowIndex) {
            val paidType = paymentList[i - Row.FIRST_DATA.index][Column.TIPO.index]

            val color = when (paidType) {
                PaidType.Pagamento.type -> colors.pagamentoColor.toArgb()
                PaidType.AP.type -> colors.apColor.toArgb()
                PaidType.Prestado.type -> colors.prestadoColor.toArgb()
                PaidType.DevSaldo.type -> colors.devSaldoColor.toArgb()
                else -> Color.Black.toArgb()
            }
            val tintRow = excelRepository.tintRowBackground(
                sheetName = sheetName,
                fromRowIndex = i,
                toRowIndex = i,
                fromColumnIndex = Column.firstColumnIndex,
                toColumnIndex = Column.lastColumnIndex,
                color = color
            )
            if (tintRow is Resource.Error) return tintRow
        }

        return Resource.succeeded
    }

    private suspend fun addDifIpcFormula(sheetName: String, endRowIndex: Int, client: Client?): SimpleResource {
        val ipcDifFirstCellFormula = ExcelQueries.getGeralIpcDifFormula(client)

        return excelRepository.addFormulaToRange(
            sheetName = sheetName,
            formula = ipcDifFirstCellFormula,
            fromRowIndex = Row.FIRST_DATA.index,
            fromColumnIndex = Column.IPC_DIF.index,
            toRowIndex = endRowIndex,
            toColumnIndex = Column.IPC_DIF.index
        )
    }

    private suspend fun addSubtotal(sheetName: String, endRowIndex: Int): SimpleResource {
        val subTotalCellFormula = ExcelQueries.getSubtotalFormula(endRowIndex)

        return excelRepository.addFormulaToCell(
            sheetName = sheetName,
            formula = subTotalCellFormula,
            rowIndex = Row.SUBTOTAL.index,
            columnIndex = Column.SUBTOTAL.index,
            style = CellType.styleFactory.getStyle(
                sheet = excelRepository.workbook!!.worksheets[sheetName],
                cellType = CellType.SubTotal
            )
        )
    }

    private suspend fun formatColumns(sheetName: String, endRowIndex: Int): SimpleResource {
        for (column in Column.firstColumnIndex..Column.lastColumnIndex) {
            excelRepository.run {
                setColumnWidth(
                    sheetName = sheetName,
                    columnIndex = column,
                    width = Column.getColumnByIndex(column)!!.width
                ).takeIf { it is Resource.Error } ?: setStyleToRange(
                    sheetName = sheetName,
                    fromRowIndex = Row.FIRST_DATA.index,
                    toRowIndex = endRowIndex,
                    fromColumnIndex = column,
                    style = CellType.styleFactory.getStyle(
                        sheet = excelRepository.workbook!!.worksheets[sheetName],
                        cellType = Column.getColumnByIndex(column)!!.cellType
                    )
                ).takeIf { it is Resource.Error }
            }?.let { return it }
        }
        return Resource.succeeded
    }
}