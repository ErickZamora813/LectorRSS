package com.erick.applicationrss.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.erick.applicationrss.Screens.components.NewsItem
import com.erick.applicationrss.Screens.components.parseDate
import com.erick.applicationrss.Screens.components.rssElements
import com.erick.applicationrss.data.AppDatabase
import com.erick.applicationrss.data.RssItem
import com.erick.applicationrss.data.RssUrl
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var showDialog by remember { mutableStateOf(false) }
    var rssUrl by remember { mutableStateOf("") }
    var rssItems by remember { mutableStateOf(listOf<RssItem>()) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val rssDao = database.rssUrlDao()

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val storedUrls = rssDao.getAllUrls()
            if (storedUrls.isNotEmpty()) {
                rssUrl = storedUrls.first().url
                rssItems = rssElements(rssUrl)
            }
        }
    }

    val sortedItems = rssItems.sortedByDescending { it.pubDate?.let { parseDate(it) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lector de RSS") },
                actions = {
                    IconButton(onClick = {
                        showDialog = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar un Link")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = {
                    coroutineScope.launch {
                        isRefreshing = true
                        val newItems = rssElements(rssUrl)
                        rssItems = rssItems + newItems.filterNot { it in rssItems }
                        isRefreshing = false
                    }
                },
                indicator = { state, refreshTrigger ->
                    SwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = refreshTrigger,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (sortedItems.isNotEmpty()) {
                        items(sortedItems.size) { index ->
                            NewsItem(sortedItems[index])
                        }
                    } else {
                        item {
                            Text(text = "Sin noticias", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Agrega un RSS") },
            text = {
                Column {
                    Text("Enter the URL:")
                    TextField(
                        value = rssUrl,
                        onValueChange = { rssUrl = it },
                        placeholder = { Text("https://example.com/feed.xml") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                rssDao.insertUrl(RssUrl(url = rssUrl))
                                val newItems = rssElements(rssUrl)
                                rssItems = rssItems + newItems.filterNot { it in rssItems }
                            }
                        }
                        showDialog = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}




