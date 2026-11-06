package com.example.recipe_helper.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_helper.domain.model.Recipe
import com.example.recipe_helper.presentation.common.RecipeCard
import com.example.recipe_helper.ui.theme.OrangePrimary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onRecipeClick: (Recipe) -> Unit,
    onAddRecipeClick: () -> Unit,
    onAiChefClick: () -> Unit,
    onGroceryClick: () -> Unit = {},
    onMealPlannerClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRecipeClick,
                containerColor = OrangePrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Recipe",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = OrangePrimary)
                    }
                }

                is HomeUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                    ) {
                        // 1. Header Greeting (scrolls away smoothly)
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Hello Chef! 👋",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "What do you want to cook?",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(OrangePrimary.copy(alpha = 0.15f))
                                            .clickable { onMealPlannerClick() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = "Meal Planner",
                                            tint = OrangePrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(OrangePrimary.copy(alpha = 0.15f))
                                            .clickable { onGroceryClick() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingCart,
                                            contentDescription = "Grocery List",
                                            tint = OrangePrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // 2. AI Chef Assistance Banner (scrolls away smoothly)
                        item {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = OrangePrimary.copy(alpha = 0.12f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onAiChefClick() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(OrangePrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "AI Chef",
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "AI Chef Assistant ✨",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "Have leftovers? Generate recipes by ingredients!",
                                            fontSize = 12.sp,
                                            color = Color.DarkGray
                                        )
                                    }

                                    Text(
                                        text = "Try Now >",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OrangePrimary
                                    )
                                }
                            }
                        }

                        // 3. STICKY HEADER: Search Bar & Cuisine Category Chips
                        stickyHeader {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(vertical = 4.dp)
                            ) {
                                OutlinedTextField(
                                    value = state.searchQuery,
                                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                                    placeholder = { Text("Search recipe or cuisine...", color = Color.Gray, fontSize = 14.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search",
                                            tint = Color.Gray
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = OrangePrimary,
                                        unfocusedBorderColor = Color(0xFFE0E0E0),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    ),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(state.categories) { cat ->
                                        val isSelected = state.selectedCategory == cat
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = if (isSelected) OrangePrimary else Color.White,
                                            border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE0E0E0)) else null,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(20.dp))
                                                .clickable { viewModel.onCategorySelected(cat) }
                                        ) {
                                            Text(
                                                text = cat,
                                                color = if (isSelected) Color.White else Color.Black,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 13.sp,
                                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Advanced Quick Filter Chips
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    item {
                                        val isSelected = state.selectedQuickFilter == QuickFilter.UNDER_20_MINS
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (isSelected) Color(0xFFFF9800) else Color(0xFFF0F0F0),
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable { viewModel.onQuickFilterSelected(QuickFilter.UNDER_20_MINS) }
                                        ) {
                                            Text(
                                                text = "⚡ Under 20 Mins",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else Color.DarkGray,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }

                                    item {
                                        val isSelected = state.selectedQuickFilter == QuickFilter.HIGH_PROTEIN
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (isSelected) Color(0xFF00BCD4) else Color(0xFFF0F0F0),
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable { viewModel.onQuickFilterSelected(QuickFilter.HIGH_PROTEIN) }
                                        ) {
                                            Text(
                                                text = "💪 High Protein (>30g)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else Color.DarkGray,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }

                                    item {
                                        val isSelected = state.selectedQuickFilter == QuickFilter.LOW_CALORIE
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (isSelected) Color(0xFFFF5722) else Color(0xFFF0F0F0),
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable { viewModel.onQuickFilterSelected(QuickFilter.LOW_CALORIE) }
                                        ) {
                                            Text(
                                                text = "🔥 Low Calorie (<400)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else Color.DarkGray,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 4. Section Header
                        item {
                            Text(
                                text = "Recipes (${state.recipes.size})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        // 5. Recipe List Items
                        if (state.recipes.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No recipes found. Try searching or generating with AI!",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        } else {
                            items(state.recipes.distinctBy { it.id }, key = { it.id }) { recipe ->
                                RecipeCard(
                                    recipe = recipe,
                                    onClick = { onRecipeClick(recipe) },
                                    onFavoriteToggle = { viewModel.toggleFavorite(recipe) }
                                )
                            }
                        }
                    }
                }

                is HomeUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
