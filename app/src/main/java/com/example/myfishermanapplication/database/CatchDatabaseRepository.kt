package com.example.myfishermanapplication.database

import com.example.myfishermanapplication.model.Catch
import kotlinx.coroutines.flow.Flow

class CatchRepository(private val catchDao: CatchDao) {

    val allCatches: Flow<List<Catch>> = catchDao.getAllCatches()

    fun getTotalCatches(): Flow<Int> = catchDao.getTotalCatches()

    fun getAllWeights(): Flow<List<String>> = catchDao.getAllWeights()

    fun getAllLocations(): Flow<List<String>> = catchDao.getAllLocations()

    suspend fun insert(catch: Catch) {
        catchDao.insertCatch(catch)
    }

    suspend fun update(catch: Catch) {
        catchDao.updateCatch(catch)
    }

    suspend fun delete(catch: Catch) {
        catchDao.deleteCatch(catch)
    }

    suspend fun getCatchById(id: String): Catch? {
        return catchDao.getCatchById(id)
    }
}
