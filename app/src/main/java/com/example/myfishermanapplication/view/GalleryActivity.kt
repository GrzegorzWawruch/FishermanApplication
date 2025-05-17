package com.example.myfishermanapplication.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.myfishermanapplication.viewmodel.GalleryViewModel

class GalleryActivity : ComponentActivity() {

    private val galleryViewModel by viewModels<GalleryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Column(galleryViewModel)
        }
    }

    @Composable
    fun Column(viewModel: GalleryViewModel) {
        val fishList by viewModel.fishList.collectAsState()

        Box(modifier = Modifier.fillMaxSize()) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .padding(top = 30.dp, bottom = 10.dp)
                        .fillMaxHeight(0.93f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(top = 20.dp)
                ) {
                    items(items = fishList) { fish ->
                        val context = LocalContext.current
                        Button(
                            onClick = {
                                val intent = Intent(context, KnowledgeActivity::class.java).apply {
                                    putExtra("fish_id", fish.id)
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                .height(80.dp)
                                .fillMaxWidth(),
                        ) {
                            Text(
                                text = "${fish.name}",
                                modifier = Modifier.wrapContentSize()
                            )
                        }
                    }

                }
                BottomRow()
            }
        }
    }

    @Composable
    fun BottomRow() {
        val context: Context = LocalContext.current
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp, start = 5.dp, end = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
        ) {
            Button(onClick = {
                startActivity(Intent(context, ListActivity::class.java))
                finish()
            }) {
                Icon(Icons.Default.List, contentDescription = "list icon")
            }
            Button(onClick = {
                startActivity(Intent(context, MainActivity::class.java))
                finish()
            }) {
                Icon(Icons.Outlined.Home, contentDescription = "home icon")
            }
            Button(onClick = {

            }) {
                Icon(Icons.Default.AccountBox, contentDescription = "account icon")
            }
        }
    }
}