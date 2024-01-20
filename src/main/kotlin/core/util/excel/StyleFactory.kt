package core.util.excel

import com.aspose.cells.Style
import com.aspose.cells.TextAlignmentType
import com.aspose.cells.Worksheet

class StyleFactory {
    private val stylesCache = mutableMapOf<CellType, Style>()

    fun getStyle(sheet: Worksheet, cellType: CellType): Style {
        return stylesCache.getOrPut(cellType) {
            createStyle(sheet, cellType)
        }
    }

    private fun createStyle(sheet: Worksheet, cellType: CellType): Style {
        val style = sheet.workbook.createStyle()

        when (cellType) {
            is CellType.Number, is CellType.SubTotal -> {
                style.horizontalAlignment = TextAlignmentType.CENTER
            }
            else -> {
                style.horizontalAlignment = TextAlignmentType.LEFT
            }
        }

        style.cultureCustom = cellType.format

        setFont(style, cellType)

        return style
    }

    private fun setFont(style: Style, cellType: CellType) {
        style.let {
            it.font.name = "Verdana"
            it.font.isBold = cellType is CellType.Header
            it.font.size = when (cellType) {
                is CellType.SubTotal, is CellType.Legend -> 10
                else -> 8
            }
        }
    }

    fun createStylesForAllCellTypes(sheet: Worksheet) {
        stylesCache.clear()
        CellType::class.sealedSubclasses.forEach { subclass ->
            val cellType = subclass.objectInstance ?: return
            if (!stylesCache.containsKey(cellType)) {
                stylesCache[cellType] = createStyle(sheet, cellType)
            }
        }
    }
}