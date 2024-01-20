package feature_payments.data.local

import feature_payments.data.util.JsonParser

class Converters(
    private val jsonParser: JsonParser,
) {
    fun <T> fromJsonToState(json: String, type: Class<T>): T? {
        return jsonParser.fromJson(json, type)
    }

    fun <T> toJsonFromState(state: T, type: Class<T>): String {
        return jsonParser.toJson(state, type) ?: "{}"
    }
}