package com.example.recipe_helper.presentation.meal_planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_helper.data.local.MealPlanEntity
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

data class MealPlannerUiState(
    val selectedDay: String = "Monday",
    val plans: List<MealPlanEntity> = emptyList(),
    val availableRecipes: List<Recipe> = emptyList(),
    val isPickerOpen: Boolean = false,
    val targetSlotMealType: String = "Breakfast",
    val totalCaloriesForSelectedDay: Int = 0,
    val totalProteinForSelectedDay: Int = 0
)

@HiltViewModel
class MealPlannerViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val selectedDayState = MutableStateFlow("Monday")
    private val isPickerOpenState = MutableStateFlow(false)
    private val targetSlotMealTypeState = MutableStateFlow("Breakfast")

    val uiState: StateFlow<MealPlannerUiState> = combine(
        repository.getAllMealPlans(),
        repository.getAllRecipes(),
        selectedDayState,
        isPickerOpenState,
        targetSlotMealTypeState
    ) { plans, recipes, selectedDay, isPickerOpen, targetMealType ->

        val dayPlans = plans.filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }
        val dayCalories = dayPlans.sumOf { it.calories }
        val dayProtein = dayPlans.sumOf { it.proteinGrams }

        MealPlannerUiState(
            selectedDay = selectedDay,
            plans = plans,
            availableRecipes = recipes,
            isPickerOpen = isPickerOpen,
            targetSlotMealType = targetMealType,
            totalCaloriesForSelectedDay = dayCalories,
            totalProteinForSelectedDay = dayProtein
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MealPlannerUiState()
    )

    fun setSelectedDay(day: String) {
        selectedDayState.value = day
    }

    fun openRecipePicker(mealType: String) {
        targetSlotMealTypeState.value = mealType
        isPickerOpenState.value = true
    }

    fun closeRecipePicker() {
        isPickerOpenState.value = false
    }

    fun assignRecipeToSlot(recipe: Recipe) {
        val day = selectedDayState.value
        val mealType = targetSlotMealTypeState.value
        viewModelScope.launch {
            repository.setMealPlanSlot(day, mealType, recipe)
            closeRecipePicker()
        }
    }

    fun removeRecipeFromSlot(mealType: String) {
        val day = selectedDayState.value
        viewModelScope.launch {
            repository.removeMealPlanSlot(day, mealType)
        }
    }

    fun exportWeekToGroceryList() {
        viewModelScope.launch {
            repository.addWeeklyMealPlanToGroceryList()
        }
    }
}
