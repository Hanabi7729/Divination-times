package com.example.model

import androidx.compose.ui.graphics.Color

enum class OracleType {
    YES_NO,
    CHOICE
}

enum class VerdictType {
    YES,
    NO,
    MYSTIC_MAYBE
}

data class CrystalSkin(
    val id: String,
    val name: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val glowColor: Color,
    val priceStardust: Int,
    val isPremiumOnly: Boolean = false,
    val runeColor: Color = Color.White
)

data class OrbAnimation(
    val id: String,
    val name: String,
    val description: String,
    val priceStardust: Int
)

data class FortuneStyle(
    val id: String,
    val name: String,
    val description: String,
    val toneTag: String,
    val priceStardust: Int
)

data class AppLanguage(
    val code: String,
    val displayName: String,
    val flagEmoji: String
)

data class YesNoAnswer(
    val idNumber: Int,
    val verdict: VerdictType,
    val shortAnswer: String,
    val cosmicWisdom: String,
    val alignmentPercentage: Int
)

data class ChoiceResult(
    val selectedOption: String,
    val allOptions: List<String>,
    val cosmicHarmony: Int,
    val propheticReason: String,
    val styleUsed: String
)
