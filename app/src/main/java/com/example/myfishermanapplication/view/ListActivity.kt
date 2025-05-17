package com.example.myfishermanapplication.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.myfishermanapplication.database.CatchDatabaseRepository
import com.example.myfishermanapplication.viewmodel.CatchListViewModel
import com.example.myfishermanapplication.viewmodel.CatchListViewModelFactory
import kotlinx.coroutines.launch
import com.example.myfishermanapplication.database.AppDatabase

class ListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(applicationContext)
        val catchRepository = CatchDatabaseRepository(db.catchDao())
        val factory = CatchListViewModelFactory(catchRepository)
        val catchListViewModel = ViewModelProvider(this, factory)[CatchListViewModel::class.java]

        setContent {
            CatchListScreen(viewModel = catchListViewModel)
        }
    }
}

@Composable
fun CatchListScreen(viewModel: CatchListViewModel) {
    val context = LocalContext.current
    val catches by viewModel.allCatches.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 10.dp)
                    .fillMaxHeight(0.93f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(top = 20.dp)
            ) {
                items(
                    items = catches,
                    key = { it.id }
                ) { catch ->
                    var expanded by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        val intent = Intent(context, InputActivity::class.java).apply {
                                            putExtra("catch_id", catch.id)
                                        }
                                        context.startActivity(intent)
                                    },
                                    onTap = {
                                        expanded = !expanded
                                    }
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Połów z dnia ${catch.date}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (expanded) "Zwiń" else "Rozwiń"
                            )
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    viewModel.deleteCatch(catch)
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Usuń")
                            }
                        }

                        AnimatedVisibility(visible = expanded) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                Text("Liczba ryb: ${catch.fishCount}")
                                Text("Waga: ${catch.fishWeight}")
                                Text("Lokalizacja: ${catch.location}")
                                Text("Notatki: ${catch.notes}")
                            }
                        }
                    }
                }
            }

            BottomRow()
        }

        FloatingActionButton(
            onClick = {
                context.startActivity(Intent(context, InputActivity::class.java))
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 5.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "add icon")
        }
    }
}

@Composable
fun BottomRow() {
    val context: Context = LocalContext.current
    val intentList = Intent(context, ListActivity::class.java)
    val intentGallery = Intent(context, GalleryActivity::class.java)
    val intentMain = Intent(context, MainActivity::class.java)

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp, start = 5.dp, end = 5.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom,
    ) {
        Button(onClick = { context.startActivity(intentList) }) {
            Icon(imageVector = Icons.Default.List, contentDescription = "list icon")
        }
        Button(onClick = {
            context.startActivity(intentMain)
        }) {
            Icon(imageVector = Icons.Outlined.Home, contentDescription = "home icon")
        }
        Button(onClick = {
            context.startActivity(intentGallery)
        }) {
            Icon(imageVector = Icons.Default.AccountBox, contentDescription = "account icon")
        }
    }
}
