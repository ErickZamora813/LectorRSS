package com.erick.applicationrss.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rss_urls")
data class RssUrl(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String
)
