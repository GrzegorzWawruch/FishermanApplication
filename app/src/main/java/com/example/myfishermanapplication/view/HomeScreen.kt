package com.example.myfishermanapplication.view

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myfishermanapplication.database.AppDatabase
import com.example.myfishermanapplication.database.CatchDatabaseRepository
import com.example.myfishermanapplication.viewmodel.StatsViewModel
import com.example.myfishermanapplication.viewmodel.StatsViewModelFactory
import kotlinx.coroutines.delay
import androidx.compose.foundation.border

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val repository = remember { CatchDatabaseRepository(db.catchDao()) }
    val viewModel: StatsViewModel = viewModel(factory = StatsViewModelFactory(repository))

    val totalCatches by viewModel.totalCatches.collectAsState(initial = 0)
    val totalFishCount by viewModel.totalFishCount.collectAsState()
    val totalWeight by viewModel.totalWeight.collectAsState()
    val averageWeight by viewModel.averageWeight.collectAsState()
    val mostFrequentLocation by viewModel.mostFrequentLocation.collectAsState()

    val stats = listOf(
        "✅ Łączna liczba połowów: $totalCatches",
        "🐟 Łączna liczba ryb: $totalFishCount",
        "⚖️ Łączna waga: ${"%.2f".format(totalWeight)} kg",
        "📈 Średnia waga: ${"%.2f".format(averageWeight)} kg",
        "📍 Najczęstsza lokalizacja: $mostFrequentLocation"
    )

    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(stats) {
        while (true) {
            delay(4000)
            currentIndex = (currentIndex + 1) % stats.size
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Witaj w aplikacji wędkarskiej! 🎣",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color(0xFF121212),
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(64.dp))

        Box(
            modifier = Modifier
                .size(260.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.85f))
                .border(width = 4.dp, color = Color.White, shape = CircleShape)
                .clickable { navController.navigate("stats") },
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = stats[currentIndex],
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(durationMillis = 800)
                    ) with slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(durationMillis = 800)
                    )
                },
                label = "rotatingStats"
            ) { stat ->
                Text(
                    text = stat,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }

    }
}
