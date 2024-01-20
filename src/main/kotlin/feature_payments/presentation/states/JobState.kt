package feature_payments.presentation.states

import feature_payments.presentation.util.Client

data class JobState(
    val jobNumber: String?,
    val jobName: String?,
    val jobClient: Client? = null,
)