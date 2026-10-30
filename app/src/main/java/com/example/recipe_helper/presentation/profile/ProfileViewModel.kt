package com.example.recipe_helper.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_helper.data.local.LoggedMealEntity
import com.example.recipe_helper.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

enum class Timeframe {
    TODAY,
    THIS_WEEK,
    THIS_MONTH
}

data class ProfileUiState(
    val timeframe: Timeframe = Timeframe.TODAY,
    val consumedCalories: Int = 0,
    val targetCalories: Int = 2000,
    val consumedProteinGrams: Int = 0,
    val targetProteinGrams: Int = 120,
    val loggedMeals: List<LoggedMealEntity> = emptyList(),
    val totalCookedCount: Int = 0,
    val totalFavoritesCount: Int = 0,
    val isLoggingDialogVisible: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val timeframeState = MutableStateFlow(Timeframe.TODAY)
    private val isDialogVisibleState = MutableStateFlow(false)

    val uiState: StateFlow<ProfileUiState> = combine(
        repository.getAllLoggedMeals(),
        repository.getAllRecipes(),
        timeframeState,
        isDialogVisibleState
    ) { loggedMeals, allRecipes, timeframe, isDialogVisible ->

        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()

        val startTime = when (timeframe) {
            Timeframe.TODAY -> {
                calendar.apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }
            Timeframe.THIS_WEEK -> {
                calendar.apply {
                    add(Calendar.DAY_OF_YEAR, -7)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }
            Timeframe.THIS_MONTH -> {
                calendar.apply {
                    add(Calendar.DAY_OF_YEAR, -30)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }
        }

        val filteredMeals = loggedMeals.filter { it.timestamp >= startTime }

        val targetCal = when (timeframe) {
            Timeframe.TODAY -> 2000
            Timeframe.THIS_WEEK -> 14000
            Timeframe.THIS_MONTH -> 60000
        }

        val targetProtein = when (timeframe) {
            Timeframe.TODAY -> 120
            Timeframe.THIS_WEEK -> 840
            Timeframe.THIS_MONTH -> 3600
        }

        val totalCal = filteredMeals.sumOf { it.calories }
        val totalProtein = filteredMeals.sumOf { it.proteinGrams }

        val favCount = allRecipes.count { it.isFavorite }
        val customCount = allRecipes.count { it.isCustom }

        ProfileUiState(
            timeframe = timeframe,
            consumedCalories = totalCal,
            targetCalories = targetCal,
            consumedProteinGrams = totalProtein,
            targetProteinGrams = targetProtein,
            loggedMeals = filteredMeals,
            totalCookedCount = customCount + loggedMeals.size,
            totalFavoritesCount = favCount,
            isLoggingDialogVisible = isDialogVisible
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    init {
        // Seed default meal logs if none exist so meters demonstrate immediate tracking value
        viewModelScope.launch {
            repository.getAllLoggedMeals().collect { list ->
                if (list.isEmpty()) {
                    val now = System.currentTimeMillis()
                    repository.logMeal("High Protein Omelette & Toast", 450, 32)
                    repository.logMeal("Grilled Chicken & Quinoa Salad", 580, 48)
                    repository.logMeal("Greek Yogurt Berry Bowl", 260, 22)
                }
            }
        }
    }

    fun setTimeframe(timeframe: Timeframe) {
        timeframeState.value = timeframe
    }

    fun showLoggingDialog(show: Boolean) {
        isDialogVisibleState.value = show
    }

    fun logMeal(title: String, calories: String, protein: String) {
        val cal = calories.toIntOrNull() ?: 350
        val prot = protein.toIntOrNull() ?: 25
        val mealTitle = title.ifBlank { "Balanced Meal" }

        viewModelScope.launch {
            repository.logMeal(title = mealTitle, calories = cal, proteinGrams = prot)
            showLoggingDialog(false)
        }
    }

    fun deleteLoggedMeal(id: Long) {
        viewModelScope.launch {
            repository.deleteLoggedMeal(id)
        }
    }
}
