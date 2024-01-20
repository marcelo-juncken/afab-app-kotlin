package feature_payments.domain.use_case.excel


import Excel.Column
import Excel.Range
import androidx.compose.ui.graphics.toArgb
import com.aspose.cells.FilterOperatorType
import core.util.Constants.WORKSHEET_DISCR_DEV_EXEC
import core.util.Constants.WORKSHEET_DISCR_DEV_POS
import core.util.Constants.WORKSHEET_DISCR_DEV_PROD
import core.util.Constants.WORKSHEET_DISCR_EXEC
import core.util.Constants.WORKSHEET_DISCR_POS
import core.util.Constants.WORKSHEET_DISCR_PROD
import core.util.Constants.WORKSHEET_NAME_EXEC
import core.util.Constants.WORKSHEET_NAME_GERAL
import core.util.Constants.WORKSHEET_NAME_POS
import core.util.Constants.WORKSHEET_NAME_PROD
import core.util.Resource
import core.util.SimpleResource
import feature_payments.domain.repository.ExcelRepository
import feature_payments.presentation.states.ColorsState

class SplitSheetsUseCase(
    private val excelRepository: ExcelRepository,
) {
    suspend operator fun invoke(
        colors: ColorsState,
        createExec: Boolean,
        createProd: Boolean,
        createPos: Boolean,
    ): SimpleResource {

        if (createExec) {
            val createExecResult = splitSheets(
                targetSheetName = WORKSHEET_NAME_EXEC,
                criteria1 = WORKSHEET_DISCR_EXEC,
                criteria2 = WORKSHEET_DISCR_DEV_EXEC,
                targetColor = colors.execColor.toArgb()
            )
            if (createExecResult is Resource.Error) return Resource.failed(createExecResult.errorMessage)
        }

        if (createProd) {
            val createProdResult = splitSheets(
                targetSheetName = WORKSHEET_NAME_PROD,
                criteria1 = WORKSHEET_DISCR_PROD,
                criteria2 = WORKSHEET_DISCR_DEV_PROD,
                targetColor = colors.prodColor.toArgb()
            )
            if (createProdResult is Resource.Error) return Resource.failed(createProdResult.errorMessage)
        }

        if (createPos) {
            val createPosResult = splitSheets(
                targetSheetName = WORKSHEET_NAME_POS,
                criteria1 = WORKSHEET_DISCR_POS,
                criteria2 = WORKSHEET_DISCR_DEV_POS,
                targetColor = colors.posColor.toArgb()
            )
            if (createPosResult is Resource.Error) return Resource.failed(createPosResult.errorMessage)
        }

        val resetGeralFilter =
            excelRepository.filterSheet(sheetName = WORKSHEET_NAME_GERAL, rangeAsString = Range.FILTER.range)
        if (resetGeralFilter is Resource.Error) return Resource.failed(resetGeralFilter.errorMessage)

        return Resource.succeeded
    }

    private suspend fun splitSheets(
        sourceSheetName: String = WORKSHEET_NAME_GERAL,
        targetSheetName: String,
        criteria1: String,
        criteria2: String = "",
        targetColor: Int,
    ): SimpleResource {
        val createTargetSheetResult = excelRepository.createSheet(targetSheetName)
        if (createTargetSheetResult is Resource.Error) return Resource.failed(createTargetSheetResult.errorMessage)

        val filterGeralResult = excelRepository.filterSheet(
            sheetName = sourceSheetName,
            rangeAsString = Range.FILTER.range,
            filterByColumnIndex = Column.DISCR.index,
            operatorType1 = FilterOperatorType.BEGINS_WITH,
            criteria1 = criteria1,
            isAnd = false,
            operatorType2 = FilterOperatorType.BEGINS_WITH,
            criteria2 = criteria2
        )
        if (filterGeralResult is Resource.Error) return Resource.failed(filterGeralResult.errorMessage)


        val endRowIndex = excelRepository.getLastVisibleRow(sourceSheetName).let { result ->
            if (result is Resource.Error) return Resource.failed(result.errorMessage)
            result.data
        }

        val copyDataResult = excelRepository.copyFilteredData(
            sourceSheetName = sourceSheetName,
            targetSheetName = targetSheetName,
            fromRowIndex = 0,
            toRowIndex = endRowIndex,
            fromColumnIndex = Column.firstColumnIndex,
            toColumnIndex = Column.lastColumnIndex
        )
        if (copyDataResult is Resource.Error) return Resource.failed(copyDataResult.errorMessage)

        val filterTargetSheetResult =
            excelRepository.filterSheet(sheetName = targetSheetName, rangeAsString = Range.FILTER.range)
        if (filterTargetSheetResult is Resource.Error) return Resource.failed(message = filterTargetSheetResult.errorMessage)

        val setSheetTabColor = excelRepository.setSheetTabColor(sheetName = targetSheetName, color = targetColor)
        return setSheetTabColor
    }
}