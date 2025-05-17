package com.example.myfishermanapplication.view

import android.app.DatePickerDialog
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myfishermanapplication.database.CatchDatabaseRepository
import com.example.myfishermanapplication.model.Catch
import kotlinx.coroutines.launch
import java.util.*
import com.example.myfishermanapplication.database.AppDatabase


class InputActivity : ComponentActivity() {

    private var editingCatchId: String? = null
    private lateinit var repository: CatchDatabaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(applicationContext)
        repository = CatchDatabaseRepository(db.catchDao())

        editingCatchId = intent.getStringExtra("catch_id")

        setContent {
            ColumnWithInput(repository, editingCatchId) {
                finish()
            }
        }
    }
}

@Composable
fun ColumnWithInput(
    repository: CatchDatabaseRepository,
    editingId: String?,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val intentList = Intent(context, ListActivity::class.java)
    val intentGallery = Intent(context, GalleryActivity::class.java)
    val intentMain = Intent(context, MainActivity::class.java)

    var fishCount by remember { mutableStateOf("") }
    var fishWeight by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            selectedDate = "$year-${month + 1}-$day"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(editingId) {
        editingId?.let {
            val existingCatch = repository.getCatchById(it)
            existingCatch?.let { catchItem ->
                fishCount = catchItem.fishCount.toString()
                fishWeight = catchItem.fishWeight.toString()
                location = catchItem.location
                notes = catchItem.notes
                selectedDate = catchItem.date
            }
        }
    }

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
                .padding(16.dp)
        )

        Column(
            modifier = Modifier
                .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (editingId != null) "Edytuj połów" else "Co dzisiaj złowiłeś?",
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
                coroutineScope.launch {
                    if (
                        fishCount.isBlank() ||
                        fishWeight.isBlank() ||
                        location.isBlank() ||
                        notes.isBlank() ||
                        selectedDate.isBlank()
                    ) {
                        snackbarHostState.showSnackbar("Uzupełnij wszystkie pola.")
                    } else {

                        val parsedCount = fishCount.toIntOrNull()
                        val parsedWeight = fishWeight.toDoubleOrNull()

                        if (parsedCount == null || parsedWeight == null) {
                            snackbarHostState.showSnackbar("Wprowadź poprawne liczby.")
                            return@launch
                        }

                        val catchItem = Catch(
                            id = editingId?.toString() ?: UUID.randomUUID().toString(),
                            fishCount = parsedCount,
                            fishWeight = parsedWeight,
                            location = location,
                            notes = notes,
                            date = selectedDate
                        )

                        if (editingId != null) {
                            repository.update(catchItem)
                            snackbarHostState.showSnackbar("Zaktualizowano połów.")
                        } else {
                            repository.insert(catchItem)
                            snackbarHostState.showSnackbar("Dodano nowy połów.")
                        }

                        onFinish()
                    }
                }
            }) {
                Text(if (editingId != null) "Zapisz zmiany" else "Dodaj połów")
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
