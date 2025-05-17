package com.example.myfishermanapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfishermanapplication.database.CatchRepository
import com.example.myfishermanapplication.model.Catch
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StatsViewModel(private val repository: CatchRepository) : ViewModel() {

    val totalCatches = repository.getTotalCatches()

    val totalFishCount: StateFlow<Int> = flow {
        val catches = repository.getAllCatchesList()
        val totalCount = catches.sumOf {
            it.fishCount
        }
        emit(totalCount)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalWeight: StateFlow<Double> = repository.getAllWeights()
        .map { it.sum() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val mostFrequentLocation: StateFlow<String> = repository.getAllLocations()
        .map { locations ->
            locations.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "brak"
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "brak")

    private val _heaviestCatch = MutableStateFlow<Catch?>(null)
    val heaviestCatch: StateFlow<Catch?> = _heaviestCatch

    init {
        viewModelScope.launch {
            _heaviestCatch.value = repository.getHeaviestCatch()
        }
    }

//    val averageWeight: StateFlow<Double> = repository.getAllWeights()
//        .map { if (it.isNotEmpty()) it.average() else 0.0 }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val averageWeight: StateFlow<Double> = flow {
        val catches: List<Catch> = repository.getAllCatchesList()
        val totalWeight: Double = catches.sumOf { it.fishWeight }
        val totalFishCount: Int = catches.sumOf { it.fishCount }
        emit(if (totalFishCount > 0) totalWeight / totalFishCount else 0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)







}