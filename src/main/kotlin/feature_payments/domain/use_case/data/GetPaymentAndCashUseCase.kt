package feature_payments.domain.use_case.data

import core.util.DateUtil
import core.util.Resource
import core.util.ValidationUtil
import feature_auth.domain.use_case.CheckLoggedInUserUseCase
import feature_payments.domain.models.JobOrder
import feature_payments.domain.models.PaymentListResult
import feature_payments.domain.repository.PaymentsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetPaymentAndCashUseCase(
    private val paymentsRepository: PaymentsRepository,
    private val checkLoggedInUserUseCase: CheckLoggedInUserUseCase,
) {
    suspend operator fun invoke(
        jobList: List<JobOrder>?,
        jobSearchText: String,
        jobNumber: String?,
        jobName: String?,
        dtCreated: String?,
        dtIni: String?,
        dtEnd: String?,
        useDateCreate: Boolean,
        useAlternativeCode: Boolean,
        createGeral: Boolean,
        createCash: Boolean,
    ): PaymentListResult {

        val dtIniError = ValidationUtil.validateDate(date = dtIni)
        var dtEndError = ValidationUtil.validateDate(date = dtEnd)
        var dtCreatedError = if (useDateCreate) ValidationUtil.validateDate(date = dtCreated) else null

        val transformedDtIni = dtIniError?.let { "" } ?: DateUtil.minusOneDay(DateUtil.transformDateFormat(dtIni))
        val transformedDtEnd = dtEndError?.let { "" } ?: DateUtil.plusOneDay(DateUtil.transformDateFormat(dtEnd))
        val transformedDtCreated = if (dtCreatedError == null && useDateCreate) {
            DateUtil.plusOneDay(DateUtil.transformDateFormat(dtCreated))
        } else {
            ""
        }

        val createSheetsError = ValidationUtil.validateCheckboxes(createGeral = createGeral, createCash = createCash)

        val jobError = ValidationUtil.validateJob(
            jobList = jobList?.map { "${it.jobNumber} - ${it.jobName}" },
            jobSearchText = jobSearchText,
            jobNumber = jobNumber,
            jobName = jobName
        )

        if (dtIniError == null && dtEndError == null) {
            dtEndError = ValidationUtil.checkIfInitialDateIsLaterThanEndDate(
                initialDate = transformedDtIni,
                endDate = transformedDtEnd
            )
        }

        if (dtIniError == null && dtCreatedError == null && useDateCreate) {
            dtCreatedError = ValidationUtil.checkIfInitialDateIsLaterThanCreatedDate(
                initialDate = transformedDtIni,
                createdDate = transformedDtCreated
            )
        }

        val hasError = listOf(
            dtIniError,
            dtEndError,
            dtCreatedError,
            jobError,
            createSheetsError
        ).any { it != null }

        if (hasError) {
            return PaymentListResult(
                dtIniError = dtIniError,
                dtEndError = dtEndError,
                dtCreatedError = dtCreatedError,
                jobError = jobError,
                cbCreateSheetError = createSheetsError
            )
        }

        val user = checkLoggedInUserUseCase()
        if (user is Resource.Error) return PaymentListResult(paymentResult = Resource.Error(user.errorMessage))

        return coroutineScope {
            val paymentResultDeferred = if (!createGeral) {
                null
            } else {
                async {
                    paymentsRepository.getPaymentList(
                        useDateCreate = useDateCreate,
                        useAlternativeCode = useAlternativeCode,
                        jobNumber = jobNumber!!,
                        dtCreated = if (useDateCreate) transformedDtCreated else null,
                        dtIni = transformedDtIni,
                        dtEnd = transformedDtEnd
                    )
                }
            }

            val cashResultDeferred = if (!createCash) {
                null
            } else {
                async {
                    paymentsRepository.getCashList(
                        useDateCreate = useDateCreate,
                        useAlternativeCode = useAlternativeCode,
                        jobNumber = jobNumber!!,
                        dtCreated = if (useDateCreate) transformedDtCreated else null
                    )
                }
            }
            val paymentResult = paymentResultDeferred?.await()
            val cashResult = cashResultDeferred?.await()

            paymentsRepository.closeConnection()

            PaymentListResult(
                paymentResult = if (paymentResultDeferred == null) Resource.Success(emptyList()) else paymentResult,
                cashResult = if (cashResultDeferred == null) Resource.Success(emptyList()) else cashResult,
            )
        }
    }
}