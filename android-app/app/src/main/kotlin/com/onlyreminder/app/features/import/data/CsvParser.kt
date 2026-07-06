package com.onlyreminder.app.features.import.data

import com.onlyreminder.app.features.import.domain.RawImportRow
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class CsvParser {

    fun parse(
        inputStream: InputStream,
        separator: Char? = null,
        encoding: String = "UTF-8"
    ): List<RawImportRow> {
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName(encoding)))
        val lines = reader.readLines()
        if (lines.isEmpty()) return emptyList()

        val activeSeparator = separator ?: detectSeparator(lines.first())

        return lines.mapIndexed { index, line ->
            RawImportRow(index, parseLine(line, activeSeparator))
        }
    }

    fun detectSeparator(line: String): Char {
        val counts = mutableMapOf(',' to 0, ';' to 0, '\t' to 0)
        line.forEach { char ->
            if (counts.containsKey(char)) {
                counts[char] = counts[char]!! + 1
            }
        }
        return counts.maxByOrNull { it.value }?.key ?: ','
    }

    private fun parseLine(line: String, separator: Char): List<String> {
        val result = mutableListOf<String>()
        var curVal = StringBuilder()
        var inQuotes = false

        var i = 0
        while (i < line.length) {
            val ch = line[i]
            if (inQuotes) {
                if (ch == '\"') {
                    if (i + 1 < line.length && line[i + 1] == '\"') {
                        curVal.append('\"')
                        i++
                    } else {
                        inQuotes = false
                    }
                } else {
                    curVal.append(ch)
                }
            } else {
                if (ch == '\"') {
                    inQuotes = true
                } else if (ch == separator) {
                    result.add(curVal.toString().trim())
                    curVal = StringBuilder()
                } else {
                    curVal.append(ch)
                }
            }
            i++
        }
        result.add(curVal.toString().trim())
        return result
    }
}
