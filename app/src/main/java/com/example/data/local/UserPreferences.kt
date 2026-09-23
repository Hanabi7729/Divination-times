package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("crystal_oracle_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PREMIUM = "key_is_premium"
        private const val KEY_STARDUST = "key_stardust"
        private const val KEY_DIVINATION_COUNT = "key_divination_count"
        private const val KEY_SELECTED_SKIN = "key_selected_skin"
        private const val KEY_SELECTED_ANIMATION = "key_selected_anim"
        private const val KEY_SELECTED_STYLE = "key_selected_style"
        private const val KEY_UNLOCKED_SKINS = "key_unlocked_skins"
        private const val KEY_UNLOCKED_ANIMATIONS = "key_unlocked_anims"
        private const val KEY_UNLOCKED_STYLES = "key_unlocked_styles"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_SIMULATE_OFFLINE = "key_simulate_offline"
        private const val KEY_LAST_DAILY_MEDITATION = "key_last_meditation"
    }

    var isPremium: Boolean
        get() = prefs.getBoolean(KEY_PREMIUM, false)
        set(value) = prefs.edit().putBoolean(KEY_PREMIUM, value).apply()

    var stardust: Int
        get() = prefs.getInt(KEY_STARDUST, 120) // Give 120 free starter stardust
        set(value) = prefs.edit().putInt(KEY_STARDUST, value).apply()

    var divinationCount: Int
        get() = prefs.getInt(KEY_DIVINATION_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_DIVINATION_COUNT, value).apply()

    var selectedSkinId: String
        get() = prefs.getString(KEY_SELECTED_SKIN, "amethyst") ?: "amethyst"
        set(value) = prefs.edit().putString(KEY_SELECTED_SKIN, value).apply()

    var selectedAnimationId: String
        get() = prefs.getString(KEY_SELECTED_ANIMATION, "mist") ?: "mist"
        set(value) = prefs.edit().putString(KEY_SELECTED_ANIMATION, value).apply()

    var selectedStyleId: String
        get() = prefs.getString(KEY_SELECTED_STYLE, "cosmic") ?: "cosmic"
        set(value) = prefs.edit().putString(KEY_SELECTED_STYLE, value).apply()

    var unlockedSkinIds: Set<String>
        get() = prefs.getStringSet(KEY_UNLOCKED_SKINS, setOf("amethyst")) ?: setOf("amethyst")
        set(value) = prefs.edit().putStringSet(KEY_UNLOCKED_SKINS, value).apply()

    var unlockedAnimationIds: Set<String>
        get() = prefs.getStringSet(KEY_UNLOCKED_ANIMATIONS, setOf("mist")) ?: setOf("mist")
        set(value) = prefs.edit().putStringSet(KEY_UNLOCKED_ANIMATIONS, value).apply()

    var unlockedStyleIds: Set<String>
        get() = prefs.getStringSet(KEY_UNLOCKED_STYLES, setOf("cosmic", "ancient")) ?: setOf("cosmic", "ancient")
        set(value) = prefs.edit().putStringSet(KEY_UNLOCKED_STYLES, value).apply()

    var languageCode: String
        get() = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    var simulateOffline: Boolean
        get() = prefs.getBoolean(KEY_SIMULATE_OFFLINE, false)
        set(value) = prefs.edit().putBoolean(KEY_SIMULATE_OFFLINE, value).apply()

    var lastDailyMeditationDate: Long
        get() = prefs.getLong(KEY_LAST_DAILY_MEDITATION, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_DAILY_MEDITATION, value).apply()
}
