package com.example.myfishermanapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfishermanapplication.database.CatchRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StatsViewModel(private val repository: CatchRepository) : ViewModel() {

    val totalCatches = repository.getTotalCatches()

    val totalWeight: StateFlow<Double> = repository.getAllWeights()
        .map { weights ->
            weights.sumOf { it.replace(",", ".").replace("kg", "").trim().toDoubleOrNull() ?: 0.0 }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val mostFrequentLocation: StateFlow<String> = repository.getAllLocations()
        .map { locations ->
            locations.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "brak"
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "brak")
}