package com.onlyreminder.app.features.import.data

import android.util.Xml
import com.onlyreminder.app.features.import.domain.RawImportRow
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

class XmlParser {

    private val expectedFields = listOf(
        "firstName", "lastName", "displayName", "phone", "email",
        "company", "birthday", "tags", "group", "source", "notes",
        "status", "marketingConsent", "privacyConsent"
    )

    fun parse(inputStream: InputStream): List<RawImportRow> {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)

        val rows = mutableListOf<RawImportRow>()
        rows.add(RawImportRow(0, expectedFields))

        var eventType = parser.eventType
        var currentContact: MutableMap<String, String>? = null
        var currentTag: String? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    currentTag = parser.name
                    if (currentTag == "contact") {
                        currentContact = mutableMapOf()
                    }
                }

                XmlPullParser.TEXT -> {
                    val text = parser.text.trim()
                    if (text.isNotEmpty() && currentContact != null && currentTag != null) {
                        currentContact[currentTag] = text
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name == "contact" && currentContact != null) {
                        val rowData = expectedFields.map { field ->
                            currentContact!![field] ?: ""
                        }
                        rows.add(RawImportRow(rows.size, rowData))
                        currentContact = null
                    }
                    currentTag = null
                }
            }
            eventType = parser.next()
        }

        return rows
    }
}
