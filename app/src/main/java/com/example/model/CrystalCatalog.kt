package com.example.model

import androidx.compose.ui.graphics.Color

object CrystalCatalog {
    val SKINS = listOf(
        CrystalSkin(
            id = "amethyst",
            name = "Celestial Amethyst",
            description = "The classic seer's crystal, pulsing with arcane ultraviolet light and clairvoyant tranquility.",
            primaryColor = Color(0xFF6B21A8),
            secondaryColor = Color(0xFFA855F7),
            accentColor = Color(0xFFE9D5FF),
            glowColor = Color(0xFF9333EA),
            priceStardust = 0,
            isPremiumOnly = false,
            runeColor = Color(0xFFE9D5FF)
        ),
        CrystalSkin(
            id = "emerald",
            name = "Emerald Oracle",
            description = "Infused with deep earth vibrations, ancient woodland spirits, and emerald clarity.",
            primaryColor = Color(0xFF064E3B),
            secondaryColor = Color(0xFF059669),
            accentColor = Color(0xFF6EE7B7),
            glowColor = Color(0xFF10B981),
            priceStardust = 150,
            isPremiumOnly = false,
            runeColor = Color(0xFFA7F3D0)
        ),
        CrystalSkin(
            id = "obsidian",
            name = "Solar Obsidian",
            description = "Forged in primordial magma, carrying volcanic fire and uncompromising truth.",
            primaryColor = Color(0xFF1F1206),
            secondaryColor = Color(0xFFEA580C),
            accentColor = Color(0xFFFBBF24),
            glowColor = Color(0xFFF97316),
            priceStardust = 250,
            isPremiumOnly = false,
            runeColor = Color(0xFFFDE68A)
        ),
        CrystalSkin(
            id = "blood_moon",
            name = "Blood Moon Ruby",
            description = "Attuned to total lunar eclipses, unveiling hidden desires and twilight portents.",
            primaryColor = Color(0xFF4C0519),
            secondaryColor = Color(0xFFE11D48),
            accentColor = Color(0xFFFDA4AF),
            glowColor = Color(0xFFF43F5E),
            priceStardust = 350,
            isPremiumOnly = false,
            runeColor = Color(0xFFFFCCD5)
        ),
        CrystalSkin(
            id = "diamond",
            name = "Ethereal Diamond",
            description = "Cut from pure astral crystal, refracting cosmic light into prismatic futures.",
            primaryColor = Color(0xFF083344),
            secondaryColor = Color(0xFF06B6D4),
            accentColor = Color(0xFFE0F2FE),
            glowColor = Color(0xFF38BDF8),
            priceStardust = 500,
            isPremiumOnly = false,
            runeColor = Color(0xFFBAE6FD)
        ),
        CrystalSkin(
            id = "cyberpunk",
            name = "Cyberpunk Neon",
            description = "Synthesized in high-tech neo-tokyo alleys, running quantum divination algorithms.",
            primaryColor = Color(0xFF1E1035),
            secondaryColor = Color(0xFFD946EF),
            accentColor = Color(0xFF06B6D4),
            glowColor = Color(0xFFEC4899),
            priceStardust = 650,
            isPremiumOnly = false,
            runeColor = Color(0xFF67E8F9)
        ),
        CrystalSkin(
            id = "void",
            name = "Cosmic Void",
            description = "Contains the gravitational mystery of a dying star and the silence of deep space.",
            primaryColor = Color(0xFF050512),
            secondaryColor = Color(0xFF312E81),
            accentColor = Color(0xFF818CF8),
            glowColor = Color(0xFF4338CA),
            priceStardust = 800,
            isPremiumOnly = false,
            runeColor = Color(0xFFC7D2FE)
        ),
        CrystalSkin(
            id = "archmage",
            name = "Archmage Eternity",
            description = "The ultimate mystical relic. Blazes with solar gold runes and sacred starlight geometry.",
            primaryColor = Color(0xFF382305),
            secondaryColor = Color(0xFFD97706),
            accentColor = Color(0xFFFEF08A),
            glowColor = Color(0xFFF59E0B),
            priceStardust = 0,
            isPremiumOnly = true,
            runeColor = Color(0xFFFFFBEB)
        )
    )

    val ANIMATIONS = listOf(
        OrbAnimation("mist", "Mystic Mist", "Gentle ethereal vapors curling within the crystal.", 0),
        OrbAnimation("sparks", "Stellar Sparks", "Bursting micro-constellations and glittering stardust.", 100),
        OrbAnimation("galaxies", "Swirling Galaxies", "Dual spiral nebulae dancing in celestial orbital harmony.", 200),
        OrbAnimation("runes", "Sacred Runes", "Floating ancient glyphs rotating around the orb equator.", 300),
        OrbAnimation("lightning", "Lightning Arc", "Crackling arcane plasma leaping against the crystal surface.", 400)
    )

    val STYLES = listOf(
        FortuneStyle("cosmic", "Cosmic Oracle", "Speaks of stars, quantum realities, and stellar alignments.", "Star Alignment", 0),
        FortuneStyle("ancient", "Ancient Sage", "Delivers solemn proverbs, archaic prophecies, and mythic wisdom.", "Mythic Lore", 0),
        FortuneStyle("sassy", "Sassy Mystic", "Sharp, witty, direct, and doesn't sugarcoat the cosmic tea.", "Witty & Direct", 150),
        FortuneStyle("enigma", "Cryptic Enigma", "Speaks in shadowy riddles, twilight echoes, and eerie poetry.", "Dark Poetry", 250),
        FortuneStyle("zen", "Zen Master", "Brings mindfulness, inner balance, and peaceful clarity.", "Tranquil Insight", 200)
    )

    val LANGUAGES = listOf(
        AppLanguage("en", "English", "🇺🇸"),
        AppLanguage("es", "Español", "🇪🇸"),
        AppLanguage("fr", "Français", "🇫🇷"),
        AppLanguage("de", "Deutsch", "🇩🇪"),
        AppLanguage("ja", "日本語", "🇯🇵"),
        AppLanguage("zh", "简体中文", "🇨🇳"),
        AppLanguage("pt", "Português", "🇧🇷"),
        AppLanguage("it", "Italiano", "🇮🇹")
    )

    fun getSkin(id: String): CrystalSkin = SKINS.find { it.id == id } ?: SKINS.first()
    fun getAnimation(id: String): OrbAnimation = ANIMATIONS.find { it.id == id } ?: ANIMATIONS.first()
    fun getStyle(id: String): FortuneStyle = STYLES.find { it.id == id } ?: STYLES.first()
}
