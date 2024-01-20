package core.data.remote

import java.sql.ResultSet

interface DBConnection {
    suspend fun closeConnection()

    suspend fun getJobs(resultQuery: String): ResultSet?

    suspend fun getPayments(
        resultQuery: String,
        jobNumber: String,
        dtCreated: String?,
        dtIni: String,
        dtEnd: String
    ): ResultSet?

    suspend fun getCash(
        resultQuery: String,
        jobNumber: String,
        dtCreated: String?
    ): ResultSet?

}