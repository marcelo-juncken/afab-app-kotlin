
import com.aspose.cells.CellsHelper
import core.util.excel.CellType

object Excel {

    enum class Column(val cellName: String, val letter: String, val cellType: CellType, val width: Double = 10.0) {
        TIPO("TIPO", "A", CellType.Text),
        DT_VENC("DT VENC", "B", CellType.Date),
        DT_PAG("DT PAG", "C", CellType.Date),
        DT_EMI("DT EMI", "D", CellType.Date),
        FORN("FORNECEDOR", "E", CellType.Text, 13.0),
        PLC("PLANO DE CONTAS", "F", CellType.Text, 13.0),
        JOB("JOB", "G", CellType.Text, 8.0),
        NO_NF("No. N.F.", "H", CellType.Text, 8.0),
        DT_NF("DATA N.F.", "I", CellType.Date),
        CHEQUE("CHEQUE", "J", CellType.Text, 8.0),
        BLANK_K("", "K", CellType.Text, 2.0),
        AP("AP", "L", CellType.Text, 8.0),
        ID("ID", "M", CellType.Text, 8.0),
        DISCR("DISCRIMINAÇÃO", "N", CellType.Text, 16.0),
        BLANKO_O("", "O", CellType.Text, 2.0),
        VAL_LI("VALOR LÍQUIDO", "P", CellType.Number, 18.0),
        IPC("IPC", "Q", CellType.Text, 8.0),
        SUBTOTAL("", "P", CellType.SubTotal),
        IPC_DIF("DIF IPC", "R", CellType.General, 8.0);

        val index = CellsHelper.columnNameToIndex(letter)

        companion object {
            fun getColumnByIndex(index: Int) = values().find { it.index == index }
            val firstColumnIndex = TIPO.index
            val lastColumnIndex = IPC_DIF.index
        }
    }

    enum class Row(val rowNumber: Int) {
        SUBTOTAL(1),
        HEADER(2),
        FIRST_DATA(3);

        val index = rowNumber - 1

        companion object {
            const val height = 15.0
        }
    }

    enum class Range(val range: String) {
        FILTER("${Column.TIPO.letter}${Row.HEADER.rowNumber}:${Column.IPC_DIF.letter}${Row.HEADER.rowNumber}");
    }

    sealed class Legend(val columnLetter: String, rowNumber: Int, val style: CellType = CellType.Text) {
        sealed class Aba(val text: String = "", val rowNumber: Int, columnLetter: String = "S") :
            Legend(columnLetter = columnLetter, rowNumber = rowNumber) {
            object Header : Aba(text = "Aba", rowNumber = 3, columnLetter = "S")
            object Exec : Aba(text = "EXEC", rowNumber = 4)
            object Prod : Aba(text = "PROD", rowNumber = 5)
            object Pos : Aba(text = "POS", rowNumber = 6)
            object Total : Aba(text = "Total", rowNumber = 7)
            object Dif : Aba(text = "Dif", rowNumber = 8)

        }

        sealed class Soma(val text: String = "", val rowNumber: Int, columnLetter: String = "T") :
            Legend(columnLetter = columnLetter, rowNumber = rowNumber) {
            object Header : Soma(text = "Soma", rowNumber = 3, columnLetter = "T")
            object Exec : Soma(rowNumber = 4)
            object Prod : Soma(rowNumber = 5)
            object Pos : Soma(rowNumber = 6)
            object Total : Soma(rowNumber = 7)
            object Dif : Soma(rowNumber = 8)

        }

        companion object {
            const val firstRowIndex = 2
            fun firstColumnIndex() = CellsHelper.columnNameToIndex(Aba.Header.columnLetter)
        }

        val rowIndex = rowNumber - 1
        val columnIndex = CellsHelper.columnNameToIndex(columnLetter)
    }

    val columnsTitles = Column.values().filter { it != Column.SUBTOTAL }.map { it.name }.toMutableList()
}

sealed class JobQuery(val text: String) {
    object Number : JobQuery(text = "orc_job")
    object Name : JobQuery(text = "orc_filme")
    object Client : JobQuery(text = "orc_cliage")
}