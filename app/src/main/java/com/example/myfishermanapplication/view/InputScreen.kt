package com.example.myfishermanapplication.view

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myfishermanapplication.database.CatchDatabaseRepository
import com.example.myfishermanapplication.model.Catch
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape


@Composable
fun InputScreen(
    navController: NavController,
    repository: CatchDatabaseRepository,
    editingId: String? = null
) {
    val context = LocalContext.current
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
            .fillMaxSize()
            .padding(10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.85f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (editingId != null) "Edytuj połów" else "Co dzisiaj złowiłeś?",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            @Composable
            fun textFieldColors() = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                disabledBorderColor = Color.Gray,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                disabledLabelColor = Color.LightGray,
                cursorColor = Color.White
            )

            OutlinedTextField(
                value = fishCount,
                onValueChange = { fishCount = it },
                label = { Text("Liczba złowionych ryb") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            OutlinedTextField(
                value = fishWeight,
                onValueChange = { fishWeight = it },
                label = { Text("Łączna waga ryb") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Miejsce połowu") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notatki") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                label = { Text("Wybierz datę") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                enabled = false,
                colors = textFieldColors()
            )

            Button(
                onClick = {
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
                                id = editingId ?: UUID.randomUUID().toString(),
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

                            navController.popBackStack()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (editingId != null) "Zapisz zmiany" else "Dodaj połów")
            }
        }
    }
}
