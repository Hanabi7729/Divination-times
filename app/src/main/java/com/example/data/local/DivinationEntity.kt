package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "divination_records")
data class DivinationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "YES_NO" or "CHOICE"
    val questionOrTitle: String,
    val optionsJson: String = "", // Comma-separated or JSON list of options for choices
    val answerOrChoice: String,
    val cosmicInsight: String,
    val styleUsed: String,
    val skinUsed: String,
    val alignmentScore: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
