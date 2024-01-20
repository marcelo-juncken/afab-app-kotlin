package core.util

import core.util.StringResources.ERROR_UNKNOWN

typealias SimpleResource = Resource<Unit>

sealed class Resource<T>(val data: T? = null) {
    class Success<T>(data: T, message: String? = null) : Resource<T>(data)
    data class Error<T>(val errorMessage: String) : Resource<T>(data = null)
    class Disconnect<T> : Resource<T>()

    companion object {
        val succeeded = Success(Unit)
        fun failed(message: String?) = Error<Unit>(message ?: ERROR_UNKNOWN)
    }
}