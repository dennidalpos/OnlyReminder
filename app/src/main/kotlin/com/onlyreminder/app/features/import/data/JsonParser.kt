package com.onlyreminder.app.features.importer.data

import com.onlyreminder.app.features.importer.domain.RawImportRow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.InputStream

class JsonParser {

    private val expectedFields = listOf(
        "firstName", "lastName", "displayName", "phone", "email",
        "company", "birthday", "tags", "group", "source", "notes",
        "status", "marketingConsent", "privacyConsent"
    )

    fun parse(inputStream: InputStream): List<RawImportRow> {
        val content = inputStream.bufferedReader().use { it.readText() }
        val jsonElement = Json.parseToJsonElement(content)

        val rows = mutableListOf<RawImportRow>()

        // Header
        rows.add(RawImportRow(0, expectedFields))

        if (jsonElement is JsonArray) {
            jsonElement.forEachIndexed { index, element ->
                if (element is JsonObject) {
                    val rowData = expectedFields.map { field ->
                        val value = element[field]
                        when {
                            value == null -> ""
                            value is JsonPrimitive -> value.content
                            value is JsonArray -> value.joinToString(",") {
                                if (it is JsonPrimitive) it.content else it.toString()
                            }

                            else -> value.toString()
                        }
                    }
                    rows.add(RawImportRow(index + 1, rowData))
                }
            }
        }

        return rows
    }
}
