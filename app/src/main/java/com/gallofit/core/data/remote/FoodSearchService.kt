package com.gallofit.core.data.remote

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
        coroutineScope {
            val offJob = async { try { searchOpenFoodFacts(query) } catch (_: Exception) { emptyList() } }
            val usdaJob = async { try { searchUSDA(query) } catch (_: Exception) { emptyList() } }
            (offJob.await() + usdaJob.await())
                .distinctBy { it.name.lowercase().take(20) }
                .take(20)
        }
    }

    private fun searchOpenFoodFacts(query: String): List<FoodSearchResult> {
        val encoded = URLEncoder.encode(query, "UTF-8")
        val url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=$encoded&search_simple=1&action=process&json=1&page_size=15&lc=pt,en&fields=product_name,brands,nutriments,serving_size,product_name_pt,product_name_en"
        val conn = URL(url).openConnection()
        conn.setRequestProperty("User-Agent", "GalloFit/3.0 (mgallo17@gmail.com)")
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        val response = conn.getInputStream().bufferedReader().readText()
        val json = JSONObject(response)
        val products = json.optJSONArray("products") ?: return emptyList()

        return (0 until products.length()).mapNotNull { i ->
            val p = products.getJSONObject(i)
            val name = (p.optString("product_name_pt").takeIf { it.isNotBlank() }
                ?: p.optString("product_name_en").takeIf { it.isNotBlank() }
                ?: p.optString("product_name")).trim()
            if (name.isBlank()) return@mapNotNull null
            val n = p.optJSONObject("nutriments") ?: return@mapNotNull null
            // Tentar kcal/100g ou energy/100g convertendo kJ
            val kcal = when {
                n.has("energy-kcal_100g") -> n.optDouble("energy-kcal_100g", -1.0)
                n.has("energy_100g") -> n.optDouble("energy_100g", -1.0) / 4.184
                else -> -1.0
            }
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
