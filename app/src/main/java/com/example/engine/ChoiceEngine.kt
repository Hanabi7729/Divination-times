package com.example.engine

import com.example.model.ChoiceResult
import kotlin.random.Random

object ChoiceEngine {

    fun divineChoice(
        options: List<String>,
        styleId: String,
        styleName: String
    ): ChoiceResult {
        val cleanOptions = options.map { it.trim() }.filter { it.isNotBlank() }
        require(cleanOptions.isNotEmpty()) { "Options list cannot be empty" }

        val winnerIndex = Random.nextInt(cleanOptions.size)
        val selectedOption = cleanOptions[winnerIndex]
        val harmony = Random.nextInt(88, 100)

        val propheticReason = when (styleId) {
            "sassy" -> "Look, between everything on this table, '${selectedOption}' is the only choice that doesn't cause cosmic chaos. Treat yourself!"
            "ancient" -> "From the sacred scroll of outcomes, the elder signs manifest with clarity upon '${selectedOption}'. Destiny smiles upon this road."
            "enigma" -> "A silver feather floats down from the twilight canopy and rests upon '${selectedOption}'. The invisible threads weave in its favor."
            "zen" -> "When the mind becomes tranquil like a still lake, '${selectedOption}' surfaces with the most harmonious ripples of peace."
            else -> "The celestial alignments converge with remarkable gravitational pull directly onto '${selectedOption}', signaling unmatched synchronicity."
        }

        return ChoiceResult(
            selectedOption = selectedOption,
            allOptions = cleanOptions,
            cosmicHarmony = harmony,
            propheticReason = propheticReason,
            styleUsed = styleName
        )
    }
}
