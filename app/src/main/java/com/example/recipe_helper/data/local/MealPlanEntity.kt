package com.example.recipe_helper.data.local

import androidx.room.Entity

@Entity(tableName = "meal_plan", primaryKeys = ["dayOfWeek", "mealType"])
data class MealPlanEntity(
    val dayOfWeek: String, // "Monday", "Tuesday", etc.
    val mealType: String,  // "Breakfast", "Lunch", "Dinner"
    val recipeId: String,
    val recipeTitle: String,
    val recipeImageUrl: String,
    val calories: Int,
    val proteinGrams: Int
)
