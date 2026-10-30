package com.example.recipe_helper.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isCustom = 1 ORDER BY id DESC")
    fun getCustomRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY id DESC")
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: String): RecipeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<RecipeEntity>)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipeById(id: String)

    @Query("SELECT * FROM logged_meals ORDER BY timestamp DESC")
    fun getAllLoggedMeals(): Flow<List<LoggedMealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoggedMeal(meal: LoggedMealEntity)

    @Query("DELETE FROM logged_meals WHERE id = :id")
    suspend fun deleteLoggedMealById(id: Long)

    // Grocery Items Queries
    @Query("SELECT * FROM grocery_items ORDER BY isChecked ASC, id DESC")
    fun getAllGroceryItems(): Flow<List<GroceryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroceryItem(item: GroceryItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroceryItems(items: List<GroceryItemEntity>)

    @Query("UPDATE grocery_items SET isChecked = NOT isChecked WHERE id = :id")
    suspend fun toggleGroceryItem(id: Long)

    @Query("DELETE FROM grocery_items WHERE id = :id")
    suspend fun deleteGroceryItemById(id: Long)

    @Query("DELETE FROM grocery_items WHERE isChecked = 1")
    suspend fun clearCheckedGroceryItems()

    // Meal Plan Queries
    @Query("SELECT * FROM meal_plan")
    fun getAllMealPlans(): Flow<List<MealPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(plan: MealPlanEntity)

    @Query("DELETE FROM meal_plan WHERE dayOfWeek = :dayOfWeek AND mealType = :mealType")
    suspend fun deleteMealPlan(dayOfWeek: String, mealType: String)
}
