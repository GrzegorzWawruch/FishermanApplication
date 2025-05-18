package com.example.myfishermanapplication.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfishermanapplication.database.AppDatabase
import com.example.myfishermanapplication.database.CatchDatabaseRepository
import com.example.myfishermanapplication.viewmodel.StatsViewModel
import com.example.myfishermanapplication.viewmodel.StatsViewModelFactory
import androidx.compose.ui.draw.clip

@Composable
fun StatsScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val repository = remember { CatchDatabaseRepository(db.catchDao()) }
    val viewModel: StatsViewModel = viewModel(factory = StatsViewModelFactory(repository))

    val totalCatches by viewModel.totalCatches.collectAsState(initial = 0)
    val totalFishCount by viewModel.totalFishCount.collectAsState()
    val totalWeight by viewModel.totalWeight.collectAsState()
    val frequentLocation by viewModel.mostFrequentLocation.collectAsState()
    val heaviestCatch = viewModel.heaviestCatch.collectAsState().value
    val averageWeight = viewModel.averageWeight.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(24.dp)
                .wrapContentHeight()
                .fillMaxWidth(0.95f),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "📊 Statystyki połowów",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text("✅ Łączna liczba połowów: $totalCatches", color = Color.White)
            Text("🐟 Łączna liczba złowionych ryb: $totalFishCount", color = Color.White)
            Text("⚖️ Łączna waga: ${"%.2f".format(totalWeight)} kg", color = Color.White)
            Text("📈 Średnia waga ryby: ${"%.2f".format(averageWeight.value)} kg", color = Color.White)
            Text("📍 Najczęstsza lokalizacja: $frequentLocation", color = Color.White)

            Spacer(modifier = Modifier.height(12.dp))

            if (heaviestCatch != null) {
                Text(
                    text = "🏆 Najcięższy połów: ${heaviestCatch.fishWeight} kg\n📍 ${heaviestCatch.location}, 📅 ${heaviestCatch.date}",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            } else {
                Text("🏆 Najcięższy połów: brak danych", color = Color.White)
            }
        }
    }
}

