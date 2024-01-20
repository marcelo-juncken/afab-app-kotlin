package feature_payments.presentation.util

enum class Client(val fileName: String? = null, val contentDescription: String = "") {
    AMAZON(fileName = "amazon.png", contentDescription = "Amazon"),
    NETFLIX(fileName = "netflix.png", contentDescription = "Netflix"),
    GLOBO(fileName = "globoPB.png", contentDescription = "Globo");
}

