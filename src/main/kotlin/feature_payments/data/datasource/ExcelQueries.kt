package feature_payments.data.datasource

import Excel.Column
import Excel.Row
import JobQuery
import feature_payments.presentation.util.PaidType
import feature_payments.presentation.util.Client

object ExcelQueries {

    private fun commonQuery(codigo: String) = """
            pag_dtvc as '${Column.DT_VENC.cellName}',
            pag_pag as '${Column.DT_PAG.cellName}',
            pag_DtEmi as '${Column.DT_EMI.cellName}',
            REPLACE(pag_fav,CHAR(13)+CHAR(10),'') as '${Column.FORN.cellName}',
            CONCAT($codigo , ' - ', PLC.plc_descr) AS '${Column.PLC.cellName}',
            O.orc_job as '${Column.JOB.cellName}',
            P.pag_nonf as '${Column.NO_NF.cellName}',
            (SELECT CASE WHEN year(pag_dtnf) < 2000 then NULL else pag_dtnf end) as '${Column.DT_NF.cellName}',
            P.pag_cheque AS '${Column.CHEQUE.cellName}',
            '',
            (CASE WHEN P.pag_issaidaapr = 0 and P.pag_isprescpr = 0 and P.pag_acerap = 0 THEN '' ELSE P.pag_numAP END) AS '${Column.AP.cellName}',
            P.pag_pk AS '${Column.ID.cellName}',
            LTRIM(REPLACE(P.pag_discr,CHAR(13)+CHAR(10),'')) AS '${Column.DISCR.cellName}',
            '',
        """.trimIndent()

    fun getSubtotalFormula(endRowIndex: Int) =
        "SUBTOTAL(9,${Column.VAL_LI.letter}${Row.FIRST_DATA.rowNumber}:${Column.VAL_LI.letter}${endRowIndex + 1})"

