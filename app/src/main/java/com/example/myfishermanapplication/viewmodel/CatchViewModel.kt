package com.example.myfishermanapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfishermanapplication.database.CatchDatabaseRepository
import com.example.myfishermanapplication.model.Catch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CatchViewModel(private val repository: CatchDatabaseRepository) : ViewModel() {

    val allCatches = repository.allCatches.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    fun insertCatch(catch: Catch) {
        viewModelScope.launch {
            repository.insert(catch)
        }
    }

    fun deleteCatch(catch: Catch) = viewModelScope.launch {
        repository.delete(catch)
    }

    fun updateCatch(catch: Catch) = viewModelScope.launch {
        repository.update(catch)
    }
}
