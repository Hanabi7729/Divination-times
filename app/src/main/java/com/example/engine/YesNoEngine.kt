package com.example.engine

import com.example.model.VerdictType
import com.example.model.YesNoAnswer
import kotlin.random.Random

object YesNoEngine {

    const val TOTAL_PROPHECIES = 1250

    // Curated iconic affirmative short answers
    private val YES_SHORT_ANSWERS = listOf(
        "Undeniably Yes", "The Stars Confirm: Yes", "Without a Shadow of Doubt", "Fate Decrees: Yes",
        "Absolutely Yes", "The Runes Whisper Yes", "Most Certain", "Affirmative, Seeker",
        "It Is Written: Yes", "The Cosmos Leans Yes", "Yes, with Blessed Light", "All Signs Point to Yes",
        "Beyond Doubt, Yes", "The Oracle Sanctions: Yes", "Destiny Unfolds: Yes", "The Leylines Pulse Yes",
        "A Resounding Yes", "The Loom Weaves Yes", "Yes, Strike Forward", "Gold Shines on Yes",
        "Clear As Crystal: Yes", "A Victorious Yes", "By the High Heavens, Yes", "The Pendulum Swings Yes",
        "Yes, and May Good Fortune Follow", "The Moon's Full Blessing: Yes", "It Cannot Be Otherwise: Yes",
        "Affirmed by the Void", "Yes, the Gateway Opens", "Yes, Without Hesitation", "The Phoenix Rises: Yes",
        "A Million Constellations Say Yes", "Yes, the Current Carries You", "True and Certain: Yes",
        "The Sacred Flame Burns Yes", "Yes, As It Was Foreseen", "Favorable Tidings: Yes", "Yes, Stand Confident",
        "The Chalice Overflows: Yes", "An Auspicious Yes"
    )

    // Curated iconic negative short answers
    private val NO_SHORT_ANSWERS = listOf(
        "Decisively No", "The Stars Forbid It", "Turn Back: No", "Fate Closes the Gate: No",
        "The Void Denies: No", "Heed the Warning: No", "Not in This Cycle", "The Runes Turn Dark: No",
        "Extinguish the Thought: No", "The Abyss Answers No", "No, for Your Own Protection", "A Firm Denial",
        "The Path Is Barred: No", "No, Walk Another Road", "The Leylines Recoil: No", "An Absolute No",
        "The Loom Rejects: No", "No, Trouble Awaits", "Cast It to the Shadows: No", "The Cold Truth: No",
        "The Mirror Shatters: No", "No, Withhold Your Hand", "By the Eclipsed Sun, No", "The Pendulum Freezes: No",
        "No, Let It Rest in Peace", "The Moon Wanes in Silence: No", "Impossible in This Timeline",
        "Denied by the Ancients", "No, the Door Remains Locked", "No, Beware the Illusion", "The Serpent Hisses No",
        "A Thousand Silences Say No", "No, the Current Is Against You", "False Horizon: No",
        "The Sacred Flame Dies: No", "No, As the Shadows Warn", "Adverse Omens: No", "No, Preserve Your Energy",
        "The Chalice Is Empty: No", "An Uncompromising No"
    )

    // Combinatorial parts to synthesize thousands of unique readings:
    private val INVOCATIONS = listOf(
        "As the astral mists part,",
        "By the ancient decrees of the celestial sphere,",
        "The cosmic resonance reveals that",
        "In the quiet depths of the crystal orb,",
        "Peering through the veil of time,",
        "The sacred planetary geometries declare that",
        "Across the infinite branching timelines,",
        "From the whispering ethereal winds,",
        "The ancient elders of the star-forge proclaim:",
        "Beneath the watchful eye of the silver moon,",
        "The vibrations of the hidden leyline pulse:",
        "As the arcane constellations align in silence,",
        "The obsidian mirror reflects the cosmic truth:",
        "Through the prism of ethereal light,",
        "Upon the eternal loom of destiny,"
    )

