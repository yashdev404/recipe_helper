package com.example.recipe_helper.presentation.detail

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.recipe_helper.domain.model.Recipe
import com.example.recipe_helper.ui.theme.OrangePrimary

@Composable
fun RecipeDetailScreen(
    recipeId: String,
    viewModel: DetailViewModel,
    onBackClick: () -> Unit,
    onStartCookingClick: (Recipe) -> Unit = {},
    onEditClick: (Recipe) -> Unit = {}
) {
    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            }

            is DetailUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message, color = Color.Red, fontSize = 16.sp)
                }
            }

            is DetailUiState.Success -> {
                val recipe = state.recipe
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Hero Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        AsyncImage(
                            model = recipe.imageUrl.ifBlank { "https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=600&auto=format&fit=crop" },
                            contentDescription = recipe.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Back Button
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .padding(16.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                                .align(Alignment.TopStart)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }

                        // Action Buttons on Top Right
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.TopEnd),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Edit Recipe Button
                            IconButton(
                                onClick = { onEditClick(recipe) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.9f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Recipe",
                                    tint = Color.Black
                                )
                            }

                            // Favorite Button
                            IconButton(
                                onClick = { viewModel.toggleFavorite() },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.9f))
                            ) {
                                Icon(
                                    imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (recipe.isFavorite) Color.Red else Color.Gray
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Cuisine & Difficulty Badges
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = OrangePrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = recipe.cuisine,
                                    color = OrangePrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = recipe.difficulty,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = recipe.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Stat Boxes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatBox(icon = Icons.Default.Schedule, label = "Prep", value = "${recipe.prepTimeMinutes}m", modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(8.dp))
                            StatBox(icon = Icons.Default.Schedule, label = "Cook", value = "${recipe.cookTimeMinutes}m", modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(8.dp))
                            StatBox(icon = Icons.Default.People, label = "Servings", value = "${recipe.servings}", modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(8.dp))
                            StatBox(icon = Icons.Default.LocalFireDepartment, label = "Calories", value = "${recipe.calories}", modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons: Start Cooking Mode, Add to Grocery List, Log as Eaten
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // 1. Start Cooking Mode
                            Button(
                                onClick = { onStartCookingClick(recipe) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Start Cooking Mode 🍳",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // 2. Add to Grocery List
                                var isAddedToGrocery by remember { mutableStateOf(false) }
                                Button(
                                    onClick = {
                                        viewModel.addIngredientsToGroceryList {
                                            isAddedToGrocery = true
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isAddedToGrocery) Color(0xFF4CAF50) else Color(0xFFEEEEEE)
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (isAddedToGrocery) Icons.Default.CheckCircle else Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = if (isAddedToGrocery) Color.White else Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAddedToGrocery) "In Grocery 🛒" else "+ Grocery List",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isAddedToGrocery) Color.White else Color.Black
                                    )
                                }

                                // 3. Log as Eaten
                                var isLogged by remember { mutableStateOf(false) }
                                Button(
                                    onClick = {
                                        viewModel.logMealAsEaten {
                                            isLogged = true
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isLogged) Color(0xFF4CAF50) else Color(0xFFEEEEEE)
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (isLogged) Icons.Default.CheckCircle else Icons.Default.Restaurant,
                                        contentDescription = null,
                                        tint = if (isLogged) Color.White else Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isLogged) "Logged ✓" else "+ Log Eaten",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isLogged) Color.White else Color.Black
                                    )
                                }
                            }
                        }

                        // AI Tips Banner if present
                        recipe.aiTips?.let { tips ->
                            Spacer(modifier = Modifier.height(20.dp))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = OrangePrimary.copy(alpha = 0.1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI Tip",
                                        tint = OrangePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = tips,
                                        fontSize = 13.sp,
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Ingredients
                        Text(
                            text = "Ingredients",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (recipe.ingredients.isEmpty()) {
                            Text("No ingredients listed.", color = Color.Gray, fontSize = 14.sp)
                        } else {
                            recipe.ingredients.forEach { ingredient ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = OrangePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (ingredient.amount.isNotBlank()) "${ingredient.amount} ${ingredient.name}" else ingredient.name,
                                        fontSize = 15.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Step-by-Step Cooking Instructions with Progress Tracker
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Cooking Steps",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            if (recipe.instructions.isNotEmpty()) {
                                Text(
                                    text = "${state.completedSteps.size}/${recipe.instructions.size} Done",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (recipe.instructions.isEmpty()) {
                            Text("No step-by-step instructions provided.", color = Color.Gray, fontSize = 14.sp)
                        } else {
                            recipe.instructions.forEachIndexed { index, step ->
                                val isCompleted = state.completedSteps.contains(index)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clickable { viewModel.toggleStep(index) },
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                                        contentDescription = "Step",
                                        tint = if (isCompleted) OrangePrimary else Color.Gray,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = step,
                                        fontSize = 15.sp,
                                        color = if (isCompleted) Color.Gray else Color.DarkGray,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = OrangePrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.Black
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}
