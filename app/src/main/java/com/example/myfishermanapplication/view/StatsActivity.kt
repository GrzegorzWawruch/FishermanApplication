package com.example.myfishermanapplication.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.myfishermanapplication.database.AppDatabase
import com.example.myfishermanapplication.database.CatchDatabaseRepository
import com.example.myfishermanapplication.viewmodel.StatsViewModel
import com.example.myfishermanapplication.viewmodel.StatsViewModelFactory
import com.example.myfishermanapplication.view.ListActivity
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.runtime.collectAsState


class StatsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        val repository = CatchDatabaseRepository(db.catchDao())
        val viewModelFactory = StatsViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[StatsViewModel::class.java]

        setContent {
            StatsScreen(viewModel)
        }
    }
}


@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val context = LocalContext.current
    val totalCatches by viewModel.totalCatches.collectAsState(initial = 0)
    val totalFishCount by viewModel.totalFishCount.collectAsState()
    val totalWeight by viewModel.totalWeight.collectAsState()
    val frequentLocation by viewModel.mostFrequentLocation.collectAsState()
    val heaviestCatch = viewModel.heaviestCatch.collectAsState().value
    val averageWeight = viewModel.averageWeight.collectAsState()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECEFF1))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📊 Statystyki połowów", style = MaterialTheme.typography.headlineSmall)
        Text("✅ Łączna liczba połowów: $totalCatches")
        Text(text = "Łączna liczba złowionych ryb: $totalFishCount")
        Text("⚖️ Łączna waga: ${"%.2f".format(totalWeight)} kg")
        Text("📈 Średnia waga ryby: ${"%.2f".format(averageWeight.value)} kg")
        Text("📍 Najczęstsza lokalizacja: $frequentLocation")


        if (heaviestCatch != null) {
            Text("🏆 Najcięższy połów: ${heaviestCatch.fishWeight} kg,\nlokalizacja: ${heaviestCatch.location},\ndnia: ${heaviestCatch.date}")
        } else {
            Text("🏆 Najcięższy połów: brak danych")
        }





        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = {
            context.startActivity(Intent(context, ListActivity::class.java))
        }) {
            Text("← Powrót do listy")
        }
    }
}
