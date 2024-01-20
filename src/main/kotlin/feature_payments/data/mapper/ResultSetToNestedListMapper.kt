package feature_payments.data.mapper

import java.sql.ResultSet
import Excel.Column

fun ResultSet.toNestedList(): List<List<Any>> {
    val payments = mutableListOf<List<Any>>()

    while (next()) {
        val rowList = listOf(
            getString(Column.TIPO.cellName),
            getDate(Column.DT_VENC.cellName),
            getDate(Column.DT_PAG.cellName),
            getDate(Column.DT_EMI.cellName),
            getString(Column.FORN.cellName),
            getString(Column.PLC.cellName),
            getString(Column.JOB.cellName),
            getString(Column.NO_NF.cellName),
            getDate(Column.DT_NF.cellName),
            getString(Column.CHEQUE.cellName),
            "",
            getString(Column.AP.cellName),
            getString(Column.ID.cellName),
            getString(Column.DISCR.cellName),
            "",
            getDouble(Column.VAL_LI.cellName),
            getString(Column.IPC.cellName),
            ""
        )
        payments.add(rowList)
    }

    return payments
}