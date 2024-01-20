package feature_auth.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

object Auth0Retrofit {
    private val properties: Properties by lazy {
        Properties().apply {
            val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream("auth0.properties")
            load(inputStream)
        }
    }

    private val BASE_URL = properties.getProperty("auth0.domain")
    val auth0Api: Auth0Api by lazy {
        Retrofit.Builder()
            .baseUrl("$BASE_URL/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Auth0Api::class.java)
    }
}