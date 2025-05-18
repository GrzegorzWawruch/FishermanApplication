package com.example.myfishermanapplication.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfishermanapplication.database.AppDatabase
import com.example.myfishermanapplication.database.FishDatabaseRepository
import com.example.myfishermanapplication.model.Fish
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KnowledgeViewModel(application: Application): AndroidViewModel(application) {
    private val repository: FishDatabaseRepository

    private val _fish = MutableStateFlow<Fish?>(null)
    val fish: StateFlow<Fish?> = _fish

    init {
        val db = AppDatabase.getDatabase(application)
        repository = FishDatabaseRepository(db)
    }

    fun loadFish(fishId: String) {
        viewModelScope.launch {
            val result = repository.getFishById(fishId)
            if (result == null) {
                Log.e("KnowledgeViewModel", "Fish not found with id: $fishId")
            }
            _fish.value = result
        }
    }

    companion object {
        fun Factory(appContext: android.content.Context): androidx.lifecycle.ViewModelProvider.Factory {
            return object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(KnowledgeViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return KnowledgeViewModel(appContext.applicationContext as Application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
