package com.example.myfishermanapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfishermanapplication.database.AppDatabase
import com.example.myfishermanapplication.database.FishDatabaseRepository
import com.example.myfishermanapplication.model.Fish
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FishDatabaseRepository
    private val _fishList = MutableStateFlow<List<Fish>>(emptyList())
    val fishList: StateFlow<List<Fish>> = _fishList

    init {
        val db = AppDatabase.getDatabase(application)
        repository = FishDatabaseRepository(db)
        getAllFish()
    }

    private fun getAllFish() {
        viewModelScope.launch {
            val fishes = repository.getAllFish()
            Log.d("GalleryViewModel", "Fishes from DB: $fishes")
            Log.d("GalleryViewModel", "Fishes count: ${fishes.size}")
            _fishList.value = fishes
        }
    }
}
