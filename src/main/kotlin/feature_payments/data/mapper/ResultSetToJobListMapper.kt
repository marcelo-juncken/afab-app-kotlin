package feature_payments.data.mapper

import JobQuery
import feature_payments.presentation.util.Client
import feature_payments.domain.models.JobOrder
import java.sql.ResultSet

fun ResultSet.toJobDTOList(): List<JobOrder> {
    val jobList = mutableListOf<JobOrder>()
    while (next()) {

        val client = when (getString(JobQuery.Client.text)) {
            Client.NETFLIX.name -> {
                Client.NETFLIX
            }
            Client.AMAZON.name -> {
                Client.AMAZON
            }
            Client.GLOBO.name -> {
                Client.GLOBO
            }
            else -> null
        }

        val jobQuery = JobOrder(
            jobNumber = getString(JobQuery.Number.text),
            jobName = getString(JobQuery.Name.text),
            jobClient = client
        )

        jobList.add(jobQuery)
    }
    return jobList
}