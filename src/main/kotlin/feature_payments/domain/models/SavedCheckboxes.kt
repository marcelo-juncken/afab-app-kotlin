package feature_payments.domain.models

data class SavedCheckboxes(
    val useDateCreate : Boolean = false,
    val useAlternativeCode : Boolean = false,
    val createGeral : Boolean = true,
    val createExec : Boolean = true,
    val createProd : Boolean = true,
    val createPos : Boolean = true,
    val createCash : Boolean = true,
    val sortGeral: Boolean = true,
    val sortExec : Boolean = true,
    val sortProd : Boolean = true,
    val sortPos : Boolean = true,
    val sortCash : Boolean = true,
)
