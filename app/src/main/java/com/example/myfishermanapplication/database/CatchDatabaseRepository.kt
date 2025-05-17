package com.example.myfishermanapplication.database

import com.example.myfishermanapplication.model.Catch
import kotlinx.coroutines.flow.Flow

class CatchDatabaseRepository(private val catchDao: CatchDao) {

    val allCatches: Flow<List<Catch>> = catchDao.getAllCatches()

    fun getTotalCatches(): Flow<Int> = catchDao.getTotalCatches()

    fun getTotalFish(): Flow<Int> = catchDao.getTotalFish()

    fun getAllWeights(): Flow<List<Double>> = catchDao.getAllWeights()

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

    suspend fun deleteCatch(catch: Catch) {
        catchDao.deleteCatch(catch)
    }

    suspend fun getCatchById(id: String): Catch? {
        return catchDao.getCatchById(id)
    }

    suspend fun getAllCatchesList(): List<Catch> {
        return catchDao.getAllCatchesList()
    }

    suspend fun getHeaviestCatch(): Catch? {
        return catchDao.getAllCatchesList()
            .maxByOrNull { it.fishWeight }
    }


}
