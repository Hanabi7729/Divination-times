package com.example.data.repository

import com.example.data.local.DivinationDao
import com.example.data.local.DivinationEntity
import com.example.data.local.UserPreferences
import kotlinx.coroutines.flow.Flow

class DivinationRepository(
    private val dao: DivinationDao,
    private val preferences: UserPreferences
) {
    val allRecords: Flow<List<DivinationEntity>> = dao.getAllRecords()
    val favoriteRecords: Flow<List<DivinationEntity>> = dao.getFavoriteRecords()

    fun getRecordsByType(type: String): Flow<List<DivinationEntity>> = dao.getRecordsByType(type)

    suspend fun saveDivination(record: DivinationEntity): Long = dao.insertRecord(record)

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = dao.updateFavorite(id, isFavorite)

    suspend fun deleteRecord(id: Long) = dao.deleteById(id)

    suspend fun clearHistory() = dao.clearAll()

    // Preferences delegates
    var isPremium: Boolean
        get() = preferences.isPremium
        set(value) { preferences.isPremium = value }

    var stardust: Int
        get() = preferences.stardust
        set(value) { preferences.stardust = value }

    var divinationCount: Int
        get() = preferences.divinationCount
        set(value) { preferences.divinationCount = value }

    var selectedSkinId: String
        get() = preferences.selectedSkinId
        set(value) { preferences.selectedSkinId = value }

    var selectedAnimationId: String
        get() = preferences.selectedAnimationId
        set(value) { preferences.selectedAnimationId = value }

    var selectedStyleId: String
        get() = preferences.selectedStyleId
        set(value) { preferences.selectedStyleId = value }

    var unlockedSkinIds: Set<String>
        get() = preferences.unlockedSkinIds
        set(value) { preferences.unlockedSkinIds = value }

    var unlockedAnimationIds: Set<String>
        get() = preferences.unlockedAnimationIds
        set(value) { preferences.unlockedAnimationIds = value }

    var unlockedStyleIds: Set<String>
        get() = preferences.unlockedStyleIds
        set(value) { preferences.unlockedStyleIds = value }

    var languageCode: String
        get() = preferences.languageCode
        set(value) { preferences.languageCode = value }

    var simulateOffline: Boolean
        get() = preferences.simulateOffline
        set(value) { preferences.simulateOffline = value }

    var lastDailyMeditationDate: Long
        get() = preferences.lastDailyMeditationDate
        set(value) { preferences.lastDailyMeditationDate = value }
}