    private val YES_CONDITIONS = listOf(
        "the universe actively clears your obstacles.",
        "fortune is strongly tilted in your favor.",
        "your spirit is synchronized with the celestial tide.",
        "unseen protectors stand behind your intent.",
        "every wind blows toward your triumph.",
        "what you seek is already seeking you.",
        "the cosmic scales tip directly toward success.",
        "the hour is ripe for bold action.",
        "the shadows retreat before your luminous purpose.",
        "harmony has been granted from on high.",
        "the golden threads of fate intertwine with your wish.",
        "the cosmic forces rally to champion your cause."
    )

    private val NO_CONDITIONS = listOf(
        "this endeavor leads toward jagged stones and regret.",
        "a hidden storm brews along that horizon.",
        "the karmic cost far outweighs the fleeting prize.",
        "the current timeline resists this direction violently.",
        "your true path lies 180 degrees elsewhere.",
        "what seems golden now will rust before the next moon.",
        "fate has a vastly superior blessing waiting if you walk away.",
        "unseen snares are woven into this proposition.",
        "patience is your only shield against ruin.",
        "the door is intentionally locked for your sanctuary.",
        "the astral winds blow cold with warning.",
        "to force this gate would break your inner equilibrium."
    )

    private val ADVICE_YES = listOf(
        "Walk forward without glancing back.",
        "Trust the quiet intuition humming in your chest.",
        "Speak your truth boldly to the world.",
        "Plant the seed today; harvest shall be abundant.",
        "Do not let mortal doubts slow your stride.",
        "Keep your focus pure and celebrate the turning tide.",
        "Open your hands and receive the unfolding grace.",
        "Strike while the cosmic iron glows incandescent."
    )

    private val ADVICE_NO = listOf(
        "Step back and let the illusion dissolve.",
        "Conserve your vitality for the true battle ahead.",
        "Rejoice that this detour was kept from you.",
        "Breathe deeply and seek peace in stillness.",
        "Do not bargain with what was meant to be released.",
        "Look to your left; an untrodden path awaits.",
        "Silence your haste and wait for the true sunrise.",
        "Close this ledger and turn to a cleaner parchment."
    )

    // Multi-language short overrides
    private val LOCALIZED_YES = mapOf(
        "es" to listOf("Sí Rotundo", "El Cosmos Afirma: Sí", "Sin Duda Alguna, Sí", "El Destino Dice Sí", "Totalmente Sí", "Las Runas Susurran Sí"),
        "fr" to listOf("Oui Absolu", "Les Astres Disent Oui", "Sans Aucun Doute: Oui", "Le Destin Confirme: Oui", "Oui Rayonnant", "Les Runes Murmurent Oui"),
        "de" to listOf("Zweifellos Ja", "Die Sterne Bestätigen: Ja", "Ohne Jeden Zweifel", "Das Schicksal Spricht: Ja", "Vollkommen Ja", "Die Runen Flüstern Ja"),
        "ja" to listOf("揺るぎなき肯定（YES）", "星々が示す答え：YES", "一点の曇りもなきYES", "運命の裁定：YES", "天の導き：YES", "水晶が輝く：YES"),
        "zh" to listOf("毋庸置疑：是", "群星昭示：吉（YES）", "天命所归：是", "乾坤已定：成", "符文显现：是", "星轨正向：YES"),
        "pt" to listOf("Sim Absoluto", "As Estrelas Confirmam: Sim", "Sem Sombra de Dúvida", "O Destino Diz Sim", "Totalmente Sim", "As Runas Murmuram Sim"),
        "it" to listOf("Assolutamente Sì", "Le Stelle Confermano: Sì", "Senza Alcun Dubbio", "Il Destino Dice Sì", "Certamente Sì", "Le Rune Sussurrano Sì")
    )

