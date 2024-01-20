package feature_payments.domain.models

import androidx.compose.ui.graphics.Color

data class SavedColors(
    val pagamentoColor: Color = Color(255, 241, 118),
    val apColor: Color = Color(33, 150, 243),
    val prestadoColor: Color = Color(76, 175, 80),
    val devSaldoColor: Color = Color(211, 47, 47),
    val execColor: Color = Color(249, 168, 37),
    val prodColor: Color = Color(255, 0, 255),
    val posColor: Color = Color(25, 118, 210),
)