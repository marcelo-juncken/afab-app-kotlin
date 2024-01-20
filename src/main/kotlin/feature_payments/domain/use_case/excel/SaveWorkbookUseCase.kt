package feature_payments.domain.use_case.excel

import core.util.Constants.WORKBOOK_NAME
import core.util.Resource
import core.util.SimpleResource
import feature_payments.domain.repository.ExcelRepository

class SaveWorkbookUseCase(
    private val excelRepository: ExcelRepository
) {

    suspend operator fun invoke(goToSheetIndex: Int? = null) : SimpleResource {
        excelRepository.saveWorkbook(fileName = WORKBOOK_NAME, goToSheetIndex = goToSheetIndex).let { result ->
            if (result is Resource.Error) return result
        }
        return Resource.succeeded
    }
}