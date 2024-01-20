package feature_payments.presentation.util

enum class PaidType(
    var type: String,
) {
    Pagamento("Pagamento"),
    AP("AP"),
    Prestado("Prestado"),
    DevSaldo("Dev. Saldo"),
    Exec("Exec"),
    Pos("Pos"),
    Prod("Prod");
}
