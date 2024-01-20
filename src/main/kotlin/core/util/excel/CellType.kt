package core.util.excel

sealed class CellType(
    val format: String,
) {
    object Header : CellType(format = "General")
    object SubTotal : CellType(format = "#.##0,00")
    object General : CellType(format = "General")
    object Legend : CellType(format = "#.##0,00")
    object Date : CellType(format = "dd/mm/yyyy")
    object Text : CellType(format = "@")
    object Number : CellType(format = "#.##0,00")

    companion object {
        val styleFactory = StyleFactory()
    }
}