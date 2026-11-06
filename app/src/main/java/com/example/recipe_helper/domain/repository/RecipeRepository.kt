package com.example.recipe_helper.domain.repository

import com.example.recipe_helper.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun getAllRecipes(): Flow<List<Recipe>>
    fun getCustomRecipes(): Flow<List<Recipe>>
    fun getFavoriteRecipes(): Flow<List<Recipe>>
    suspend fun getRecipeById(id: String): Recipe?
    suspend fun addRecipe(recipe: Recipe)
    suspend fun updateRecipe(recipe: Recipe)
    suspend fun toggleFavorite(recipe: Recipe)
    suspend fun deleteRecipe(id: String)
    suspend fun searchRemoteMeals(query: String): List<Recipe>
    suspend fun generateAiRecipe(ingredients: List<String>): Recipe
    suspend fun syncRemoteRecipesToLocal()
    fun getAllLoggedMeals(): Flow<List<com.example.recipe_helper.data.local.LoggedMealEntity>>
    suspend fun logMeal(title: String, calories: Int, proteinGrams: Int, recipeId: String = "")
    suspend fun deleteLoggedMeal(id: Long)

    // Grocery List
    fun getAllGroceryItems(): Flow<List<com.example.recipe_helper.data.local.GroceryItemEntity>>
    suspend fun addGroceryItemsFromRecipe(recipe: Recipe)
    suspend fun addCustomGroceryItem(name: String, amount: String = "", category: String = "Pantry")
    suspend fun toggleGroceryItem(id: Long)
    suspend fun deleteGroceryItem(id: Long)
    suspend fun clearCheckedGroceryItems()

    // Meal Plan
    fun getAllMealPlans(): Flow<List<com.example.recipe_helper.data.local.MealPlanEntity>>
    suspend fun setMealPlanSlot(dayOfWeek: String, mealType: String, recipe: Recipe)
    suspend fun removeMealPlanSlot(dayOfWeek: String, mealType: String)
    suspend fun addWeeklyMealPlanToGroceryList()
}
