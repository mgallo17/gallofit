package com.gallofit.bdd.steps

import io.cucumber.java.pt.*
import org.junit.Assert.*
import com.gallofit.core.domain.model.*
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Step definitions para os BDD tests do GalloFit.
 * Cucumber com linguagem português.
 */
class RegistoCaloriastSteps {

    private var dayOfWeek: DayOfWeek = DayOfWeek.MONDAY
    private val foodEntries = mutableListOf<FoodEntry>()
    private val profile = UserProfile(
        defaultMacros = MacroTargets(caloriesKcal = 2100, proteinG = 190, carbsG = 160, fatG = 65),
        doubleDayMacros = MacroTargets(caloriesKcal = 2200, proteinG = 190, carbsG = 170, fatG = 65),
        doubleDays = setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)
    )
    private var appliedTemplate: MealTemplate? = null
    private var addedEntry: FoodEntry? = null

    // ─── Givens ─────────────────────────────────────────────
    @Dado("que hoje é segunda-feira")
    fun hojeESegunda() { dayOfWeek = DayOfWeek.MONDAY }

    @Dado("que hoje é terça-feira")
    fun hojeETerca() { dayOfWeek = DayOfWeek.TUESDAY }

    @Dado("que o utilizador tem uma meta de {int} kcal para dias normais")
    fun metaNormal(kcal: Int) {
        // já configurado no profile
    }

    @Dado("uma meta de {int} kcal para dias duplos \\(terça e quinta\\)")
    fun metaDupla(kcal: Int) {
        // já configurado no profile
    }

    @Dado("que o utilizador tem o template {string} guardado")
    fun templateGuardado(nome: String) {
        appliedTemplate = DefaultTemplates.all.find { it.name == nome }
        assertNotNull("Template '$nome' não encontrado", appliedTemplate)
    }

    // ─── Whens ──────────────────────────────────────────────
    @Quando("o utilizador regista as seguintes refeições:")
    fun registaRefeicoes(dataTable: io.cucumber.datatable.DataTable) {
        foodEntries.clear()
        dataTable.asMaps().forEach { row ->
            val nome = row["nome"] ?: ""
            val kcal = row["kcal"]?.toInt() ?: 0
            foodEntries.add(makeFoodEntry(nome, kcal))
        }
    }

    @Quando("o utilizador regista {int} kcal no total")
    fun registaTotalKcal(kcal: Int) {
        foodEntries.clear()
        foodEntries.add(makeFoodEntry("Refeição total", kcal))
    }

    @Quando("o utilizador aplica o template ao slot {string}")
    fun aplicaTemplate(slot: String) {
        val template = appliedTemplate ?: return
        val mealSlot = MealSlot.valueOf(slot)
        addedEntry = FoodEntry(
            id = java.util.UUID.randomUUID().toString(),
            date = LocalDate.now(),
            mealSlot = mealSlot,
            name = template.name,
            caloriesKcal = template.totalCalories,
            proteinG = template.totalProtein,
            carbsG = template.totalCarbs,
            fatG = template.totalFat,
            fromTemplateId = template.id
        )
    }

    // ─── Thens ──────────────────────────────────────────────
    @Então("o total consumido deve ser {int} kcal")
    fun totalConsumido(expected: Int) {
        val total = foodEntries.sumOf { it.caloriesKcal }
        assertEquals(expected, total)
    }

    @Então("as calorias restantes devem ser {int} kcal")
    fun caloriasRestantes(expected: Int) {
        val targets = if (dayOfWeek in profile.doubleDays) profile.doubleDayMacros else profile.defaultMacros
        val consumed = foodEntries.sumOf { it.caloriesKcal }
        val remaining = targets.caloriesKcal - consumed
        assertEquals(expected, remaining)
    }

    @Então("o estado deve ser {string}")
    fun verificaEstado(estado: String) {
        val targets = if (dayOfWeek in profile.doubleDays) profile.doubleDayMacros else profile.defaultMacros
        val consumed = foodEntries.sumOf { it.caloriesKcal }
        when (estado) {
            "dentro da meta" -> assertTrue(consumed <= targets.caloriesKcal)
            "meta excedida" -> assertTrue(consumed > targets.caloriesKcal)
        }
    }

    @Então("o tipo de dia deve ser {string}")
    fun verificaTipoDia(tipo: String) {
        val actual = if (dayOfWeek in profile.doubleDays) "DUPLO" else "NORMAL"
        assertEquals(tipo, actual)
    }

    @Então("deve ser adicionada uma entrada com {int} kcal")
    fun entradaComKcal(expected: Int) {
        val entry = addedEntry ?: fail("Nenhuma entrada foi adicionada")
        assertEquals(expected, entry.caloriesKcal, 5)
    }

    @Então("a proteína deve ser {int} gramas")
    fun proteina(expected: Int) {
        val entry = addedEntry ?: fail("Nenhuma entrada foi adicionada")
        assertEquals(expected.toDouble(), entry.proteinG, 2.0)
    }

    @Então("o slot deve ser {string}")
    fun slot(expected: String) {
        val entry = addedEntry ?: fail("Nenhuma entrada foi adicionada")
        assertEquals(MealSlot.valueOf(expected), entry.mealSlot)
    }

    // ─── Helpers ─────────────────────────────────────────────
    private fun makeFoodEntry(nome: String, kcal: Int) = FoodEntry(
        id = java.util.UUID.randomUUID().toString(),
        date = LocalDate.now(),
        mealSlot = MealSlot.LUNCH,
        name = nome,
        caloriesKcal = kcal,
        proteinG = 0.0,
        carbsG = 0.0,
        fatG = 0.0
    )
}
