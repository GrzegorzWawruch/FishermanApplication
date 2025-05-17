package com.example.myfishermanapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfishermanapplication.database.CatchDatabaseRepository
import com.example.myfishermanapplication.model.Catch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CatchListViewModel(private val repository: CatchDatabaseRepository) : ViewModel() {
    val allCatches: StateFlow<List<Catch>> = repository.allCatches
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun deleteCatch(catch: Catch) {
        viewModelScope.launch {
            repository.deleteCatch(catch)
        }
    }
}
