package com.example.recipe_helper.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grocery_items")
data class GroceryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: String = "",
    val category: String = "Pantry",
    val isChecked: Boolean = false,
    val recipeTitle: String = ""
)
