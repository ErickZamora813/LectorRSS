package com.erick.applicationrss.data

data class RssItem(
    val title: String,
    val pubDate: String?,
    val link: String,
    val imageUrl: String?,
    val siteName: String
)
