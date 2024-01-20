package feature_payments.data.repository

import core.data.remote.DBConnection
import core.util.Resource
import core.util.SimpleResource
import core.util.StringResources.CONNECTION_NULL
import core.util.StringResources.ERROR_UNKNOWN
import core.util.StringResources.JOB_LIST_IS_EMPTY
import core.util.StringResources.QUERY_ERROR
import feature_payments.data.datasource.ExcelQueries
import feature_payments.data.mapper.toJobDTOList
import feature_payments.data.mapper.toNestedList
import feature_payments.domain.models.JobOrder
import feature_payments.domain.repository.PaymentsRepository
import feature_payments.presentation.util.Client
import java.sql.SQLException


class PaymentsRepositoryImpl(
    private val dbConnection: DBConnection,
) : PaymentsRepository {
    override suspend fun getJobs(
        resultQuery: String,
    ): Resource<List<JobOrder>> {
        return try {

            val jobList = dbConnection.getJobs(
                resultQuery = resultQuery
            )?.toJobDTOList() ?: throw Exception(CONNECTION_NULL)

            if (jobList.isEmpty()) throw Exception(JOB_LIST_IS_EMPTY)

            Resource.Success(jobList)
        } catch (e: SQLException) {
            Resource.Error(CONNECTION_NULL)
        } catch (e: Exception) {
            Resource.Error(ERROR_UNKNOWN)
        } finally {
            dbConnection.closeConnection()
        }
    }

    override suspend fun getPaymentList(
        useDateCreate: Boolean,
        useAlternativeCode: Boolean,
        jobNumber: String,
        dtCreated: String?,
        dtIni: String,
        dtEnd: String,
    ): Resource<List<List<Any>>> {
        try {
            val resultQuery =
                ExcelQueries.getGeralResultQuery(useDateCreate = useDateCreate, useAlternativeCode = useAlternativeCode)

            val paymentList =
                dbConnection.getPayments(
                    resultQuery = resultQuery,
                    jobNumber = jobNumber,
                    dtCreated = dtCreated,
                    dtIni = dtIni,
                    dtEnd = dtEnd
                )?.toNestedList() ?: return Resource.Error(CONNECTION_NULL)

            return Resource.Success(paymentList)
        } catch (e: SQLException) {
            return Resource.Error(CONNECTION_NULL)
        } catch (e: Exception) {
            return Resource.Error(ERROR_UNKNOWN)
        }
    }

    override suspend fun getCashList(
        useDateCreate: Boolean,
        useAlternativeCode: Boolean,
        jobNumber: String,
        dtCreated: String?,
    ): Resource<List<List<Any>>> {
        try {
            val resultQuery =
                ExcelQueries.getCashResultQuery(useDateCreate = useDateCreate, useAlternativeCode = useAlternativeCode)

            val cashList =
                dbConnection.getCash(
                    resultQuery = resultQuery,
                    jobNumber = jobNumber,
                    dtCreated = dtCreated
                )?.toNestedList() ?: return Resource.Error(QUERY_ERROR)

            return Resource.Success(cashList)
        } catch (e: SQLException) {
            return Resource.Error(CONNECTION_NULL)
        } catch (e: Exception) {
            return Resource.Error(ERROR_UNKNOWN) //TODO: 3/12/2023 tomar cuidado p n mostrar dado de conexao
        }
    }

    override suspend fun closeConnection() {
            dbConnection.closeConnection()
    }
}