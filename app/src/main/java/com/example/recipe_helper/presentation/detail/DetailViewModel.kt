package com.example.recipe_helper.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_helper.domain.model.Recipe
import com.example.recipe_helper.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(
        val recipe: Recipe,
        val completedSteps: Set<Int> = emptySet()
    ) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            val recipe = repository.getRecipeById(recipeId)
            if (recipe != null) {
                _uiState.value = DetailUiState.Success(recipe)
            } else {
                _uiState.value = DetailUiState.Error("Recipe not found.")
            }
        }
    }

    fun toggleStep(stepIndex: Int) {
        val currentState = _uiState.value
        if (currentState is DetailUiState.Success) {
            val updatedSteps = currentState.completedSteps.toMutableSet()
            if (updatedSteps.contains(stepIndex)) {
                updatedSteps.remove(stepIndex)
            } else {
                updatedSteps.add(stepIndex)
            }
            _uiState.value = currentState.copy(completedSteps = updatedSteps)
        }
    }

    fun toggleFavorite() {
        val currentState = _uiState.value
        if (currentState is DetailUiState.Success) {
            val updatedRecipe = currentState.recipe.copy(isFavorite = !currentState.recipe.isFavorite)
            _uiState.value = currentState.copy(recipe = updatedRecipe)
            viewModelScope.launch {
                repository.toggleFavorite(currentState.recipe)
            }
        }
    }

    fun logMealAsEaten(onLogged: () -> Unit = {}) {
        val currentState = _uiState.value
        if (currentState is DetailUiState.Success) {
            val recipe = currentState.recipe
            viewModelScope.launch {
                repository.logMeal(
                    title = recipe.title,
                    calories = recipe.calories,
                    proteinGrams = recipe.proteinGrams,
                    recipeId = recipe.id
                )
                onLogged()
            }
        }
    }

    fun addIngredientsToGroceryList(onAdded: () -> Unit = {}) {
        val currentState = _uiState.value
        if (currentState is DetailUiState.Success) {
            val recipe = currentState.recipe
            viewModelScope.launch {
                repository.addGroceryItemsFromRecipe(recipe)
                onAdded()
            }
        }
    }
}
