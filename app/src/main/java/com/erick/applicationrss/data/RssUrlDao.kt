package com.erick.applicationrss.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RssUrlDao {
    @Insert
    suspend fun insertUrl(rssUrl: RssUrl)

    @Query("SELECT * FROM rss_urls")
    suspend fun getAllUrls(): List<RssUrl>
}