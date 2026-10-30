package com.example.recipe_helper.domain.model

enum class RecipeSource {
    LOCAL,
    REMOTE,
    AI
}

data class Recipe(
    val id: String,
    val title: String,
    val cuisine: String = "General",
    val difficulty: String = "Easy",
    val imageUrl: String = "",
    val prepTimeMinutes: Int = 15,
    val cookTimeMinutes: Int = 30,
    val servings: Int = 4,
    val calories: Int = 350,
    val proteinGrams: Int = 25,
    val ingredients: List<Ingredient> = emptyList(),
    val instructions: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false,
    val source: RecipeSource = RecipeSource.LOCAL,
    val aiTips: String? = null
)

data class Ingredient(
    val name: String,
    val amount: String = ""
)
