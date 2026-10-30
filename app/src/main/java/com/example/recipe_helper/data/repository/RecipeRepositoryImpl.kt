package com.example.recipe_helper.data.repository

import com.example.recipe_helper.data.local.RecipeDao
import com.example.recipe_helper.data.local.RecipeEntity
import com.example.recipe_helper.data.remote.MealApiService
import com.example.recipe_helper.data.remote.dto.MealDto
import com.example.recipe_helper.domain.model.Ingredient
import com.example.recipe_helper.domain.model.Recipe
import com.example.recipe_helper.domain.model.RecipeSource
import com.example.recipe_helper.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val dao: RecipeDao,
    private val mealApiService: MealApiService
) : RecipeRepository {

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return dao.getAllRecipes().map { entities ->
            entities.map { it.toRecipe() }
        }
    }

    override fun getCustomRecipes(): Flow<List<Recipe>> {
        return dao.getCustomRecipes().map { entities ->
            entities.map { it.toRecipe() }
        }
    }

    override fun getFavoriteRecipes(): Flow<List<Recipe>> {
        return dao.getFavoriteRecipes().map { entities ->
            entities.map { it.toRecipe() }
        }
    }

    override suspend fun getRecipeById(id: String): Recipe? {
        val local = dao.getRecipeById(id)?.toRecipe()
        if (local != null) return local

        // Try remote fetch if not found locally
        return runCatching {
            val response = mealApiService.getMealById(id)
            val dto = response.meals?.firstOrNull()
            dto?.toDomainRecipe()
        }.getOrNull()
    }

    override suspend fun addRecipe(recipe: Recipe) {
        dao.insertRecipe(RecipeEntity.fromRecipe(recipe))
    }

    override suspend fun toggleFavorite(recipe: Recipe) {
        val updated = recipe.copy(isFavorite = !recipe.isFavorite)
        dao.insertRecipe(RecipeEntity.fromRecipe(updated))
    }

    override suspend fun deleteRecipe(id: String) {
        dao.deleteRecipeById(id)
    }

    override fun getAllLoggedMeals(): Flow<List<com.example.recipe_helper.data.local.LoggedMealEntity>> {
        return dao.getAllLoggedMeals()
    }

    override suspend fun logMeal(title: String, calories: Int, proteinGrams: Int, recipeId: String) {
        dao.insertLoggedMeal(
            com.example.recipe_helper.data.local.LoggedMealEntity(
                recipeId = recipeId,
                title = title,
                calories = calories,
                proteinGrams = proteinGrams,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun deleteLoggedMeal(id: Long) {
        dao.deleteLoggedMealById(id)
    }

    override fun getAllGroceryItems(): Flow<List<com.example.recipe_helper.data.local.GroceryItemEntity>> {
        return dao.getAllGroceryItems()
    }

    override suspend fun addGroceryItemsFromRecipe(recipe: Recipe) {
        val items = recipe.ingredients.map { ing ->
            val cat = when {
                ing.name.contains("chicken", ignoreCase = true) || ing.name.contains("beef", ignoreCase = true) || ing.name.contains("pancetta", ignoreCase = true) -> "Meat & Seafood"
                ing.name.contains("milk", ignoreCase = true) || ing.name.contains("cheese", ignoreCase = true) || ing.name.contains("egg", ignoreCase = true) -> "Dairy & Eggs"
                ing.name.contains("tomato", ignoreCase = true) || ing.name.contains("avocado", ignoreCase = true) || ing.name.contains("salad", ignoreCase = true) -> "Produce"
                else -> "Pantry & Spices"
            }
            com.example.recipe_helper.data.local.GroceryItemEntity(
                name = ing.name,
                amount = ing.amount,
                category = cat,
                recipeTitle = recipe.title
            )
        }
        dao.insertGroceryItems(items)
    }

    override suspend fun addCustomGroceryItem(name: String, amount: String, category: String) {
        if (name.isBlank()) return
        dao.insertGroceryItem(
            com.example.recipe_helper.data.local.GroceryItemEntity(
                name = name.trim(),
                amount = amount.trim(),
                category = category.ifBlank { "Pantry & Spices" }
            )
        )
    }

    override suspend fun toggleGroceryItem(id: Long) {
        dao.toggleGroceryItem(id)
    }

    override suspend fun deleteGroceryItem(id: Long) {
        dao.deleteGroceryItemById(id)
    }

    override suspend fun clearCheckedGroceryItems() {
        dao.clearCheckedGroceryItems()
    }

    override fun getAllMealPlans(): Flow<List<com.example.recipe_helper.data.local.MealPlanEntity>> {
        return dao.getAllMealPlans()
    }

    override suspend fun setMealPlanSlot(dayOfWeek: String, mealType: String, recipe: Recipe) {
        dao.insertMealPlan(
            com.example.recipe_helper.data.local.MealPlanEntity(
                dayOfWeek = dayOfWeek,
                mealType = mealType,
                recipeId = recipe.id,
                recipeTitle = recipe.title,
                recipeImageUrl = recipe.imageUrl,
                calories = recipe.calories,
                proteinGrams = recipe.proteinGrams
            )
        )
    }

    override suspend fun removeMealPlanSlot(dayOfWeek: String, mealType: String) {
        dao.deleteMealPlan(dayOfWeek, mealType)
    }

    override suspend fun addWeeklyMealPlanToGroceryList() {
        val plans = dao.getAllMealPlans().firstOrNull() ?: emptyList()
        for (plan in plans) {
            val recipe = getRecipeById(plan.recipeId)
            if (recipe != null) {
                addGroceryItemsFromRecipe(recipe)
            }
        }
    }

    override suspend fun searchRemoteMeals(query: String): List<Recipe> {
        return runCatching {
            val response = mealApiService.searchMeals(query)
            response.meals?.map { it.toDomainRecipe() } ?: emptyList()
        }.getOrDefault(emptyList())
    }

    override suspend fun generateAiRecipe(ingredients: List<String>): Recipe {
        val ingredientNames = ingredients.filter { it.isNotBlank() }
            .joinToString(", ") { it.trim().replaceFirstChar { char -> char.uppercase() } }
            .ifBlank { "Fresh Pantry Mix" }

        val mainIngredient = ingredients.firstOrNull { it.isNotBlank() }
            ?.trim()?.replaceFirstChar { it.uppercase() } ?: "Special"

        val generatedRecipe = Recipe(
            id = "ai_${System.currentTimeMillis()}",
            title = "Chef AI's $mainIngredient Creation",
            cuisine = "Fusion",
            difficulty = if (ingredients.size > 4) "Medium" else "Easy",
            imageUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600&auto=format&fit=crop",
            prepTimeMinutes = 10 + (ingredients.size * 2),
            cookTimeMinutes = 15 + (ingredients.size * 3),
            servings = 2,
            calories = 320 + (ingredients.size * 35),
            ingredients = ingredients.filter { it.isNotBlank() }.map { Ingredient(it.trim(), "as needed") } +
                    listOf(Ingredient("Olive Oil", "1 tbsp"), Ingredient("Salt & Pepper", "to taste")),
            instructions = listOf(
                "Prep all selected ingredients ($ingredientNames) by washing and finely chopping.",
                "Heat 1 tbsp olive oil in a non-stick pan over medium heat.",
                "Sauté the main ingredients until fragrant and golden tender.",
                "Season with salt, black pepper, and your favorite aromatic herbs.",
                "Garnish with fresh greens, serve warm, and enjoy your AI-suggested dish!"
            ),
            isCustom = true,
            source = RecipeSource.AI,
            aiTips = "Chef's Tip: Adjust seasoning after cooking. For extra flavor, splash lemon juice or grated parmesan before serving."
        )

        // Save AI recipe directly to local database
        dao.insertRecipe(RecipeEntity.fromRecipe(generatedRecipe))
        return generatedRecipe
    }

    override suspend fun syncRemoteRecipesToLocal() {
        val existing = dao.getAllRecipes().firstOrNull() ?: emptyList()
        if (existing.isNotEmpty()) return

        // Populate seed remote & local recipes
        val seedQueries = listOf("chicken", "pasta", "salad")
        val fetchedRecipes = mutableListOf<Recipe>()

        for (q in seedQueries) {
            val remoteResults = searchRemoteMeals(q)
            fetchedRecipes.addAll(remoteResults.take(2))
        }

        val uniqueFetched = fetchedRecipes.distinctBy { it.id }

        if (uniqueFetched.isNotEmpty()) {
            dao.insertAll(uniqueFetched.map { RecipeEntity.fromRecipe(it) })
        } else {
            populateFallbackData()
        }
    }

    private suspend fun populateFallbackData() {
        val sampleRecipes = listOf(
            Recipe(
                id = "52772",
                title = "Classic Spaghetti Carbonara",
                cuisine = "Italian",
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1612874742237-6526221588e3?w=600&auto=format&fit=crop",
                prepTimeMinutes = 15,
                cookTimeMinutes = 20,
                servings = 4,
                calories = 450,
                ingredients = listOf(
                    Ingredient("Spaghetti", "400g"),
                    Ingredient("Eggs", "4 large"),
                    Ingredient("Pancetta or Bacon", "150g"),
                    Ingredient("Parmesan Cheese", "100g")
                ),
                instructions = listOf(
                    "Boil spaghetti in salted water until al dente.",
                    "Crisp the pancetta in a large skillet over medium heat.",
                    "Whisk eggs and parmesan together in a bowl.",
                    "Toss hot pasta into skillet with pancetta, remove from heat.",
                    "Quickly stir in egg mixture to create a creamy sauce."
                ),
                source = RecipeSource.REMOTE
            ),
            Recipe(
                id = "52855",
                title = "Avocado Toast with Egg",
                cuisine = "American",
                difficulty = "Easy",
                imageUrl = "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=600&auto=format&fit=crop",
                prepTimeMinutes = 10,
                cookTimeMinutes = 5,
                servings = 2,
                calories = 280,
                ingredients = listOf(
                    Ingredient("Sourdough Bread", "2 slices"),
                    Ingredient("Ripe Avocado", "1"),
                    Ingredient("Eggs", "2")
                ),
                instructions = listOf(
                    "Toast the sourdough bread slices.",
                    "Mash avocado with salt and pepper.",
                    "Fry or poach eggs to desired doneness.",
                    "Spread avocado on toast, top with egg."
                ),
                source = RecipeSource.REMOTE
            )
        )
        dao.insertAll(sampleRecipes.map { RecipeEntity.fromRecipe(it) })
    }

    private fun MealDto.toDomainRecipe(): Recipe {
        val ingredientList = mutableListOf<Ingredient>()
        val rawIngredients = listOf(
            strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
            strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10
        )
        val rawMeasures = listOf(
            strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
            strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10
        )

        for (i in rawIngredients.indices) {
            val name = rawIngredients.getOrNull(i)?.trim()
            val measure = rawMeasures.getOrNull(i)?.trim() ?: ""
            if (!name.isNullOrBlank()) {
                ingredientList.add(Ingredient(name = name, amount = measure))
            }
        }

        val parsedInstructions = strInstructions?.split("\r\n", "\n")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?: emptyList()

        return Recipe(
            id = idMeal,
            title = strMeal,
            cuisine = strArea ?: strCategory ?: "International",
            difficulty = "Medium",
            imageUrl = strMealThumb ?: "",
            prepTimeMinutes = 15,
            cookTimeMinutes = 25,
            servings = 4,
            calories = 400,
            ingredients = ingredientList,
            instructions = parsedInstructions,
            source = RecipeSource.REMOTE
        )
    }
}
