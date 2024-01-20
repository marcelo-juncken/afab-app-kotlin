package feature_payments.domain.models

import feature_payments.presentation.util.Client

data class JobOrder(
    val jobNumber: String?,
    val jobName: String?,
    val jobClient: Client?,
)

