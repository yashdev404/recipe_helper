package com.example.recipe_helper.data.local

import androidx.room.TypeConverter
import com.example.recipe_helper.domain.model.Ingredient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromIngredientsList(ingredients: List<Ingredient>?): String {
        return gson.toJson(ingredients ?: emptyList<Ingredient>())
    }

    @TypeConverter
    fun toIngredientsList(data: String?): List<Ingredient> {
        if (data.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<Ingredient>>() {}.type
        return try {
            gson.fromJson(data, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromInstructionsList(instructions: List<String>?): String {
        return gson.toJson(instructions ?: emptyList<String>())
    }

    @TypeConverter
    fun toInstructionsList(data: String?): List<String> {
        if (data.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(data, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
