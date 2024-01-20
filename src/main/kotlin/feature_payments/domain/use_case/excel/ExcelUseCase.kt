package feature_payments.domain.use_case.excel

import core.util.Constants.WORKSHEET_NAME_CASH
import core.util.Constants.WORKSHEET_NAME_CASH_POR_IPCS
import core.util.Constants.WORKSHEET_NAME_EXEC
import core.util.Constants.WORKSHEET_NAME_EXEC_POR_IPCS
import core.util.Constants.WORKSHEET_NAME_GERAL
import core.util.Constants.WORKSHEET_NAME_GERAL_POR_IPCS
import core.util.Constants.WORKSHEET_NAME_POS
import core.util.Constants.WORKSHEET_NAME_POS_POR_IPCS
import core.util.Constants.WORKSHEET_NAME_PROD
import core.util.Constants.WORKSHEET_NAME_PROD_POR_IPCS
import core.util.Resource
import core.util.SimpleResource
import feature_payments.presentation.util.Client
import feature_payments.presentation.states.CheckboxesState
import feature_payments.presentation.states.ColorsState

class ExcelUseCase(
    private val geralSheetUseCase: GeralSheetUseCase,
    private val cashSheetUseCase: CashSheetUseCase,
    private val sortSheetUseCase: SortSheetUseCase,
    private val splitSheetsUseCase: SplitSheetsUseCase,
    private val createLegendsUseCase: CreateLegendsUseCase,
    private val saveWorkbookUseCase: SaveWorkbookUseCase,
) {
    suspend operator fun invoke(
        paymentList: List<List<Any>>,
        cashList: List<List<Any>>,
        colors: ColorsState,
        jobClient: Client?,
        sheetsToCreate: CheckboxesState,
    ): SimpleResource {

        if (paymentList.isNotEmpty() && sheetsToCreate.createGeral) {
            createGeralSheet(
                paymentList = paymentList,
                colors = colors,
                jobClient = jobClient,
                sortGeral = sheetsToCreate.sortGeral
            ).let { result ->
                if (result is Resource.Error) return result
            }

            splitSheetsUseCase(
                colors = colors,
                createExec = sheetsToCreate.createExec,
                createProd = sheetsToCreate.createProd,
                createPos = sheetsToCreate.createPos
            ).let { result ->
                if (result is Resource.Error) return result
            }

            sortExecPosProd(
                sortExec = sheetsToCreate.sortExec,
                sortProd = sheetsToCreate.sortProd,
                sortPos = sheetsToCreate.sortPos
            ).let { result ->
                if (result is Resource.Error) return result
            }

            createLegendsUseCase(
                colors = colors,
                createExec = sheetsToCreate.createExec,
                createProd = sheetsToCreate.createProd,
                createPos = sheetsToCreate.createPos
            ).let { result ->
                if (result is Resource.Error) return result
            }
        }

        if (cashList.isNotEmpty() && sheetsToCreate.createCash) {
            createCashSheet(
                cashList = cashList,
                colors = colors,
                jobClient = jobClient,
                sortCash = sheetsToCreate.sortCash
            ).let { result ->
                if (result is Resource.Error) return result
            }
        }
        saveWorkbookUseCase(goToSheetIndex = 0).let { result ->
            if (result is Resource.Error) return result
        }
        return Resource.succeeded
    }

    private suspend fun createGeralSheet(
        paymentList: List<List<Any>>,
        colors: ColorsState,
        jobClient: Client?,
        sortGeral: Boolean,
    ): SimpleResource {
        val createGeralResult = geralSheetUseCase(paymentList = paymentList, colors = colors, client = jobClient)
        if (createGeralResult is Resource.Error) return createGeralResult

        if (sortGeral) {
            val sortSheetResult =
                sortSheetUseCase(
                    sourceSheetName = WORKSHEET_NAME_GERAL,
                    targetSheetName = WORKSHEET_NAME_GERAL_POR_IPCS
                )
            if (sortSheetResult is Resource.Error) return sortSheetResult
        }
        return Resource.succeeded
    }

    private suspend fun createCashSheet(
        cashList: List<List<Any>>,
        colors: ColorsState,
        jobClient: Client?,
        sortCash: Boolean,
    ): SimpleResource {
        val createCashResult = cashSheetUseCase(cashList = cashList, colors = colors, client = jobClient)
        if (createCashResult is Resource.Error) return createCashResult

        if(sortCash){
            val sortSheetResult =
                sortSheetUseCase(sourceSheetName = WORKSHEET_NAME_CASH, targetSheetName = WORKSHEET_NAME_CASH_POR_IPCS)
            if (sortSheetResult is Resource.Error) return sortSheetResult
        }

        return Resource.succeeded
    }

    private suspend fun sortExecPosProd(sortExec: Boolean, sortProd: Boolean, sortPos: Boolean): SimpleResource {
        if (sortExec) {
            val sortExecResult =
                sortSheetUseCase(sourceSheetName = WORKSHEET_NAME_EXEC, targetSheetName = WORKSHEET_NAME_EXEC_POR_IPCS)
            if (sortExecResult is Resource.Error) return sortExecResult
        }

        if (sortProd) {
            val sortProdResult =
                sortSheetUseCase(sourceSheetName = WORKSHEET_NAME_PROD, targetSheetName = WORKSHEET_NAME_PROD_POR_IPCS)
            if (sortProdResult is Resource.Error) return sortProdResult
        }

        if (sortPos) {
            val sortPosResult =
                sortSheetUseCase(sourceSheetName = WORKSHEET_NAME_POS, targetSheetName = WORKSHEET_NAME_POS_POR_IPCS)
            if (sortPosResult is Resource.Error) return sortPosResult
        }

        return Resource.succeeded
    }
}