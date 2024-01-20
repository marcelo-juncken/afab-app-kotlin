package feature_auth.data.repository.datasource

import org.litote.kmongo.coroutine.CoroutineDatabase

interface IDatabaseConnection {
    val database: CoroutineDatabase
    fun close()
}