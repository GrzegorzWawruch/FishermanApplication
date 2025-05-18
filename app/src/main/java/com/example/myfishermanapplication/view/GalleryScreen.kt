package com.example.myfishermanapplication.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myfishermanapplication.viewmodel.GalleryViewModel

@Composable
fun GalleryScreen(navController: NavController) {
    val viewModel: GalleryViewModel = viewModel()
    val fishList by viewModel.fishList.collectAsState()

    // ⛔️ USUNIĘTO background(Color.LightGray)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
        ) {
            items(items = fishList) { fish ->
                Button(
                    onClick = {
                        navController.navigate("knowledge/${fish.id}")
                    },
                    modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                        .height(80.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black.copy(alpha = 0.7f),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = fish.name,
                        modifier = Modifier.wrapContentSize()
                    )
                }

            }
        }
    }
}

