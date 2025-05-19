package com.example.myfishermanapplication.view

import androidx.compose.animation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myfishermanapplication.database.CatchDatabaseRepository
import com.example.myfishermanapplication.viewmodel.CatchListViewModel
import com.example.myfishermanapplication.viewmodel.CatchListViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background


@Composable
fun ListScreen(
    navController: NavController,
    repository: CatchDatabaseRepository
) {
    val viewModel: CatchListViewModel = viewModel(factory = CatchListViewModelFactory(repository))
    val catches by viewModel.allCatches.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(catches, key = { it.id }) { catch ->
                    var expanded by remember { mutableStateOf(false) }

                    val statusColor = when {
                        catch.fishCount > 10 -> Color(0xFF4CAF50)
                        catch.fishCount > 5 -> Color(0xFFFFC107)
                        else -> Color(0xFFF44336)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { expanded = !expanded },
                                    onLongPress = {
                                        navController.navigate("input/${catch.id}")
                                    }
                                )
                            },
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Połów z dnia ${catch.date}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(statusColor)
                                )
                                IconButton(onClick = {
                                    coroutineScope.launch { viewModel.deleteCatch(catch) }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Usuń",
                                        tint = Color.White
                                    )
                                }
                            }

                            AnimatedVisibility(
                                visible = expanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    Text("Liczba ryb: ${catch.fishCount}", color = Color.White)
                                    Text("Waga: ${catch.fishWeight} kg", color = Color.White)
                                    Text("Lokalizacja: ${catch.location}", color = Color.White)
                                    Text("Notatki: ${catch.notes}", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigate("input")
            },
            containerColor = Color.Black.copy(alpha = 0.7f),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Dodaj połów")
        }
    }
}
