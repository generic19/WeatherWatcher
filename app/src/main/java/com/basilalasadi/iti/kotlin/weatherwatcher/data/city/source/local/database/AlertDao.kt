package com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.database.dto.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("select * from AlertEntity")
    fun getAlertsFlow(): Flow<List<AlertEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAlert(alert: AlertEntity): Long
    
    @Delete
    suspend fun removeAlert(alert: AlertEntity): Int
    
    @Query("update AlertEntity set isActive = :isActive where requestCode = :requestCode")
    suspend fun setActive(requestCode: Int, isActive: Boolean): Int
}