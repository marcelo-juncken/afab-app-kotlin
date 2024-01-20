package feature_auth.data.repository.datasource

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.reactivestreams.KMongo

class MongoDBConnectionImpl(
    dbName: String,
    user: String,
    password: String,
    domain: String,
) : IDatabaseConnection {

    private val connectionString: ConnectionString by lazy {ConnectionString("mongodb+srv://$user:$password@$dbName.$domain/?retryWrites=true&w=majority") }

    private val settings: MongoClientSettings by lazy {
        MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .serverApi(
                ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build()
            )
            .build()
    }

    private val mongoClient by lazy {
        KMongo.createClient(settings).coroutine
    }
    override val database: CoroutineDatabase by lazy {
        mongoClient.getDatabase(dbName)
    }

    override fun close() {
        mongoClient.close()
    }
}
