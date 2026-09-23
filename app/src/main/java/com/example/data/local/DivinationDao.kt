package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DivinationDao {
    @Query("SELECT * FROM divination_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<DivinationEntity>>

    @Query("SELECT * FROM divination_records WHERE type = :type ORDER BY timestamp DESC")
    fun getRecordsByType(type: String): Flow<List<DivinationEntity>>

    @Query("SELECT * FROM divination_records WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteRecords(): Flow<List<DivinationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DivinationEntity): Long

    @Query("UPDATE divination_records SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM divination_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM divination_records")
    suspend fun clearAll()
}
