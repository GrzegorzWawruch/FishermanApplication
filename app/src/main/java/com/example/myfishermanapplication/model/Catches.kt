package com.example.myfishermanapplication.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity
data class Catch(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val fishCount: Int,
    val fishWeight: Double,
    val location: String,
    val notes: String,
    val date: String
): Parcelable