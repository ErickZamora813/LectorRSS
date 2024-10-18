package com.erick.applicationrss.Screens.components

import com.erick.applicationrss.data.RssItem
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

suspend fun rssElements(urlString: String): List<RssItem> {
    val items = mutableListOf<RssItem>()
    try {
        val url = URL(urlString)
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val xpp = factory.newPullParser()
        xpp.setInput(url.openStream(), "UTF-8")

        var eventType = xpp.eventType
        var insideItem = false
        var title: String? = null
        var pubDate: String? = null
        var imageUrl: String? = null
        var description: String? = null
        val siteName = URL(urlString).host
        var link: String? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = xpp.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (tagName.equals("item", ignoreCase = true)) {
                        insideItem = true
                    } else if (insideItem) {
                        when {
                            tagName.equals("title", ignoreCase = true) -> title = xpp.nextText()
                            tagName.equals("pubDate", ignoreCase = true) -> pubDate = xpp.nextText()
                            tagName.equals("link", ignoreCase = true) -> link = xpp.nextText()
                            tagName.equals("description", ignoreCase = true) -> description = xpp.nextText()
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (tagName.equals("item", ignoreCase = true)) {
                        // Si no encontramos una imagen en <media:content>, intentamos extraerla de la descripción
                        if (imageUrl == null && description != null) {
                            imageUrl = extractImageUrlFromDescription(description)
                        }

                        if (title != null && link != null) {
                            items.add(RssItem(title, pubDate, link, imageUrl, siteName))
                        }
                        insideItem = false
                        title = null
                        pubDate = null
                        link = null
                        imageUrl = null
                        description = null
                    }
                }
            }
            eventType = xpp.next()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return items
}


fun formatPubDate(pubDate: String): String {
    val date = parseDate(pubDate)
    return if (date != null) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        dateFormat.format(date)
    } else {
        "Unknown"
    }
}

fun parseDate(dateStr: String): Date? {
    return try {
        val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
        format.parse(dateStr)
    } catch (e: Exception){
        e.printStackTrace()
        null
    }
}

fun extractImageUrlFromDescription(description: String): String? {
    val imgTagPattern = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"]".toRegex()
    val matchResult = imgTagPattern.find(description)
    return matchResult?.groups?.get(1)?.value
}
