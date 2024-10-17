package com.erick.applicationrss.Screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.erick.applicationrss.data.RssItem

@Composable
fun NewsItem(rssItem: RssItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Imagen (si existe)
        rssItem.imageUrl?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 8.dp)
            )
        }

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = rssItem.siteName, style = MaterialTheme.typography.bodySmall)
            Text(text = rssItem.title, style = MaterialTheme.typography.bodyMedium)
            rssItem.pubDate?.let {
                Text(text = formatPubDate(it), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}


