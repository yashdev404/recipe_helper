package com.example.recipe_helper.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recipe_helper.presentation.ai_chef.AiChefScreen
import com.example.recipe_helper.presentation.ai_chef.AiChefViewModel
import com.example.recipe_helper.presentation.detail.DetailViewModel
import com.example.recipe_helper.presentation.detail.RecipeDetailScreen
import com.example.recipe_helper.presentation.home.HomeScreen
import com.example.recipe_helper.presentation.home.HomeViewModel
import com.example.recipe_helper.presentation.cooking_mode.CookingModeScreen
import com.example.recipe_helper.presentation.grocery.GroceryScreen
import com.example.recipe_helper.presentation.grocery.GroceryViewModel
import com.example.recipe_helper.presentation.meal_planner.MealPlannerScreen
import com.example.recipe_helper.presentation.meal_planner.MealPlannerViewModel
import com.example.recipe_helper.presentation.my_recipes.AddRecipeScreen
import com.example.recipe_helper.presentation.my_recipes.AddRecipeViewModel
import com.example.recipe_helper.presentation.my_recipes.MyRecipesScreen
import com.example.recipe_helper.presentation.profile.ProfileScreen
import com.example.recipe_helper.presentation.profile.ProfileViewModel
import com.example.recipe_helper.ui.theme.OrangePrimary

object ScreenRoute {
    const val HOME = "home"
    const val AI_CHEF = "ai_chef"
    const val MY_RECIPES = "my_recipes"
    const val PROFILE = "profile"
    const val ADD_RECIPE = "add_recipe"
    const val DETAIL = "detail"
    const val COOKING_MODE = "cooking_mode"
    const val GROCERY = "grocery"
    const val MEAL_PLANNER = "meal_planner"
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        ScreenRoute.HOME,
        ScreenRoute.AI_CHEF,
        ScreenRoute.MY_RECIPES,
        ScreenRoute.PROFILE
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BottomNavItem(
                            icon = Icons.Default.Home,
                            label = "Home",
                            isSelected = currentRoute == ScreenRoute.HOME,
                            onClick = {
                                if (currentRoute != ScreenRoute.HOME) {
                                    navController.navigate(ScreenRoute.HOME) {
                                        popUpTo(ScreenRoute.HOME) { inclusive = true }
                                    }
                                }
                            }
                        )

                        BottomNavItem(
                            icon = Icons.Default.AutoAwesome,
                            label = "AI Chef",
                            isSelected = currentRoute == ScreenRoute.AI_CHEF,
                            onClick = {
                                if (currentRoute != ScreenRoute.AI_CHEF) {
                                    navController.navigate(ScreenRoute.AI_CHEF) {
                                        popUpTo(ScreenRoute.HOME)
                                    }
                                }
                            }
                        )

                        BottomNavItem(
                            icon = Icons.Default.Bookmark,
                            label = "My Recipes",
                            isSelected = currentRoute == ScreenRoute.MY_RECIPES,
                            onClick = {
                                if (currentRoute != ScreenRoute.MY_RECIPES) {
                                    navController.navigate(ScreenRoute.MY_RECIPES) {
                                        popUpTo(ScreenRoute.HOME)
                                    }
                                }
                            }
                        )

