package com.example.myfishermanapplication.view

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.myfishermanapplication.database.AppDatabase
import com.example.myfishermanapplication.database.CatchRepository
import com.example.myfishermanapplication.model.Catch
import com.example.myfishermanapplication.viewmodel.CatchViewModel
import com.example.myfishermanapplication.viewmodel.CatchViewModelFactory
import kotlinx.coroutines.launch
import java.util.*

class InputActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(this)
        val catchDao = db.catchDao()
        val repository = CatchRepository(catchDao)
        val viewModelFactory = CatchViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[CatchViewModel::class.java]

        setContent {
            ColumnWithInput(viewModel = viewModel)
        }
    }
}

@Composable
fun ColumnWithInput(viewModel: CatchViewModel) {
    val context = LocalContext.current
    val intentList = Intent(context, ListActivity::class.java)
    val intentGallery = Intent(context, GalleryActivity::class.java)
    val intentMain = Intent(context, MainActivity::class.java)

    var fishCount by remember { mutableStateOf("") }
    var fishWeight by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    var selectedDate by remember { mutableStateOf("") }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, pickedYear, pickedMonth, pickedDay ->
            selectedDate = "$pickedYear-${pickedMonth + 1}-$pickedDay"
        },
        year, month, day
    )

    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )

        Column(
            modifier = Modifier
                .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Co dzisiaj złowiłeś?",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = fishCount,
                onValueChange = { fishCount = it },
                label = { Text("Liczba złowionych ryb") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fishWeight,
                onValueChange = { fishWeight = it },
                label = { Text("Łączna waga ryb") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Miejsce połowu") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notatki") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                label = { Text("Wybierz datę") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                enabled = false
            )

            Button(onClick = {
                if (
                    fishCount.isBlank() ||
                    fishWeight.isBlank() ||
                    location.isBlank() ||
                    notes.isBlank() ||
                    selectedDate.isBlank()
                ) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Uzupełnij wszystkie pola.")
                    }
                } else {
                    val newCatch = Catch(
                        fishCount = fishCount.toIntOrNull() ?: 0,
                        fishWeight = fishWeight.replace(",", ".").toDoubleOrNull() ?: 0.0,
                        location = location,
                        notes = notes,
                        date = selectedDate
                    )
                    viewModel.insertCatch(newCatch)

                    // Czyścimy pola
                    fishCount = ""
                    fishWeight = ""
                    location = ""
                    notes = ""
                    selectedDate = ""

                    // Przechodzimy do ListActivity
                    context.startActivity(Intent(context, ListActivity::class.java))
                }
            }) {
                Text("Submit")
            }


            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                Button(onClick = { context.startActivity(intentList) }) {
                    Icon(Icons.Default.List, contentDescription = "list icon")
                }
                Button(onClick = { context.startActivity(intentMain) }) {
                    Icon(Icons.Outlined.Home, contentDescription = "home icon")
                }
                Button(onClick = { context.startActivity(intentGallery) }) {
                    Icon(Icons.Default.AccountBox, contentDescription = "account icon")
                }
            }
        }
    }
}
