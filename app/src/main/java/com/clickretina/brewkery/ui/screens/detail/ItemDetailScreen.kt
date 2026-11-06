package com.clickretina.brewkery.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.clickretina.brewkery.domain.model.MenuItem
import com.clickretina.brewkery.domain.model.MilkOption
import com.clickretina.brewkery.domain.model.SizeOption
import com.clickretina.brewkery.ui.theme.AmberBadgeBg
import com.clickretina.brewkery.ui.theme.AmberStar
import com.clickretina.brewkery.ui.theme.Crema
import com.clickretina.brewkery.ui.theme.CremaBorder
import com.clickretina.brewkery.ui.theme.CremaSoft
import com.clickretina.brewkery.ui.theme.CremaSurface
import com.clickretina.brewkery.ui.theme.Espresso
import com.clickretina.brewkery.ui.theme.EspressoLight
import com.clickretina.brewkery.ui.theme.RoseFavorite
import com.clickretina.brewkery.ui.theme.Terracotta
import com.clickretina.brewkery.ui.theme.TerracottaDark
import com.clickretina.brewkery.ui.theme.TextMuted
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemDetailScreen(
    viewModel: ItemDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedSize by viewModel.selectedSize.collectAsState()
    val selectedMilk by viewModel.selectedMilk.collectAsState()
    val selectedSugar by viewModel.selectedSugar.collectAsState()
    val quantity by viewModel.quantity.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val dynamicUnitPrice by viewModel.dynamicUnitPrice.collectAsState()
    val dynamicTotal by viewModel.dynamicTotal.collectAsState()

    Scaffold(
        containerColor = Crema,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CremaSurface)
                        .border(1.dp, CremaBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Espresso,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "ITEM CUSTOMIZER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Espresso,
                    letterSpacing = 1.sp
                )

                IconButton(
                    onClick = viewModel::toggleFavorite,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CremaSurface)
                        .border(1.dp, CremaBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) RoseFavorite else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        bottomBar = {
            if (uiState is ItemDetailUiState.Success) {
                DetailBottomBar(
                    quantity = quantity,
                    totalPrice = dynamicTotal,
                    onAdjustQty = viewModel::adjustQuantity,
                    onAddToCart = {
                        if (viewModel.addToCart()) {
                            onNavigateToCart()
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is ItemDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Terracotta)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Loading customizations...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            is ItemDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Failed to load item",
                            fontWeight = FontWeight.Bold,
                            color = Espresso,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.message,
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = viewModel::loadItem,
                            colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry")
                        }
                    }
                }
            }
            is ItemDetailUiState.Success -> {
                val item = state.item
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Hero Image with Badge
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(20.dp))
                        ) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (!item.badge.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Espresso.copy(alpha = 0.85f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = item.badge,
                                        color = AmberStar,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

                    // Title & Dynamic Unit Price
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Espresso
                                )
                                Text(
                                    text = item.tagline,
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = String.format(Locale.US, "$%.2f", dynamicUnitPrice),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Terracotta,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Metadata Row: Prep Time, Calories, Rating
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MetaPill(icon = "⏱", text = item.prepTime)
                            if (item.calories > 0) {
                                MetaPill(icon = "🔥", text = "${item.calories} kcal")
                            }
                            MetaPill(icon = "★", text = "${item.rating} (${item.reviewCount})")
                        }
                    }

                    // Description
                    item {
                        Text(
                            text = item.description,
                            fontSize = 12.sp,
                            color = TextMuted,
                            lineHeight = 18.sp
                        )
                    }

                    // Key Ingredients Chips
                    if (item.ingredients.isNotEmpty()) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "KEY INGREDIENTS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextMuted,
                                    letterSpacing = 0.8.sp
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    item.ingredients.forEach { ing ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(CremaSurface)
                                                .border(1.dp, CremaBorder, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = ing,
                                                fontSize = 10.sp,
                                                color = EspressoLight,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 1. Sizes Selection
                    if (item.sizes.isNotEmpty()) {
                        item {
                            CustomizationSection(title = "1. SELECT SIZE") {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(item.sizes) { size ->
                                        val isSelected = selectedSize?.id == size.id
                                        SelectableCard(
                                            label = size.label,
                                            extraPrice = size.extraPrice,
                                            isSelected = isSelected,
                                            onClick = { viewModel.selectSize(size) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. Milk Options (or Spreads / Toppings)
                    if (item.milkOptions.isNotEmpty()) {
                        item {
                            val isBakery = item.categoryId == "cat_bakery"
                            val sectionTitle = if (isBakery) "2. BUTTER / SPREAD" else "2. MILK / FOAM OPTION"
                            CustomizationSection(title = sectionTitle) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(item.milkOptions) { milk ->
                                        val isSelected = selectedMilk?.id == milk.id
                                        SelectableCard(
                                            label = milk.name,
                                            extraPrice = milk.extraPrice,
                                            isSelected = isSelected,
                                            onClick = { viewModel.selectMilk(milk) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. Sugar Levels (or Warmth / Serving)
                    if (item.sugarLevels.isNotEmpty()) {
                        item {
                            val isBakery = item.categoryId == "cat_bakery"
                            val sectionTitle = if (isBakery) "3. SERVING PREFERENCE" else "3. SWEETNESS / SUGAR LEVEL"
                            CustomizationSection(title = sectionTitle) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(item.sugarLevels) { sugar ->
                                        val isSelected = selectedSugar == sugar
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) CremaSoft else CremaSurface)
                                                .border(
                                                    1.dp,
                                                    if (isSelected) Terracotta else CremaBorder,
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .clickable { viewModel.selectSugar(sugar) }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = sugar,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Terracotta else TextMuted
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
    }
}

@Composable
fun MetaPill(icon: String, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CremaSurface)
            .border(1.dp, CremaBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 10.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )
    }
}

@Composable
fun CustomizationSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CremaSurface)
            .border(1.dp, CremaBorder, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = Espresso,
            letterSpacing = 0.8.sp
        )
        content()
    }
}

@Composable
fun SelectableCard(
    label: String,
    extraPrice: Double,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) CremaSoft else CremaSurface)
            .border(1.dp, if (isSelected) Terracotta else CremaBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Terracotta else Espresso
        )
        Text(
            text = if (extraPrice > 0) String.format(Locale.US, "+$%.2f", extraPrice) else "+$0.00",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) TerracottaDark else TextMuted,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun DetailBottomBar(
    quantity: Int,
    totalPrice: Double,
    onAdjustQty: (Int) -> Unit,
    onAddToCart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CremaSurface)
            .border(1.dp, CremaBorder)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Quantity Stepper
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(CremaSoft)
                .border(1.dp, CremaBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "−",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Espresso,
                modifier = Modifier
                    .clickable { onAdjustQty(-1) }
                    .padding(horizontal = 6.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = quantity.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Espresso,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "+",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Espresso,
                modifier = Modifier
                    .clickable { onAdjustQty(1) }
                    .padding(horizontal = 6.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Add to Cart Button
        Button(
            onClick = onAddToCart,
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
        ) {
            Text(
                text = "Add to Cart",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "•", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = String.format(Locale.US, "$%.2f", totalPrice),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}