                        BottomNavItem(
                            icon = Icons.Default.Person,
                            label = "Profile",
                            isSelected = currentRoute == ScreenRoute.PROFILE,
                            onClick = {
                                if (currentRoute != ScreenRoute.PROFILE) {
                                    navController.navigate(ScreenRoute.PROFILE) {
                                        popUpTo(ScreenRoute.HOME)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = ScreenRoute.HOME
            ) {
                composable(ScreenRoute.HOME) {
                    val viewModel: HomeViewModel = hiltViewModel()
                    HomeScreen(
                        viewModel = viewModel,
                        onRecipeClick = { recipe ->
                            navController.navigate("${ScreenRoute.DETAIL}/${recipe.id}")
                        },
                        onAddRecipeClick = {
                            navController.navigate(ScreenRoute.ADD_RECIPE)
                        },
                        onAiChefClick = {
                            navController.navigate(ScreenRoute.AI_CHEF)
                        },
                        onGroceryClick = {
                            navController.navigate(ScreenRoute.GROCERY)
                        },
                        onMealPlannerClick = {
                            navController.navigate(ScreenRoute.MEAL_PLANNER)
                        }
                    )
                }

                composable(ScreenRoute.AI_CHEF) {
                    val viewModel: AiChefViewModel = hiltViewModel()
                    AiChefScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() },
                        onRecipeClick = { recipe ->
                            navController.navigate("${ScreenRoute.DETAIL}/${recipe.id}")
                        }
                    )
                }

                composable(ScreenRoute.MY_RECIPES) {
                    val viewModel: HomeViewModel = hiltViewModel()
                    val state by viewModel.uiState.collectAsState()
                    val recipes = (state as? com.example.recipe_helper.presentation.home.HomeUiState.Success)?.recipes ?: emptyList()

                    MyRecipesScreen(
                        recipes = recipes,
                        onRecipeClick = { recipe ->
                            navController.navigate("${ScreenRoute.DETAIL}/${recipe.id}")
                        },
                        onFavoriteToggle = { recipe ->
                            viewModel.toggleFavorite(recipe)
                        },
                        onAddRecipeClick = {
                            navController.navigate(ScreenRoute.ADD_RECIPE)
                        }
                    )
                }

                composable(ScreenRoute.PROFILE) {
                    val viewModel: ProfileViewModel = hiltViewModel()
                    ProfileScreen(
                        viewModel = viewModel,
                        onGroceryClick = {
                            navController.navigate(ScreenRoute.GROCERY)
                        },
                        onMealPlannerClick = {
                            navController.navigate(ScreenRoute.MEAL_PLANNER)
                        }
                    )
                }

                composable(ScreenRoute.ADD_RECIPE) {
                    val viewModel: AddRecipeViewModel = hiltViewModel()
                    AddRecipeScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() },
                        onRecipeSaved = {
                            navController.navigate(ScreenRoute.MY_RECIPES) {
                                popUpTo(ScreenRoute.HOME)
                            }
                        }
                    )
                }

                composable(
                    route = "${ScreenRoute.DETAIL}/{recipeId}",
                    arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
                    val viewModel: DetailViewModel = hiltViewModel()
                    RecipeDetailScreen(
                        recipeId = recipeId,
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() },
                        onStartCookingClick = { recipe ->
                            navController.navigate("${ScreenRoute.COOKING_MODE}/${recipe.id}")
                        }
                    )
                }

                composable(
                    route = "${ScreenRoute.COOKING_MODE}/{recipeId}",
                    arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
                    val detailViewModel: DetailViewModel = hiltViewModel()
                    val state by detailViewModel.uiState.collectAsState()

                    LaunchedEffect(recipeId) {
                        detailViewModel.loadRecipe(recipeId)
                    }

                    val recipe = (state as? com.example.recipe_helper.presentation.detail.DetailUiState.Success)?.recipe
                    if (recipe != null) {
                        CookingModeScreen(
                            recipe = recipe,
                            onBackClick = { navController.popBackStack() },
                            onFinishCooking = { finishedRecipe ->
                                detailViewModel.logMealAsEaten {
                                    navController.popBackStack()
                                }
                            }
                        )
                    }
                }

                composable(ScreenRoute.GROCERY) {
                    val viewModel: GroceryViewModel = hiltViewModel()
                    GroceryScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(ScreenRoute.MEAL_PLANNER) {
                    val viewModel: MealPlannerViewModel = hiltViewModel()
                    MealPlannerScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .width(54.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) OrangePrimary else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) OrangePrimary else Color.Gray
        )
    }
}
