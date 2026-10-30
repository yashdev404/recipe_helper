package com.example.recipe_helper.presentation.grocery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_helper.data.local.GroceryItemEntity
import com.example.recipe_helper.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroceryUiState(
    val items: List<GroceryItemEntity> = emptyList(),
    val categorizedItems: Map<String, List<GroceryItemEntity>> = emptyMap()
)

@HiltViewModel
class GroceryViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    val uiState: StateFlow<GroceryUiState> = repository.getAllGroceryItems()
        .map { list ->
            val grouped = list.groupBy { it.category.ifBlank { "Pantry & Spices" } }
            GroceryUiState(
                items = list,
                categorizedItems = grouped
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GroceryUiState()
        )

    fun toggleItem(id: Long) {
        viewModelScope.launch {
            repository.toggleGroceryItem(id)
        }
    }

    fun addItem(name: String, amount: String, category: String) {
        viewModelScope.launch {
            repository.addCustomGroceryItem(name, amount, category)
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            repository.deleteGroceryItem(id)
        }
    }

    fun clearChecked() {
        viewModelScope.launch {
            repository.clearCheckedGroceryItems()
        }
    }
}
