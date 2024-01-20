package feature_payments.domain.use_case.excel

import Excel.Column
import Excel.Row
import com.aspose.cells.SortOrder
import core.util.Resource
import core.util.SimpleResource
import feature_payments.domain.repository.ExcelRepository

class SortSheetUseCase(
    private val excelRepository: ExcelRepository
) {
    suspend operator fun invoke(sourceSheetName: String, targetSheetName: String): SimpleResource {

        val getLastDataRowIndexResult =  excelRepository.getLastDataRowIndex(sourceSheetName)
        if (getLastDataRowIndexResult is Resource.Error) {
            return Resource.failed(getLastDataRowIndexResult.errorMessage)
        }
        if (getLastDataRowIndexResult.data!! <= Row.HEADER.index) return Resource.succeeded

        excelRepository.duplicateSheet(
            sourceSheetName = sourceSheetName,
            targetSheetName = targetSheetName
        ).let { result ->
            if (result is Resource.Error) return Resource.failed(result.errorMessage)
        }

        val lastDataRowIndex = excelRepository.getLastDataRowIndex(sheetName = targetSheetName).let { result ->
            if (result is Resource.Error) return Resource.failed(result.errorMessage)
            result.data!!
        }

        excelRepository.sortSheet(
            sheetNameToSort = targetSheetName,
            sortOrder = SortOrder.ASCENDING,
            sortColumnIndex = Column.IPC.index,
            startRowIndex = Row.FIRST_DATA.index,
            endRowIndex = lastDataRowIndex,
            startColumnIndex = Column.firstColumnIndex,
            endColumnIndex =Column.lastColumnIndex
        ).let { result ->
            if (result is Resource.Error) return result
        }

        excelRepository.applySubtotalToSheet(
            sheetName = targetSheetName,
            groupByColumnIndex = Column.IPC.index,
            totalColumnIndex = Column.VAL_LI.index,
            startRowIndex = Row.FIRST_DATA.index,
            endRowIndex = lastDataRowIndex,
            startColumnIndex = Column.firstColumnIndex,
            endColumnIndex = Column.lastColumnIndex
        ).let { result ->
            if (result is Resource.Error) return result
        }

        return Resource.succeeded
    }
}