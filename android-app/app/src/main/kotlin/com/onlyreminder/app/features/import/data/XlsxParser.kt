package com.onlyreminder.app.features.import.data

import com.onlyreminder.app.features.import.domain.RawImportRow
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

class XlsxParser {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun parse(inputStream: InputStream): List<RawImportRow> {
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0) // Default to first sheet
        val rows = mutableListOf<RawImportRow>()

        for (i in 0..sheet.lastRowNum) {
            val row = sheet.getRow(i) ?: continue
            val rowData = mutableListOf<String>()

            for (j in 0 until row.lastCellNum) {
                val cell = row.getCell(j)
                val value = when (cell?.cellType) {
                    CellType.STRING -> cell.stringCellValue
                    CellType.NUMERIC -> {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            dateFormatter.format(cell.dateCellValue)
                        } else {
                            // Avoid scientific notation for phone numbers
                            cell.numericCellValue.toLong().toString()
                        }
                    }

                    CellType.BOOLEAN -> cell.booleanCellValue.toString()
                    CellType.FORMULA -> cell.cellFormula
                    else -> ""
                }
                rowData.add(value.trim())
            }

            if (rowData.any { it.isNotEmpty() }) {
                rows.add(RawImportRow(i, rowData))
            }
        }

        workbook.close()
        return rows
    }
}
