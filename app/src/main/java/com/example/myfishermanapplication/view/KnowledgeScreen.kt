package com.example.myfishermanapplication.view

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfishermanapplication.model.Fish
import com.example.myfishermanapplication.viewmodel.KnowledgeViewModel
import java.io.IOException
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background


@Composable
fun KnowledgeScreen(fishId: String) {
    val context = LocalContext.current
    val viewModel: KnowledgeViewModel = viewModel(factory = KnowledgeViewModel.Factory(context.applicationContext))
    val fish by viewModel.fish.collectAsState()

    LaunchedEffect(fishId) {
        viewModel.loadFish(fishId)
    }

    fish?.let {
        FishDetailsScreen(it)
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Ładowanie danych ryby...", fontSize = 18.sp)
    }
}

@Composable
fun FishDetailsScreen(fish: Fish) {
    val context = LocalContext.current
    val bitmap = remember { loadBitmapFromAssets(context, fish.imageUri ?: "") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = fish.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = fish.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } ?: Text("Błąd wczytywania obrazu", color = Color.White)

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Dorastają do: ${fish.length} cm", fontSize = 18.sp, color = Color.White)
                Text(text = "Przynęta: ${fish.bait}", fontSize = 18.sp, color = Color.White)
                Text(text = "Wymiar ochronny: ${fish.protectiveDimension} cm", fontSize = 18.sp, color = Color.White)
                Text(text = "Opis: ${fish.description}", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}


fun loadBitmapFromAssets(context: Context, filePath: String): android.graphics.Bitmap? {
    return try {
        val inputStream = context.assets.open(filePath)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}
