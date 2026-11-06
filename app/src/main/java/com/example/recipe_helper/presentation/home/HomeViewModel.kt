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

enum class QuickFilter {
    ALL,
    UNDER_20_MINS,
    HIGH_PROTEIN,
    LOW_CALORIE
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val recipes: List<Recipe>,
        val categories: List<String>,
        val searchQuery: String,
        val selectedCategory: String,
        val selectedQuickFilter: QuickFilter = QuickFilter.ALL
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow("All")
    private val selectedQuickFilter = MutableStateFlow(QuickFilter.ALL)

    val categories = listOf("All", "Italian", "American", "Indian", "Easy", "AI Generated")

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAllRecipes(),
        searchQuery,
        selectedCategory,
        selectedQuickFilter
    ) { recipes, query, category, quickFilter ->
        val filtered = recipes.filter { recipe ->
            val matchesQuery = recipe.title.contains(query, ignoreCase = true) ||
                    recipe.cuisine.contains(query, ignoreCase = true)

            val matchesCategory = when (category) {
                "All" -> true
                "Easy" -> recipe.difficulty.equals("Easy", ignoreCase = true)
                "AI Generated" -> recipe.source.name == "AI" || recipe.isCustom
                else -> recipe.cuisine.equals(category, ignoreCase = true)
            }

            val matchesQuickFilter = when (quickFilter) {
                QuickFilter.ALL -> true
                QuickFilter.UNDER_20_MINS -> (recipe.prepTimeMinutes + recipe.cookTimeMinutes) <= 20
                QuickFilter.HIGH_PROTEIN -> recipe.proteinGrams >= 30
                QuickFilter.LOW_CALORIE -> recipe.calories <= 400
            }

            matchesQuery && matchesCategory && matchesQuickFilter
        }

        HomeUiState.Success(
            recipes = filtered,
            categories = categories,
            searchQuery = query,
            selectedCategory = category,
            selectedQuickFilter = quickFilter
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

    fun onQuickFilterSelected(filter: QuickFilter) {
        if (selectedQuickFilter.value == filter) {
            selectedQuickFilter.value = QuickFilter.ALL
        } else {
            selectedQuickFilter.value = filter
        }
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            repository.toggleFavorite(recipe)
        }
    }
}
