package com.example.myfishermanapplication.view

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.example.myfishermanapplication.database.AppDatabase
import com.example.myfishermanapplication.database.CatchDatabaseRepository
import com.example.myfishermanapplication.ui.theme.MyFishermanApplicationTheme
import com.example.myfishermanapplication.view.components.VideoBackgroundPlayer
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

@UnstableApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(applicationContext)
        val repository = CatchDatabaseRepository(db.catchDao())

        setContent {
            MyFishermanApplicationTheme {
                val context = LocalContext.current

                val videoUri = remember {
                    Uri.parse("android.resource://${context.packageName}/raw/background")
                }

                val exoPlayer = remember {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaItem(androidx.media3.common.MediaItem.fromUri(videoUri))
                        prepare()
                        playWhenReady = true
                        repeatMode = ExoPlayer.REPEAT_MODE_ALL
                    }
                }

                DisposableEffect(Unit) {
                    onDispose { exoPlayer.release() }
                }

                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black))
                {
                    VideoBackgroundPlayer(
                        context = context,
                        videoUri = videoUri,
                        exoPlayer = exoPlayer,
                        modifier = Modifier.fillMaxSize()
                    )

                    MainNavigation(repository)
                }
            }
        }
    }
}

@Composable
fun MainNavigation(repository: CatchDatabaseRepository) {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem("Lista", "list", Icons.Filled.List),
        BottomNavItem("Home", "home", Icons.Outlined.Home),
        BottomNavItem("Galeria", "gallery", Icons.Filled.AccountBox)
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, bottomNavItems)
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .padding(innerPadding)
                .background(Color.Transparent)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("list") { ListScreen(navController, repository) }
            composable("gallery") { GalleryScreen(navController) }
            composable("stats") { StatsScreen() }
            composable("input") { InputScreen(navController, repository) }
            composable("input/{catchId}") { backStackEntry ->
                val catchId = backStackEntry.arguments?.getString("catchId")
                InputScreen(navController, repository, editingId = catchId)
            }
            composable("knowledge/{fishId}") { backStackEntry ->
                val fishId = backStackEntry.arguments?.getString("fishId") ?: ""
                KnowledgeScreen(fishId)
            }
        }
    }
}
