package core.data.remote

import java.io.InputStream
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.*

object JDBCConnection : DBConnection {
    private var connection: Connection? = null

    private fun loadProperties(): Properties {
        val properties = Properties()
        val inputStream: InputStream? = javaClass.getResourceAsStream("/jdbc.properties")
        inputStream.use {
            properties.load(it)
        }
        return properties
    }

    private fun openConnection() {
        val properties = loadProperties()
        val url = properties.getProperty("database.url")
        val username = properties.getProperty("database.username")
        val password = properties.getProperty("database.password")
        connection = DriverManager.getConnection(url, username, password)
    }

    override suspend fun closeConnection() {
        connection?.close()
    }

    override suspend fun getJobs(resultQuery: String): ResultSet? {
        connection.let {
            if (it == null || it.isClosed) openConnection()
        }

        return connection?.let {
            val statement = it.prepareStatement(resultQuery)
            statement.executeQuery()
        }
    }

    override suspend fun getPayments(
        resultQuery: String,
        jobNumber: String,
        dtCreated: String?,
        dtIni: String,
        dtEnd: String,
    ): ResultSet? {
        connection.let {
            if (it == null || it.isClosed) openConnection()
        }

        return connection?.let {
            val statement = it.prepareStatement(resultQuery)
            var paramIndex = 1

            for (i in 1..4) {

                if (i == 2) {
                    statement.setString(paramIndex++, dtIni)
                    statement.setString(paramIndex++, dtEnd)

                    dtCreated?.let {
                        statement.setString(paramIndex++, dtCreated)
                    }
                }

                statement.setString(paramIndex++, jobNumber)

                dtCreated?.let {
                    statement.setString(paramIndex++, dtCreated)
                }

                statement.setString(paramIndex++, dtIni)
                statement.setString(paramIndex++, dtEnd)

            }
            statement.executeQuery()
        }
    }

    override suspend fun getCash(resultQuery: String, jobNumber: String, dtCreated: String?): ResultSet? {
        connection.let {
            if (it == null || it.isClosed) openConnection()
        }

        return connection?.let {
            val statement = it.prepareStatement(resultQuery)
            var paramIndex = 1

            for (i in 1..4) {
                if (i == 2) {
                    dtCreated?.let {
                        statement.setString(paramIndex++, dtCreated)
                    }
                }

                statement.setString(paramIndex++, jobNumber)

                dtCreated?.let {
                    statement.setString(paramIndex++, dtCreated)
                }
            }
            statement.executeQuery()
        }
    }
}