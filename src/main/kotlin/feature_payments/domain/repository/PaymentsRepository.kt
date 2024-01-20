package feature_payments.domain.repository

import core.util.Resource
import core.util.SimpleResource
import feature_payments.domain.models.JobOrder

interface PaymentsRepository {

    suspend fun getJobs(
        resultQuery: String
    ): Resource<List<JobOrder>>

    suspend fun getPaymentList(
        useDateCreate: Boolean,
        useAlternativeCode: Boolean,
        jobNumber: String,
        dtCreated: String?,
        dtIni: String,
        dtEnd: String,
    ): Resource<List<List<Any>>>

    suspend fun getCashList(
        useDateCreate: Boolean,
        useAlternativeCode: Boolean,
        jobNumber: String,
        dtCreated: String?
    ): Resource<List<List<Any>>>

    suspend fun closeConnection()
}