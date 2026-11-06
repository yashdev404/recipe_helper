package com.example.recipe_helper.presentation.my_recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_helper.domain.model.Ingredient
import com.example.recipe_helper.domain.model.Recipe
import com.example.recipe_helper.domain.model.RecipeSource
import com.example.recipe_helper.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AddRecipeUiState {
    object Idle : AddRecipeUiState
    object Saving : AddRecipeUiState
    object Success : AddRecipeUiState
    data class LoadedForEdit(val recipe: Recipe) : AddRecipeUiState
    data class Error(val message: String) : AddRecipeUiState
}

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddRecipeUiState>(AddRecipeUiState.Idle)
    val uiState: StateFlow<AddRecipeUiState> = _uiState.asStateFlow()

    private var existingRecipe: Recipe? = null

    fun loadRecipeForEditing(recipeId: String) {
        viewModelScope.launch {
            val recipe = repository.getRecipeById(recipeId)
            if (recipe != null) {
                existingRecipe = recipe
                _uiState.value = AddRecipeUiState.LoadedForEdit(recipe)
            }
        }
    }

    fun saveRecipe(
        title: String,
        cuisine: String,
        difficulty: String,
        imageUrl: String,
        prepTime: String,
        cookTime: String,
        servings: String,
        calories: String,
        ingredientsList: List<String>
    ) {
        if (title.isBlank()) {
            _uiState.value = AddRecipeUiState.Error("Title cannot be empty.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddRecipeUiState.Saving

            val recipeId = existingRecipe?.id ?: System.currentTimeMillis().toString()
            val isFavorite = existingRecipe?.isFavorite ?: false
            val instructions = existingRecipe?.instructions?.ifEmpty {
                listOf("Prepare ingredients", "Cook according to recipe steps", "Serve fresh and warm")
            } ?: listOf("Prepare ingredients", "Cook according to recipe steps", "Serve fresh and warm")

            val updatedRecipe = Recipe(
                id = recipeId,
                title = title.trim(),
                cuisine = cuisine.ifBlank { "General" },
                difficulty = difficulty,
                imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=600&auto=format&fit=crop" },
                prepTimeMinutes = prepTime.toIntOrNull() ?: 15,
                cookTimeMinutes = cookTime.toIntOrNull() ?: 30,
                servings = servings.toIntOrNull() ?: 4,
                calories = calories.toIntOrNull() ?: 350,
                ingredients = ingredientsList.filter { it.isNotBlank() }.map { Ingredient(name = it.trim()) },
                instructions = instructions,
                isFavorite = isFavorite,
                isCustom = true,
                source = existingRecipe?.source ?: RecipeSource.LOCAL,
                aiTips = existingRecipe?.aiTips
            )

            if (existingRecipe != null) {
                repository.updateRecipe(updatedRecipe)
            } else {
                repository.addRecipe(updatedRecipe)
            }

            _uiState.value = AddRecipeUiState.Success
        }
    }

    fun resetState() {
        _uiState.value = AddRecipeUiState.Idle
        existingRecipe = null
    }
}
