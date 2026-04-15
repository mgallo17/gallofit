package com.gallofit.core.domain.usecase

import com.gallofit.core.domain.model.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.DayOfWeek
import java.time.LocalDate

@DisplayName("MacroTargets — Cálculo de metas diárias")
class GetMacroTargetsForDayUseCaseTest {

    private lateinit var profile: UserProfile

    @BeforeEach
    fun setup() {
        profile = UserProfile(
            defaultMacros = MacroTargets(caloriesKcal = 2100, proteinG = 190, carbsG = 160, fatG = 65),
            doubleDayMacros = MacroTargets(caloriesKcal = 2200, proteinG = 190, carbsG = 170, fatG = 65),
            doubleDays = setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)
        )
    }

    @Nested
    @DisplayName("Tipo de dia")
    inner class DayTypeTests {

        @Test
        @DisplayName("Segunda é dia NORMAL — meta 2100 kcal")
        fun `segunda é dia normal`() {
            val monday = LocalDate.of(2026, 4, 13)
            assertEquals(DayType.NORMAL, getDayType(monday, profile))
            assertEquals(2100, getTargets(monday, profile).caloriesKcal)
        }

        @Test
        @DisplayName("Terça é dia DUPLO — meta 2200 kcal")
        fun `terça é dia duplo`() {
            val tuesday = LocalDate.of(2026, 4, 14)
            assertEquals(DayType.DOUBLE, getDayType(tuesday, profile))
            assertEquals(2200, getTargets(tuesday, profile).caloriesKcal)
        }

        @Test
        @DisplayName("Quinta é dia DUPLO — meta 2200 kcal")
        fun `quinta é dia duplo`() {
            val thursday = LocalDate.of(2026, 4, 17)
            assertEquals(DayType.DOUBLE, getDayType(thursday, profile))
            assertEquals(2200, getTargets(thursday, profile).caloriesKcal)
        }

        @ParameterizedTest(name = "DayOfWeek={0} → NORMAL")
        @CsvSource("MONDAY", "WEDNESDAY", "FRIDAY", "SATURDAY", "SUNDAY")
        @DisplayName("Dias que NÃO são duplos → meta normal")
        fun `dias nao duplos têm meta normal`(dayName: String) {
            val day = DayOfWeek.valueOf(dayName)
            val date = LocalDate.of(2026, 4, 13).with(day)
            assertEquals(DayType.NORMAL, getDayType(date, profile))
        }
    }

    @Nested
    @DisplayName("Cálculo de macros consumidos")
    inner class ConsumedMacrosTests {

        @Test
        @DisplayName("Soma correcta de calorias de múltiplas refeições")
        fun `soma calorias correctamente`() {
            val entries = listOf(
                makeFoodEntry(caloriesKcal = 390, proteinG = 34.0, carbsG = 48.0, fatG = 5.0),  // whey
                makeFoodEntry(caloriesKcal = 530, proteinG = 44.0, carbsG = 30.5, fatG = 22.7), // carne+batata
                makeFoodEntry(caloriesKcal = 110, proteinG = 8.0, carbsG = 14.0, fatG = 2.0),   // iogurte
            )
            val consumed = calcConsumed(entries)
            assertEquals(1030, consumed.caloriesKcal)
            assertEquals(86.0, consumed.proteinG, 0.01)
        }

        @Test
        @DisplayName("Sem refeições → tudo zero")
        fun `sem refeições tudo zero`() {
            val consumed = calcConsumed(emptyList())
            assertEquals(0, consumed.caloriesKcal)
            assertEquals(0.0, consumed.proteinG)
        }

        @Test
        @DisplayName("Dia do Matheus: 14 Abril → 1685 kcal / 145g prot")
        fun `dia real matheus`() {
            val entries = listOf(
                makeFoodEntry(caloriesKcal = 110, proteinG = 8.0),   // iogurte+morangos
                makeFoodEntry(caloriesKcal = 530, proteinG = 44.0),  // almoço
                makeFoodEntry(caloriesKcal = 530, proteinG = 44.0),  // jantar
                makeFoodEntry(caloriesKcal = 375, proteinG = 15.0),  // empanada
                makeFoodEntry(caloriesKcal = 200, proteinG = 0.0),   // 2 taças vinho
                makeFoodEntry(caloriesKcal = 390, proteinG = 34.0),  // whey noite
            )
            val consumed = calcConsumed(entries)
            assertEquals(2135, consumed.caloriesKcal)
            assertEquals(145.0, consumed.proteinG, 0.01)
        }
    }

    @Nested
    @DisplayName("Templates pré-carregados")
    inner class TemplateTests {

        @Test
        @DisplayName("Whey Padrão → 390 kcal, 34g proteína")
        fun `whey padrao tem macros correctos`() {
            val template = DefaultTemplates.WHEY_PADRAO
            assertTrue(template.totalCalories in 385..395, "Expected ~390 kcal, got ${template.totalCalories}")
            assertTrue(template.totalProtein >= 30.0)
        }

        @Test
        @DisplayName("Todos os templates têm nome e pelo menos 1 item")
        fun `templates têm conteúdo válido`() {
            DefaultTemplates.all.forEach { template ->
                assertTrue(template.name.isNotBlank(), "Template sem nome: ${template.id}")
                assertTrue(template.items.isNotEmpty(), "Template sem items: ${template.name}")
                assertTrue(template.totalCalories > 0, "Template com 0 kcal: ${template.name}")
            }
        }
    }

    // ─── Helpers ─────────────────────────────────────────────
    private fun getDayType(date: LocalDate, profile: UserProfile): DayType =
        if (date.dayOfWeek in profile.doubleDays) DayType.DOUBLE else DayType.NORMAL

    private fun getTargets(date: LocalDate, profile: UserProfile): MacroTargets =
        if (getDayType(date, profile) == DayType.DOUBLE) profile.doubleDayMacros else profile.defaultMacros

    private fun calcConsumed(entries: List<FoodEntry>): ConsumedMacros = ConsumedMacros(
        caloriesKcal = entries.sumOf { it.caloriesKcal },
        proteinG = entries.sumOf { it.proteinG },
        carbsG = entries.sumOf { it.carbsG },
        fatG = entries.sumOf { it.fatG }
    )

    private fun makeFoodEntry(
        caloriesKcal: Int,
        proteinG: Double = 0.0,
        carbsG: Double = 0.0,
        fatG: Double = 0.0
    ) = FoodEntry(
        id = java.util.UUID.randomUUID().toString(),
        date = LocalDate.now(),
        mealSlot = MealSlot.LUNCH,
        name = "Test",
        caloriesKcal = caloriesKcal,
        proteinG = proteinG,
        carbsG = carbsG,
        fatG = fatG
    )
}
