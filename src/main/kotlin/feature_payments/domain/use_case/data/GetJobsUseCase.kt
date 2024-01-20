package feature_payments.domain.use_case.data

import core.util.Resource
import feature_payments.data.datasource.ExcelQueries
import feature_payments.domain.models.JobOrder
import feature_payments.domain.repository.PaymentsRepository

class GetJobsUseCase(
    private val paymentsRepository: PaymentsRepository,
) {
    suspend operator fun invoke(): Resource<List<JobOrder>> {
        return paymentsRepository.getJobs(resultQuery = ExcelQueries.getResultJobsQuery())
    }
}