    private val LOCALIZED_NO = mapOf(
        "es" to listOf("Rotundamente No", "Los Astros Niegan: No", "Detente: No", "El Destino Cierra la Puerta", "Totalmente No", "Las Sombras Advierten: No"),
        "fr" to listOf("Non Catégorique", "Les Astres S'y Opposent: Non", "Faites Demi-Tour: Non", "Le Destin Refuse: Non", "Pas Maintenant", "Les Ombres Avertissent: Non"),
        "de" to listOf("Klares Nein", "Die Sterne Verbieten Es", "Kehre Um: Nein", "Das Schicksal Verwehrt Es", "Ganz Klar Nein", "Warnung der Schatten: Nein"),
        "ja" to listOf("断固たる否定（NO）", "星々が拒絶：NO", "立ち止まれ：NO", "運命が閉ざす扉：NO", "凶兆あり：NO", "水晶が曇る：NO"),
        "zh" to listOf("断然不可：否", "群星逆位：凶（NO）", "此路不通：否", "天命劝阻：否", "符文晦暗：否", "收敛锋芒：NO"),
        "pt" to listOf("Não Categórico", "As Estrelas Negam: Não", "Dê Meia-Volta: Não", "O Destino Fecha a Porta", "Totalmente Não", "As Sombras Avisam: Não"),
        "it" to listOf("Decisamente No", "Le Stelle Si Oppongono: No", "Torna Indietro: No", "Il Destino Nega: No", "Certamente No", "Le Ombre Avvisano: No")
    )

    fun divine(
        question: String,
        styleId: String,
        languageCode: String
    ): YesNoAnswer {
        // Unique deterministic seed using question hash + random salt to allow varied consultation
        val seed = if (question.isNotBlank()) {
            Math.abs(question.trim().hashCode() + Random.nextInt(10000))
        } else {
            Random.nextInt(100000)
        }
        val rng = Random(seed)

        // Generate unique catalog prophecy index between 1 and 1250+
        val idNumber = (Math.abs(seed) % TOTAL_PROPHECIES) + 1
        val isYes = rng.nextBoolean()
        val verdict = if (isYes) VerdictType.YES else VerdictType.NO

        // Select short answer
        val baseShort = if (isYes) {
            val localizedList = LOCALIZED_YES[languageCode]
            if (localizedList != null && localizedList.isNotEmpty()) {
                localizedList[rng.nextInt(localizedList.size)]
            } else {
                YES_SHORT_ANSWERS[rng.nextInt(YES_SHORT_ANSWERS.size)]
            }
        } else {
            val localizedList = LOCALIZED_NO[languageCode]
            if (localizedList != null && localizedList.isNotEmpty()) {
                localizedList[rng.nextInt(localizedList.size)]
            } else {
                NO_SHORT_ANSWERS[rng.nextInt(NO_SHORT_ANSWERS.size)]
            }
        }

        // Build cosmic wisdom message tailored by fortune style
        val invocation = INVOCATIONS[rng.nextInt(INVOCATIONS.size)]
        val condition = if (isYes) {
            YES_CONDITIONS[rng.nextInt(YES_CONDITIONS.size)]
        } else {
            NO_CONDITIONS[rng.nextInt(NO_CONDITIONS.size)]
        }
        val advice = if (isYes) {
            ADVICE_YES[rng.nextInt(ADVICE_YES.size)]
        } else {
            ADVICE_NO[rng.nextInt(ADVICE_NO.size)]
        }

        val cosmicWisdom = when (styleId) {
            "sassy" -> if (isYes) {
                "Honey, the crystal ball is practically screaming YES. Stop overthinking and go get it!"
            } else {
                "Darling, absolutely not. The cosmos said 'delete that idea immediately', and you should listen."
            }
            "ancient" -> if (isYes) {
                "Hearken! As carved upon the primordial monoliths: what is willed with honor shall come to pass. $advice"
            } else {
                "Heed the warning of the ancestors! The cup contains bitter dregs. Step back from the precipice."
            }
            "enigma" -> if (isYes) {
                "A solitary raven takes flight into golden dawn. The key turns smoothly in the iron lock. It is so."
            } else {
                "A bell rings under black water. Shadows lengthen across the path. Turn away before nightfall."
            }
            "zen" -> if (isYes) {
                "Like water flowing naturally downhill, this path brings clarity and peace. Move forward with gentle steps."
            } else {
                "The stone does not force the river. Let this go with a calm breath, and clarity will return."
            }
            else -> "$invocation $condition $advice"
        }

        val alignment = if (isYes) rng.nextInt(78, 100) else rng.nextInt(12, 45)

        return YesNoAnswer(
            idNumber = idNumber,
            verdict = verdict,
            shortAnswer = baseShort,
            cosmicWisdom = cosmicWisdom,
            alignmentPercentage = alignment
        )
    }
}
