package com.gallofit.core.domain.model

import java.util.UUID

/**
 * Templates pré-carregados para Matheus — sem ter que introduzir nada.
 */
object DefaultTemplates {

    val WHEY_PADRAO = MealTemplate(
        id = "whey_padrao",
        name = "Whey Padrão",
        defaultSlot = MealSlot.POST_WORKOUT,
        isFavorite = true,
        items = listOf(
            TemplateItem("Leite desnatado", caloriesKcal = 62, proteinG = 6.0, carbsG = 9.0, fatG = 0.3, quantity = 200.0, unit = "ml"),
            TemplateItem("Amfit Vanilla (1 scoop 30g)", caloriesKcal = 118, proteinG = 24.0, carbsG = 3.0, fatG = 1.5, quantity = 30.0, unit = "g"),
            TemplateItem("Banana média", caloriesKcal = 105, proteinG = 1.3, carbsG = 27.0, fatG = 0.3, quantity = 1.0, unit = "unid"),
            TemplateItem("Creatina (1 scoop 5g)", caloriesKcal = 0, proteinG = 0.0, carbsG = 0.0, fatG = 0.0, quantity = 5.0, unit = "g"),
        )
    )

    val IOGURTE_MORANGOS = MealTemplate(
        id = "iogurte_morangos",
        name = "Iogurte + Morangos",
        defaultSlot = MealSlot.BREAKFAST,
        isFavorite = true,
        items = listOf(
            TemplateItem("Iogurte Natural Eliges", caloriesKcal = 62, proteinG = 5.5, carbsG = 5.5, fatG = 2.0, quantity = 125.0, unit = "g"),
            TemplateItem("Morangos (6 unid)", caloriesKcal = 36, proteinG = 0.7, carbsG = 8.5, fatG = 0.3, quantity = 6.0, unit = "unid"),
        )
    )

    val CARNE_BATATA_OVOS = MealTemplate(
        id = "carne_batata_ovos",
        name = "Carne + Batata + Ovos",
        defaultSlot = MealSlot.LUNCH,
        isFavorite = true,
        items = listOf(
            TemplateItem("Carne moída mista (125g)", caloriesKcal = 262, proteinG = 26.0, carbsG = 0.0, fatG = 17.0, quantity = 125.0, unit = "g"),
            TemplateItem("Batata branca Airfryer (½)", caloriesKcal = 87, proteinG = 2.0, carbsG = 20.0, fatG = 0.1, quantity = 100.0, unit = "g"),
            TemplateItem("Batata doce Airfryer (¼)", caloriesKcal = 45, proteinG = 1.0, carbsG = 10.5, fatG = 0.1, quantity = 60.0, unit = "g"),
            TemplateItem("Ovo frito sem óleo (1)", caloriesKcal = 78, proteinG = 6.0, carbsG = 0.0, fatG = 5.5, quantity = 1.0, unit = "unid"),
        )
    )

    val EMPANADA_MEIA = MealTemplate(
        id = "empanada_meia",
        name = "½ Empanada Ipanema (carne)",
        defaultSlot = MealSlot.AFTERNOON_SNACK,
        isFavorite = false,
        items = listOf(
            TemplateItem("½ Empanada de carne Ipanema", caloriesKcal = 375, proteinG = 14.0, carbsG = 32.0, fatG = 20.0, quantity = 0.5, unit = "unid"),
        )
    )

    val all = listOf(WHEY_PADRAO, IOGURTE_MORANGOS, CARNE_BATATA_OVOS, EMPANADA_MEIA)
}
