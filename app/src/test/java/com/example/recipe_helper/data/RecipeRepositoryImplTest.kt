package com.example.recipe_helper.data

import com.example.recipe_helper.data.local.RecipeDao
import com.example.recipe_helper.data.remote.MealApiService
import com.example.recipe_helper.data.repository.RecipeRepositoryImpl
import com.example.recipe_helper.domain.model.RecipeSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeRepositoryImplTest {

    private val dao: RecipeDao = mockk(relaxed = true)
    private val mealApiService: MealApiService = mockk(relaxed = true)
    private lateinit var repository: RecipeRepositoryImpl

    @Before
    fun setUp() {
        repository = RecipeRepositoryImpl(dao, mealApiService)
    }

    @Test
    fun `generateAiRecipe creates AI source recipe and persists to local DB`() = runTest {
        val ingredients = listOf("Chicken", "Garlic", "Butter")

        val result = repository.generateAiRecipe(ingredients)

        assertNotNull(result)
        assertEquals(RecipeSource.AI, result.source)
        assertEquals(true, result.title.contains("Chicken"))
        coVerify { dao.insertRecipe(any()) }
    }
}
