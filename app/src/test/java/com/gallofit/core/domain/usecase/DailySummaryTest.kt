package com.gallofit.core.domain.usecase

import com.gallofit.core.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("DailySummary — Resumo diário")
class DailySummaryTest {

    @Test
    @DisplayName("caloriesRemaining é calculado correctamente")
    fun `calories remaining correcto`() {
        val summary = makeSummary(
            targetCal = 2100,
            consumedCal = 1685
        )
        assertEquals(415, summary.caloriesRemaining)
    }

    @Test
    @DisplayName("Meta atingida — caloriesRemaining é negativo ou zero")
    fun `meta atingida`() {
        val summary = makeSummary(targetCal = 2100, consumedCal = 2100)
        assertEquals(0, summary.caloriesRemaining)
    }

    @ParameterizedTest(name = "Consumido={0}, Meta={1} → Restantes={2}")
    @CsvSource(
        "1000, 2100, 1100",
        "2100, 2100, 0",
        "2300, 2100, -200",
        "0, 2100, 2100"
    )
    @DisplayName("Cálculo de calorias restantes em múltiplos cenários")
    fun `calorias restantes parametrizado`(consumed: Int, target: Int, expected: Int) {
        val summary = makeSummary(targetCal = target, consumedCal = consumed)
        assertEquals(expected, summary.caloriesRemaining)
    }

    private fun makeSummary(targetCal: Int, consumedCal: Int) = DailySummary(
        date = java.time.LocalDate.now(),
        dayType = DayType.NORMAL,
        targets = MacroTargets(caloriesKcal = targetCal),
        consumed = ConsumedMacros(caloriesKcal = consumedCal),
        meals = emptyMap(),
        workouts = emptyList(),
        weight = null
    )
}
