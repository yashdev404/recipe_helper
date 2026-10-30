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
    data class Error(val message: String) : AddRecipeUiState
}

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddRecipeUiState>(AddRecipeUiState.Idle)
    val uiState: StateFlow<AddRecipeUiState> = _uiState.asStateFlow()

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
            val newRecipe = Recipe(
                id = System.currentTimeMillis().toString(),
                title = title.trim(),
                cuisine = cuisine.ifBlank { "General" },
                difficulty = difficulty,
                imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=600&auto=format&fit=crop" },
                prepTimeMinutes = prepTime.toIntOrNull() ?: 15,
                cookTimeMinutes = cookTime.toIntOrNull() ?: 30,
                servings = servings.toIntOrNull() ?: 4,
                calories = calories.toIntOrNull() ?: 350,
                ingredients = ingredientsList.filter { it.isNotBlank() }.map { Ingredient(name = it.trim()) },
                instructions = listOf("Prepare ingredients", "Cook according to recipe steps", "Serve fresh and warm"),
                isCustom = true,
                source = RecipeSource.LOCAL
            )
            repository.addRecipe(newRecipe)
            _uiState.value = AddRecipeUiState.Success
        }
    }

    fun resetState() {
        _uiState.value = AddRecipeUiState.Idle
    }
}
