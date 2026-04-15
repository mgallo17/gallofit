package com.gallofit.core.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

data class FoodSearchResult(
    val name: String,
    val brand: String = "",
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val servingSize: String = "100g",
    val source: String = ""
)

object FoodSearchService {

    suspend fun search(query: String): List<FoodSearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<FoodSearchResult>()
        try {
            results.addAll(searchOpenFoodFacts(query))
        } catch (_: Exception) {}
        if (results.size < 5) {
            try {
                results.addAll(searchUSDA(query))
            } catch (_: Exception) {}
        }
        results.take(20)
    }

    private fun searchOpenFoodFacts(query: String): List<FoodSearchResult> {
        val encoded = URLEncoder.encode(query, "UTF-8")
        val url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=$encoded&search_simple=1&action=process&json=1&page_size=10&fields=product_name,brands,nutriments,serving_size"
        val response = URL(url).readText()
        val json = JSONObject(response)
        val products = json.optJSONArray("products") ?: return emptyList()

        return (0 until products.length()).mapNotNull { i ->
            val p = products.getJSONObject(i)
            val name = p.optString("product_name").trim()
            if (name.isBlank()) return@mapNotNull null
            val n = p.optJSONObject("nutriments") ?: return@mapNotNull null
            val kcal = n.optDouble("energy-kcal_100g", -1.0)
            if (kcal < 0) return@mapNotNull null
            FoodSearchResult(
                name = name,
                brand = p.optString("brands").split(",").firstOrNull()?.trim() ?: "",
                calories = kcal.toInt(),
                protein = n.optDouble("proteins_100g", 0.0),
                carbs = n.optDouble("carbohydrates_100g", 0.0),
                fat = n.optDouble("fat_100g", 0.0),
                servingSize = p.optString("serving_size", "100g"),
                source = "Open Food Facts"
            )
        }
    }

    private fun searchUSDA(query: String): List<FoodSearchResult> {
        val encoded = URLEncoder.encode(query, "UTF-8")
        val url = "https://api.nal.usda.gov/fdc/v1/foods/search?query=$encoded&pageSize=10&api_key=DEMO_KEY"
        val response = URL(url).readText()
        val json = JSONObject(response)
        val foods = json.optJSONArray("foods") ?: return emptyList()

        return (0 until foods.length()).mapNotNull { i ->
            val f = foods.getJSONObject(i)
            val name = f.optString("description").trim()
            if (name.isBlank()) return@mapNotNull null
            val nutrients = f.optJSONArray("foodNutrients") ?: return@mapNotNull null
            var kcal = 0.0; var prot = 0.0; var carbs = 0.0; var fat = 0.0
            for (j in 0 until nutrients.length()) {
                val n = nutrients.getJSONObject(j)
                when (n.optInt("nutrientId")) {
                    1008 -> kcal = n.optDouble("value", 0.0)
                    1003 -> prot = n.optDouble("value", 0.0)
                    1005 -> carbs = n.optDouble("value", 0.0)
                    1004 -> fat = n.optDouble("value", 0.0)
                }
            }
            if (kcal <= 0) return@mapNotNull null
            FoodSearchResult(
                name = name,
                brand = f.optString("brandOwner", ""),
                calories = kcal.toInt(),
                protein = prot,
                carbs = carbs,
                fat = fat,
                source = "USDA"
            )
        }
    }
}
