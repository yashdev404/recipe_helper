package com.example.recipe_helper.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logged_meals")
data class LoggedMealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: String = "",
    val title: String,
    val calories: Int,
    val proteinGrams: Int,
    val timestamp: Long = System.currentTimeMillis()
)
