package com.example.recipe_helper.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.recipe_helper.domain.model.Ingredient
import com.example.recipe_helper.domain.model.Recipe
import com.example.recipe_helper.domain.model.RecipeSource

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val cuisine: String,
    val difficulty: String,
    val imageUrl: String,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servings: Int,
    val calories: Int,
    val proteinGrams: Int = 25,
    val ingredients: List<Ingredient>,
    val instructions: List<String>,
    val isFavorite: Boolean,
    val isCustom: Boolean,
    val source: String = "LOCAL",
    val aiTips: String? = null
) {
    fun toRecipe(): Recipe = Recipe(
        id = id,
        title = title,
        cuisine = cuisine,
        difficulty = difficulty,
        imageUrl = imageUrl,
        prepTimeMinutes = prepTimeMinutes,
        cookTimeMinutes = cookTimeMinutes,
        servings = servings,
        calories = calories,
        proteinGrams = proteinGrams,
        ingredients = ingredients,
        instructions = instructions,
        isFavorite = isFavorite,
        isCustom = isCustom,
        source = runCatching { RecipeSource.valueOf(source) }.getOrDefault(RecipeSource.LOCAL),
        aiTips = aiTips
    )

    companion object {
        fun fromRecipe(recipe: Recipe): RecipeEntity = RecipeEntity(
            id = recipe.id,
            title = recipe.title,
            cuisine = recipe.cuisine,
            difficulty = recipe.difficulty,
            imageUrl = recipe.imageUrl,
            prepTimeMinutes = recipe.prepTimeMinutes,
            cookTimeMinutes = recipe.cookTimeMinutes,
            servings = recipe.servings,
            calories = recipe.calories,
            proteinGrams = recipe.proteinGrams,
            ingredients = recipe.ingredients,
            instructions = recipe.instructions,
            isFavorite = recipe.isFavorite,
            isCustom = recipe.isCustom,
            source = recipe.source.name,
            aiTips = recipe.aiTips
        )
    }
}
