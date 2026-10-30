package com.example.recipe_helper.data.remote

import com.example.recipe_helper.data.remote.dto.MealListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApiService {

    @GET("search.php")
    suspend fun searchMeals(
        @Query("s") query: String
    ): MealListResponse

    @GET("filter.php")
    suspend fun filterByCategory(
        @Query("c") category: String
    ): MealListResponse

    @GET("lookup.php")
    suspend fun getMealById(
        @Query("i") id: String
    ): MealListResponse
}
