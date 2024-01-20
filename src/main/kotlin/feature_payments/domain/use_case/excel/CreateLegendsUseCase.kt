package feature_payments.domain.use_case.excel

import Excel
import Excel.Legend
import androidx.compose.ui.graphics.toArgb
import core.util.Constants.WORKSHEET_NAME_EXEC
import core.util.Constants.WORKSHEET_NAME_GERAL
import core.util.Constants.WORKSHEET_NAME_POS
import core.util.Constants.WORKSHEET_NAME_PROD
import core.util.Resource
import core.util.SimpleResource
import core.util.excel.CellType
import feature_payments.domain.repository.ExcelRepository
import feature_payments.presentation.states.ColorsState

class CreateLegendsUseCase(
    private val excelRepository: ExcelRepository,
) {
    suspend operator fun invoke(
        colors: ColorsState,
        createExec: Boolean,
        createProd: Boolean,
        createPos: Boolean,
    ): SimpleResource {

        val data = listOf(
            listOf(Legend.Aba.Header.text, Legend.Soma.Header.text),
            listOf(Legend.Aba.Exec.text, Legend.Soma.Exec.text),
            listOf(Legend.Aba.Prod.text, Legend.Soma.Prod.text),
            listOf(Legend.Aba.Pos.text, Legend.Soma.Pos.text),
            listOf(Legend.Aba.Total.text, Legend.Soma.Total.text),
            listOf(Legend.Aba.Dif.text, Legend.Soma.Dif.text),
        )

        val addLegendsResult = excelRepository.addDataToSheet(
            sheetName = WORKSHEET_NAME_GERAL,
            data = data,
            startRowIndex = Legend.firstRowIndex,
            startColumnIndex = Legend.firstColumnIndex(),
            style = CellType.styleFactory.getStyle(
                sheet = excelRepository.workbook!!.worksheets[WORKSHEET_NAME_GERAL],
                cellType = CellType.Legend

            )
        )
        if (addLegendsResult is Resource.Error) return addLegendsResult

        addFormulas(createExec = createExec, createProd = createProd, createPos = createPos)

        val tintLegendsResult = tintLegends(sheetName = WORKSHEET_NAME_GERAL, colors = colors)
        if (tintLegendsResult is Resource.Error) return tintLegendsResult

        return Resource.succeeded
    }

    private suspend fun addFormulas(createExec: Boolean, createProd: Boolean, createPos: Boolean): SimpleResource {
        if (createExec) {
            val execFormula = "$WORKSHEET_NAME_EXEC!${Excel.Column.SUBTOTAL.letter}${Excel.Row.SUBTOTAL.rowNumber}"
            excelRepository.addFormulaToCell(
                sheetName = WORKSHEET_NAME_GERAL,
                formula = execFormula,
                rowIndex = Legend.Soma.Exec.rowIndex,
                columnIndex = Legend.Soma.Exec.columnIndex
            ).let { result -> if (result is Resource.Error) return result }
        }

        if (createProd) {
            val prodFormula = "$WORKSHEET_NAME_PROD!${Excel.Column.SUBTOTAL.letter}${Excel.Row.SUBTOTAL.rowNumber}"
            excelRepository.addFormulaToCell(
                sheetName = WORKSHEET_NAME_GERAL,
                formula = prodFormula,
                rowIndex = Legend.Soma.Prod.rowIndex,
                columnIndex = Legend.Soma.Prod.columnIndex
            ).let { result -> if (result is Resource.Error) return result }
        }

        if (createPos) {
            val posFormula = "$WORKSHEET_NAME_POS!${Excel.Column.SUBTOTAL.letter}${Excel.Row.SUBTOTAL.rowNumber}"
            excelRepository.addFormulaToCell(
                sheetName = WORKSHEET_NAME_GERAL,
                formula = posFormula,
                rowIndex = Legend.Soma.Pos.rowIndex,
                columnIndex = Legend.Soma.Pos.columnIndex
            ).let { result -> if (result is Resource.Error) return result }
        }

        val totalFormula =
            "Sum(${Legend.Soma.Exec.columnLetter}${Legend.Soma.Exec.rowNumber}:${Legend.Soma.Pos.columnLetter}${Legend.Soma.Pos.rowNumber})"
        excelRepository.addFormulaToCell(
            sheetName = WORKSHEET_NAME_GERAL,
            formula = totalFormula,
            rowIndex = Legend.Soma.Total.rowIndex,
            columnIndex = Legend.Soma.Total.columnIndex
        ).let { result -> if (result is Resource.Error) return result }

        val difFormula =
            "${Excel.Column.SUBTOTAL.letter}${Excel.Row.SUBTOTAL.rowNumber}-${Legend.Soma.Total.columnLetter}${Legend.Soma.Total.rowNumber}"
        excelRepository.addFormulaToCell(
            sheetName = WORKSHEET_NAME_GERAL,
            formula = difFormula,
            rowIndex = Legend.Soma.Dif.rowIndex,
            columnIndex = Legend.Soma.Dif.columnIndex
        ).let { result -> if (result is Resource.Error) return result }

        return Resource.succeeded
    }

    private suspend fun tintLegends(sheetName: String, colors: ColorsState): SimpleResource {
        val tintExecText = excelRepository.tintRowBackground(
            sheetName = sheetName,
            fromRowIndex = Legend.Aba.Exec.rowIndex,
            fromColumnIndex = Legend.Aba.Exec.columnIndex,
            color = colors.execColor.toArgb()
        )
        if (tintExecText is Resource.Error) return tintExecText


        val tintProdText = excelRepository.tintRowBackground(
            sheetName = sheetName,
            fromRowIndex = Legend.Aba.Prod.rowIndex,
            fromColumnIndex = Legend.Aba.Prod.columnIndex,
            color = colors.prodColor.toArgb()
        )
        if (tintProdText is Resource.Error) return tintProdText

        val tintPosText = excelRepository.tintRowBackground(
            sheetName = sheetName,
            fromRowIndex = Legend.Aba.Pos.rowIndex,
            fromColumnIndex = Legend.Aba.Pos.columnIndex,
            color = colors.posColor.toArgb()
        )
        if (tintPosText is Resource.Error) return tintPosText

        return Resource.succeeded
    }
}