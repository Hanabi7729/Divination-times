package com.example

import com.example.engine.ChoiceEngine
import com.example.engine.YesNoEngine
import com.example.model.VerdictType
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun yesNoEngine_hasOverOneThousandVariations() {
    assertTrue("Should have >= 1000 base prophecies", YesNoEngine.TOTAL_PROPHECIES >= 1000)
  }

  @Test
  fun yesNoEngine_returnsValidAnswer() {
    val answer = YesNoEngine.divine("Should I embark on this mystical journey?", "cosmic", "en")
    assertNotNull(answer.shortAnswer)
    assertTrue(answer.shortAnswer.isNotBlank())
    assertNotNull(answer.cosmicWisdom)
    assertTrue(answer.cosmicWisdom.isNotBlank())
    assertTrue(answer.alignmentPercentage in 0..100)
    assertTrue(answer.verdict == VerdictType.YES || answer.verdict == VerdictType.NO)
  }

  @Test
  fun yesNoEngine_supportsMultipleLanguages() {
    val languages = listOf("es", "fr", "de", "ja", "zh", "pt", "it")
    for (lang in languages) {
      val answer = YesNoEngine.divine("Will destiny guide me?", "cosmic", lang)
      assertTrue(answer.shortAnswer.isNotBlank())
    }
  }

  @Test
  fun choiceEngine_selectsFromProvidedOptions() {
    val options = listOf("Amethyst", "Sapphire", "Emerald", "Ruby")
    val result = ChoiceEngine.divineChoice(options, "ancient", "Ancient Sage")
    assertTrue(options.contains(result.selectedOption))
    assertTrue(result.cosmicHarmony in 70..100)
    assertTrue(result.propheticReason.isNotBlank())
  }
}
