package com.example.recipe_helper.presentation.ai_chef

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

sealed interface AiChefUiState {
    object Idle : AiChefUiState
    object Generating : AiChefUiState
    data class Success(val generatedRecipe: Recipe) : AiChefUiState
    data class Error(val message: String) : AiChefUiState
}

@HiltViewModel
class AiChefViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AiChefUiState>(AiChefUiState.Idle)
    val uiState: StateFlow<AiChefUiState> = _uiState.asStateFlow()

    fun generateRecipe(ingredients: List<String>) {
        val validIngredients = ingredients.filter { it.isNotBlank() }
        if (validIngredients.isEmpty()) {
            _uiState.value = AiChefUiState.Error("Please enter at least one ingredient.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AiChefUiState.Generating
            runCatching {
                val recipe = repository.generateAiRecipe(validIngredients)
                _uiState.value = AiChefUiState.Success(recipe)
            }.onFailure { err ->
                _uiState.value = AiChefUiState.Error(err.localizedMessage ?: "Failed to generate AI recipe.")
            }
        }
    }

    fun resetState() {
        _uiState.value = AiChefUiState.Idle
    }
}
