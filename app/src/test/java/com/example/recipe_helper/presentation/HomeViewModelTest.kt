package com.example.recipe_helper.presentation

import app.cash.turbine.test
import com.example.recipe_helper.domain.model.Recipe
import com.example.recipe_helper.domain.model.RecipeSource
import com.example.recipe_helper.domain.repository.RecipeRepository
import com.example.recipe_helper.presentation.home.HomeUiState
import com.example.recipe_helper.presentation.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: RecipeRepository = mockk(relaxed = true)
    private lateinit var viewModel: HomeViewModel

    private val sampleRecipes = listOf(
        Recipe(
            id = "1",
            title = "Spaghetti Carbonara",
            cuisine = "Italian",
            difficulty = "Easy",
            source = RecipeSource.REMOTE
        ),
        Recipe(
            id = "2",
            title = "Tacos",
            cuisine = "Mexican",
            difficulty = "Medium",
            source = RecipeSource.LOCAL
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getAllRecipes() } returns flowOf(sampleRecipes)
        viewModel = HomeViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState emits Success with filtered recipes when search query is changed`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertTrue(initialState is HomeUiState.Loading || initialState is HomeUiState.Success)

            testScheduler.advanceUntilIdle()

            val successState = awaitItem() as HomeUiState.Success
            assertEquals(2, successState.recipes.size)

            viewModel.onSearchQueryChanged("Carbonara")
            testScheduler.advanceUntilIdle()

            val filteredState = awaitItem() as HomeUiState.Success
            assertEquals(1, filteredState.recipes.size)
            assertEquals("Spaghetti Carbonara", filteredState.recipes.first().title)
        }
    }

    @Test
    fun `toggleFavorite triggers repository call`() = runTest {
        val targetRecipe = sampleRecipes.first()
        viewModel.toggleFavorite(targetRecipe)
        testScheduler.advanceUntilIdle()

        coVerify { repository.toggleFavorite(targetRecipe) }
    }
}
