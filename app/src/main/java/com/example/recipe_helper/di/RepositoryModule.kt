package com.example.recipe_helper.di

import com.example.recipe_helper.data.repository.RecipeRepositoryImpl
import com.example.recipe_helper.domain.repository.RecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRecipeRepository(
        impl: RecipeRepositoryImpl
    ): RecipeRepository {
        return impl
    }
}
