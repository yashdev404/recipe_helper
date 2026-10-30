package com.example.recipe_helper.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_helper.domain.model.Recipe
import com.example.recipe_helper.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val recipes: List<Recipe>,
        val categories: List<String>,
        val searchQuery: String,
        val selectedCategory: String
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow("All")

    val categories = listOf("All", "Italian", "American", "Indian", "Easy", "AI Generated")

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAllRecipes(),
        searchQuery,
        selectedCategory
    ) { recipes, query, category ->
        val filtered = recipes.filter { recipe ->
            val matchesQuery = recipe.title.contains(query, ignoreCase = true) ||
                    recipe.cuisine.contains(query, ignoreCase = true)
            val matchesCategory = when (category) {
                "All" -> true
                "Easy" -> recipe.difficulty.equals("Easy", ignoreCase = true)
                "AI Generated" -> recipe.source.name == "AI" || recipe.isCustom
                else -> recipe.cuisine.equals(category, ignoreCase = true)
            }
            matchesQuery && matchesCategory
        }

        HomeUiState.Success(
            recipes = filtered,
            categories = categories,
            searchQuery = query,
            selectedCategory = category
        ) as HomeUiState
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    init {
        viewModelScope.launch {
            repository.syncRemoteRecipesToLocal()
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        selectedCategory.value = category
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            repository.toggleFavorite(recipe)
        }
    }
}