    fun getGeralIpcDifFormula(client: Client?): String {
        return when (client) {
            Client.AMAZON ->
                "=IF(OR(AND(ISNUMBER(MID(\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",SEARCH(\"::\",\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ")-2,1)*1),ISNUMBER(MID(\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",SEARCH(\"::\",\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ")-1,1)*1),OR(LEFT(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",2)=\"EX\",LEFT(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",2)=\"PR\",LEFT(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",2)=\"PO\"),ISNUMBER(MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",3,2)-2),ISNUMBER(MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",6,2)-2),MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",5,1)=\"-\",MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",8,1)=\"-\")," +
                        "AND(ISNUMBER(MID(\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",SEARCH(\"::\",\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ")-2,2)*1),ISNUMBER(MID(\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",SEARCH(\"::\",\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ")-1,1)*1),LEFT(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",22)=\"DEVOLUÇÃO DE SALDO DE:\",ISNUMBER(MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",26,2)-2),ISNUMBER(MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",29,2)-2),MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",28,1)=\"-\",MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",31,1)=\"-\")),IF(ISNUMBER(SEARCH(\"::\"," + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ")),IF(LEFT(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",22)=\"DEVOLUÇÃO DE SALDO DE:\",SUBSTITUTE(" + Column.IPC.letter + Row.FIRST_DATA.rowNumber + ",MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",26,5),\"\"),SUBSTITUTE(" + Column.IPC.letter + Row.FIRST_DATA.rowNumber + ",MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",3,5),\"\")), \"Sem '::'\"),\"INICIAIS ERRADAS OU IPC ERRADO\")"

            Client.NETFLIX -> "=IF(OR(AND(ISNUMBER(MID(\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",SEARCH(\"::\",\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ")-1,1)*1),OR(LEFT(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",2)=\"EX\",LEFT(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",2)=\"PR\",LEFT(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",2)=\"PO\"),ISNUMBER(MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",3,4)-2),NOT(ISNUMBER(MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",7,1)-2))),AND(ISNUMBER(MID(\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",SEARCH(\"::\",\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ")-1,1)*1),LEFT(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",22)=\"DEVOLUÇÃO DE SALDO DE:\",ISNUMBER(MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",26,4)-2),NOT(ISNUMBER(MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",30,1)-2)))),IF(ISNUMBER(FIND(\"::\"" +
                    "," + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ")),IF(LEFT(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",22)=\"DEVOLUÇÃO DE SALDO DE:\"," + Column.IPC.letter + Row.FIRST_DATA.rowNumber + "-MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",26,4)," + Column.IPC.letter + Row.FIRST_DATA.rowNumber + "-MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",3,4)),\"Sem '::'\"),\"INICIAIS ERRADAS OU IPC ERRADO\")"

            else -> "=IF(OR(AND(ISNUMBER(MID(\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",SEARCH(\"::\",\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ")-1,1)*1),OR(LEFT(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",2)=\"EX\",LEFT(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",2)=\"PR\",LEFT(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",2)=\"PO\"),ISNUMBER(MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",3,4)-2),NOT(ISNUMBER(MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",7,1)-2))),AND(ISNUMBER(MID(\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",SEARCH(\"::\",\$" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ")-1,1)*1),LEFT(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",22)=\"DEVOLUÇÃO DE SALDO DE:\",ISNUMBER(MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",26,4)-2),NOT(ISNUMBER(MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",30,1)-2)))),IF(ISNUMBER(FIND(\"::\"" +
                    "," + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ")),IF(LEFT(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",22)=\"DEVOLUÇÃO DE SALDO DE:\"," + Column.IPC.letter + Row.FIRST_DATA.rowNumber + "-MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",26,4)," + Column.IPC.letter + Row.FIRST_DATA.rowNumber + "-MID(" + Column.DISCR.letter + Row.FIRST_DATA.rowNumber + ",3,4)),\"Sem '::'\"),\"INICIAIS ERRADAS OU IPC ERRADO\")"
        }
    }

    fun getCashIpcDifFormula(client: Client?): String {
        return getGeralIpcDifFormula(client)
    }


    fun getResultJobsQuery() = """
        SELECT ${JobQuery.Number.text}, ${JobQuery.Name.text},
            (CASE WHEN charindex('${Client.NETFLIX.name}', ${JobQuery.Client.text}) = 1 then '${Client.NETFLIX.name}'
            WHEN charindex('${Client.AMAZON.name}', ${JobQuery.Client.text}) = 1 then '${Client.AMAZON.name}'
            WHEN charindex('${Client.GLOBO.name}', ${JobQuery.Client.text}) = 1 then '${Client.GLOBO.name}'
            else '' end) as '${JobQuery.Client.text}'  FROM ORCAMENTOS WHERE ORC_JOB > 50
        """.trimIndent()

    fun getGeralResultQuery(useDateCreate: Boolean, useAlternativeCode: Boolean): String {
        val criacaoQuery = if (useDateCreate) " and P.pag_createdat < ? " else ""

        val codigo: String =
            if (useAlternativeCode) " PLC_CODIGOALTERNATIVO " else " CONCAT(SUBSTRING(plc_codigo,2,2),SUBSTRING(plc_codigo,5,2)) "


        val pagQuery = """
            SELECT '${PaidType.Pagamento.type}' AS '${Column.TIPO.cellName}',
                    ${commonQuery(codigo)}
                    P.pag_valli AS '${Column.VAL_LI.cellName}',
                    $codigo AS '${Column.IPC.cellName}'
                    ${joinStatements()}
                    ${commonConditions(criacaoQuery)}
                    ${geralCondition()}
                    ${pagConditions()}
                    AND ((pag_valli < 0 ) OR (PAG_DOC <> 1 AND P.pag_ctb_pk IN (SELECT ocb_ctb_pk FROM [ProFilmeNET].[dbo].[OcorrenciasBancarias] where cast(P.pag_cheque as varchar ) = ocb_Doc) AND (SELECT COUNT (ocb_doc) FROM [ProFilmeNET].[dbo].[OcorrenciasBancarias] WHERE ocb_doc = (SELECT CAST(P.pag_cheque as varchar) )) <> 0) or ( pag_doc = 1 AND P.pag_ctb_pk IN (SELECT ocb_ctb_pk FROM [ProFilmeNET].[dbo].[OcorrenciasBancarias] where cast(P.pag_cheque as varchar ) = ocb_Doc) AND P.pag_cheque IN (SELECT ocb_doc FROM [ProFilmeNET].[dbo].[OcorrenciasBancarias] where ocb_ctb_pk = P.pag_ctb_pk and ocb_data >= P.pag_pag )))
            """.trimIndent()

        val apsQuery = """
            SELECT '${PaidType.AP.type}' AS '${Column.TIPO.cellName}',
                    ${commonQuery(codigo)}
                    (COALESCE(P.pag_valli,0)-(SELECT COALESCE(SUM(PA.pag_valli),0) from pagamentos PA WHERE PA.pag_numap = p.pag_numap AND PA.pag_isprescpr = 1 AND PA.pag_issaidaapr = 0 AND PA.pag_acerap = 0 and PA.pag_orc_pk = P.pag_orc_pk   AND PA.pag_pag > ? AND PA.pag_pag < ? $criacaoQuery )) AS '${Column.VAL_LI.cellName}',
                    $codigo AS '${Column.IPC.cellName}'
                    ${joinStatements()}
                    ${commonConditions(criacaoQuery)}
                    ${geralCondition()}
                    ${apsConditions()}
                    AND ((PAG_DOC <> 1 AND P.pag_ctb_pk IN (SELECT ocb_ctb_pk FROM [ProFilmeNET].[dbo].[OcorrenciasBancarias] where cast(P.pag_cheque as varchar ) = ocb_Doc) AND (SELECT COUNT (ocb_doc) FROM [ProFilmeNET].[dbo].[OcorrenciasBancarias] WHERE ocb_doc = (SELECT CAST(P.pag_cheque as varchar) )) <> 0) or ( pag_doc = 1 AND P.pag_ctb_pk IN (SELECT ocb_ctb_pk FROM [ProFilmeNET].[dbo].[OcorrenciasBancarias] where cast(P.pag_cheque as varchar ) = ocb_Doc) AND P.pag_cheque IN (SELECT ocb_doc FROM [ProFilmeNET].[dbo].[OcorrenciasBancarias] where ocb_ctb_pk = P.pag_ctb_pk and ocb_data >= P.pag_pag )))
            """.trimIndent()

        val prestadoQuery = """
            SELECT '${PaidType.Prestado.type}' AS '${Column.TIPO.cellName}',
                    ${commonQuery(codigo)}
                    P.pag_valli AS '${Column.VAL_LI.cellName}',
                    $codigo AS '${Column.IPC.cellName}'
                    ${joinStatements()}
                    ${commonConditions(criacaoQuery)}
                    ${geralCondition()}
                    ${prestadoConditions()}
            """.trimIndent()

        val devSaldoQuery = """
            SELECT '${PaidType.DevSaldo.type}' AS '${Column.TIPO.cellName}',
                    ${commonQuery(codigo)}
                    0-P.pag_valli AS '${Column.VAL_LI.cellName}',
                    $codigo AS '${Column.IPC.cellName}'
                    FROM Pagamentos P
                    JOIN Orcamentos O ON P.pag_orc_pk = O.orc_pk
                    JOIN PlanoDeContas PLC ON plc_pk = (select PK.pag_plc_pk from pagamentos PK where PK.pag_numap = P.pag_numap and PK.pag_issaidaapr =1 and PK.pag_acerap = 0 and PK.pag_isprescpr = 0 )
                    ${commonConditions(criacaoQuery)}
                    ${geralCondition()}
                    ${devSaldoConditions()}
                    and (((PAG_DOC <> 1 AND P.pag_ctb_pk IN (SELECT ocb_ctb_pk FROM [ProFilmeNET].[dbo].[OcorrenciasBancarias] where cast(P.pag_cheque as varchar ) = ocb_Doc) AND (SELECT COUNT (ocb_doc) FROM [ProFilmeNET].[dbo].[OcorrenciasBancarias] WHERE ocb_doc = (SELECT CAST(P.pag_cheque as varchar) )) <> 0) or ( pag_doc = 1 AND P.pag_ctb_pk IN (SELECT ocb_ctb_pk FROM [ProFilmeNET].[dbo].[OcorrenciasBancarias] where cast(P.pag_cheque as varchar ) = ocb_Doc) AND P.pag_cheque IN (SELECT ocb_doc FROM [ProFilmeNET].[dbo].[OcorrenciasBancarias] where ocb_ctb_pk = P.pag_ctb_pk and ocb_data >= P.pag_pag ))))
            """.trimIndent()

        return """
            $pagQuery
            UNION
            $apsQuery
            UNION
            $prestadoQuery
            UNION
            $devSaldoQuery
            ORDER BY '${Column.DT_PAG.cellName}', '${Column.AP.cellName}'
        """.trimIndent()

    }

    private fun joinStatements(): String = """
        FROM Pagamentos P
        JOIN Orcamentos O ON P.pag_orc_pk = O.orc_pk
        JOIN PlanoDeContas PLC ON P.pag_plc_pk = PLC.plc_pk
    """.trimIndent()

    private fun commonConditions(criacaoQuery: String): String = """
        WHERE O.orc_job = ?
        AND P.pag_unif_pk = 1
        AND P.pag_emp_pk = 1
        $criacaoQuery
    """.trimIndent()

    private fun cashCondition(): String = " AND YEAR(pag_pag) < 2000 "

    private fun geralCondition(): String = " and pag_pag > ? and pag_pag < ? "

    private fun pagConditions(): String = """
        AND P.pag_IsSaidaAPr = 0
        AND P.pag_IsPresCPr = 0
        AND P.pag_AcerAP = 0
    """.trimIndent()

    private fun apsConditions(): String = """
        AND P.pag_IsSaidaAPr = 1
        AND P.pag_IsPresCPr = 0
        AND P.pag_AcerAP = 0
    """.trimIndent()

    private fun prestadoConditions(): String = """
        AND P.pag_IsSaidaAPr = 0
        AND P.pag_IsPresCPr = 1
        AND P.pag_AcerAP = 0
    """.trimIndent()

    private fun devSaldoConditions(): String = """
        AND P.pag_IsSaidaAPr = 1
        AND P.pag_IsPresCPr = 0
        AND P.pag_AcerAP = 1
    """.trimIndent()

    fun getCashResultQuery(useDateCreate: Boolean, useAlternativeCode: Boolean): String {
        val criacaoQuery = if (useDateCreate) " and P.pag_createdat < ? " else ""

        val codigo: String =
            if (useAlternativeCode) " PLC_CODIGOALTERNATIVO " else " CONCAT(SUBSTRING(plc_codigo,2,2),SUBSTRING(plc_codigo,5,2)) "


        val pagQuery = """
            SELECT '${PaidType.Pagamento.type}' as '${Column.TIPO.cellName}',
            ${commonQuery(codigo)}
            P.pag_valli AS '${Column.VAL_LI.cellName}',
            $codigo AS '${Column.IPC.cellName}'
            ${joinStatements()}
            ${commonConditions(criacaoQuery)}
            ${cashCondition()}
            ${pagConditions()}
        """.trimIndent()

        val apsQuery = """
            SELECT '${PaidType.AP.type}' as '${Column.TIPO.cellName}',
            ${commonQuery(codigo)}
            (COALESCE(P.pag_valli,0)-(SELECT COALESCE(SUM(PA.pag_valli),0) from pagamentos PA WHERE PA.pag_numap = p.pag_numap AND PA.pag_isprescpr = 1 AND PA.pag_issaidaapr = 0 AND PA.pag_acerap = 0 and PA.pag_orc_pk = P.pag_orc_pk $criacaoQuery AND YEAR(PA.pag_pag) < 2000)) AS '${Column.VAL_LI.cellName}',
            $codigo AS '${Column.IPC.cellName}'
            ${joinStatements()}
            ${commonConditions(criacaoQuery)}
            ${cashCondition()}
            ${apsConditions()}
        """.trimIndent()


        val prestadoQuery = """
            SELECT '${PaidType.Prestado.type}' as '${Column.TIPO.cellName}',
            ${commonQuery(codigo)}
            P.pag_valli AS '${Column.VAL_LI.cellName}',
            $codigo AS '${Column.IPC.cellName}'
            ${joinStatements()}
            ${commonConditions(criacaoQuery)}
            ${cashCondition()}
            ${prestadoConditions()}
        """.trimIndent()

        val devSaldoQuery = """
            SELECT '${PaidType.DevSaldo.type}' as '${Column.TIPO.cellName}',
            ${commonQuery(codigo)}
            0 - P.pag_valli AS '${Column.VAL_LI.cellName}',
            $codigo AS '${Column.IPC.cellName}'
            FROM Pagamentos P
            JOIN Orcamentos O ON P.pag_orc_pk = O.orc_pk
            JOIN PlanoDeContas PLC ON plc_pk = (
                SELECT PK.pag_plc_pk
                FROM pagamentos PK
                WHERE PK.pag_numap = P.pag_numap
                  AND PK.pag_issaidaapr = 1
                  AND PK.pag_acerap = 0
                  AND PK.pag_isprescpr = 0
            )
            ${commonConditions(criacaoQuery)}
            ${cashCondition()}
            ${devSaldoConditions()}
        """.trimIndent()

        return """
            $pagQuery
            UNION
            $apsQuery
            UNION
            $prestadoQuery
            UNION
            $devSaldoQuery
            ORDER BY '${Column.DT_PAG.cellName}', '${Column.AP.cellName}'
        """.trimIndent()

    }
}