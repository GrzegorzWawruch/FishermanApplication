package com.example.myfishermanapplication.database

import androidx.room.*
import com.example.myfishermanapplication.model.Catch
import kotlinx.coroutines.flow.Flow


@Dao
interface CatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatch(c: Catch)

    @Update
    suspend fun updateCatch(c: Catch)

    @Delete
    suspend fun deleteCatch(c: Catch)

    @Query("SELECT * FROM `Catch`")
    fun getAllCatches(): Flow<List<Catch>>

    @Query("SELECT * FROM `Catch` WHERE id = :id")
    suspend fun getCatchById(id: String): Catch?

    @Query("SELECT COUNT(*) FROM `Catch`")
    fun getTotalCatches(): Flow<Int>

    @Query("SELECT fishWeight FROM `Catch`")
    fun getAllWeights(): Flow<List<String>>

    @Query("SELECT location FROM `Catch`")
    fun getAllLocations(): Flow<List<String>>
}
