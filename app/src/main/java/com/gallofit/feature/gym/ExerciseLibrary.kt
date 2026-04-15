package com.gallofit.feature.gym

data class Exercise(
    val id: String,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val instructions: String = ""
)

object ExerciseLibrary {
    val all = listOf(
        // ── Peito ──────────────────────────────────────────────────────────
        Exercise("chest_press_machine", "Supino na Máquina", "Peito", "Máquina"),
        Exercise("pec_deck", "Pec Deck (Voador)", "Peito", "Máquina"),
        Exercise("cable_crossover", "Crossover no Cabo", "Peito", "Cabo"),
        Exercise("bench_press", "Supino Reto (Barra)", "Peito", "Barra Livre"),
        Exercise("incline_press", "Supino Inclinado", "Peito", "Barra Livre"),
        Exercise("dumbbell_fly", "Crucifixo com Halteres", "Peito", "Haltere"),

        // ── Costas ─────────────────────────────────────────────────────────
        Exercise("lat_pulldown", "Puxada Alta (Pulley)", "Costas", "Máquina/Cabo"),
        Exercise("seated_row", "Remada Sentada (Cabo)", "Costas", "Cabo"),
        Exercise("low_row_machine", "Remada Baixa Máquina", "Costas", "Máquina"),
        Exercise("back_extension", "Extensão Lombar", "Costas", "Máquina"),
        Exercise("deadlift", "Levantamento Terra", "Costas", "Barra Livre"),
        Exercise("pull_up", "Barra Fixa", "Costas", "Peso Corporal"),

        // ── Ombros ─────────────────────────────────────────────────────────
        Exercise("shoulder_press_machine", "Desenvolvimento Máquina", "Ombros", "Máquina"),
        Exercise("lateral_raise_machine", "Elevação Lateral Máquina", "Ombros", "Máquina"),
        Exercise("lateral_raise_cable", "Elevação Lateral Cabo", "Ombros", "Cabo"),
        Exercise("front_raise", "Elevação Frontal", "Ombros", "Haltere"),
        Exercise("rear_delt_fly", "Crucifixo Invertido", "Ombros", "Máquina"),

        // ── Bíceps ─────────────────────────────────────────────────────────
        Exercise("bicep_curl_machine", "Rosca Scott Máquina", "Bíceps", "Máquina"),
        Exercise("bicep_curl_cable", "Rosca no Cabo", "Bíceps", "Cabo"),
        Exercise("bicep_curl_barbell", "Rosca Direta (Barra)", "Bíceps", "Barra Livre"),
        Exercise("bicep_curl_dumbbell", "Rosca Alternada", "Bíceps", "Haltere"),
        Exercise("hammer_curl", "Rosca Martelo", "Bíceps", "Haltere"),

        // ── Tríceps ────────────────────────────────────────────────────────
        Exercise("tricep_pushdown", "Tríceps Pulley (Corda)", "Tríceps", "Cabo"),
        Exercise("tricep_overhead", "Tríceps Francês Cabo", "Tríceps", "Cabo"),
        Exercise("tricep_machine", "Tríceps Máquina", "Tríceps", "Máquina"),
        Exercise("skull_crusher", "Skull Crusher", "Tríceps", "Barra Livre"),
        Exercise("dips", "Mergulho (Paralelas)", "Tríceps", "Peso Corporal"),

        // ── Pernas ─────────────────────────────────────────────────────────
        Exercise("leg_press", "Leg Press 45°", "Pernas", "Máquina"),
        Exercise("leg_extension", "Cadeira Extensora", "Pernas", "Máquina"),
        Exercise("leg_curl", "Cadeira Flexora", "Pernas", "Máquina"),
        Exercise("calf_raise_machine", "Panturrilha Máquina", "Pernas", "Máquina"),
        Exercise("squat", "Agachamento Livre", "Pernas", "Barra Livre"),
        Exercise("hack_squat", "Hack Squat Máquina", "Pernas", "Máquina"),
        Exercise("hip_abduction", "Abdução de Quadril", "Pernas", "Máquina"),
        Exercise("hip_adduction", "Adução de Quadril", "Pernas", "Máquina"),
        Exercise("glute_kickback", "Glúteo Máquina", "Pernas", "Máquina"),

        // ── Abdômen ────────────────────────────────────────────────────────
        Exercise("crunch_machine", "Abdominal Máquina", "Abdômen", "Máquina"),
        Exercise("cable_crunch", "Abdominal no Cabo", "Abdômen", "Cabo"),
        Exercise("plank", "Prancha", "Abdômen", "Peso Corporal"),
    )

    val byMuscleGroup: Map<String, List<Exercise>> = all.groupBy { it.muscleGroup }
    val muscleGroups = byMuscleGroup.keys.sorted()
}
