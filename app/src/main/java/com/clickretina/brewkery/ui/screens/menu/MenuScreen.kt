package com.clickretina.brewkery.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.clickretina.brewkery.domain.model.Category
import com.clickretina.brewkery.domain.model.MenuItem
import com.clickretina.brewkery.ui.theme.AmberBadgeBg
import com.clickretina.brewkery.ui.theme.AmberBadgeText
import com.clickretina.brewkery.ui.theme.AmberStar
import com.clickretina.brewkery.ui.theme.Crema
import com.clickretina.brewkery.ui.theme.CremaBannerFrom
import com.clickretina.brewkery.ui.theme.CremaBorder
import com.clickretina.brewkery.ui.theme.CremaSoft
import com.clickretina.brewkery.ui.theme.CremaSurface
import com.clickretina.brewkery.ui.theme.EmeraldDark
import com.clickretina.brewkery.ui.theme.EmeraldGreen
import com.clickretina.brewkery.ui.theme.Espresso
import com.clickretina.brewkery.ui.theme.EspressoLight
import com.clickretina.brewkery.ui.theme.Terracotta
import com.clickretina.brewkery.ui.theme.TerracottaDark
import com.clickretina.brewkery.ui.theme.TextMuted
import java.util.Locale

@Composable
fun MenuScreen(
    viewModel: MenuViewModel,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrder: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val storeMeta by viewModel.storeMeta.collectAsState()
    val cartBill by viewModel.cartBill.collectAsState()
    val activeOrder by viewModel.activeOrder.collectAsState()
    val selectedCategory by viewModel.selectedCategoryId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        containerColor = Crema,
        bottomBar = {
            if (cartBill.totalItemCount > 0) {
                StickyCartBar(
                    totalQty = cartBill.totalItemCount,
                    subtotal = cartBill.subtotal,
                    onViewCart = onNavigateToCart
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Header Bar
            BrandHeader(
                cartCount = cartBill.totalItemCount,
                onCartClick = onNavigateToCart
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic Home Announcement / Active Order Banner
            DynamicHomeBanner(
                activeOrder = activeOrder,
                storeDeliveryTime = storeMeta.estimatedDeliveryTime,
                deliveryFee = storeMeta.deliveryFee,
                onTrackOrder = onNavigateToOrder
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            when (val state = uiState) {
                is MenuUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Terracotta)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Brewing menu...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                is MenuUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "Unable to load menu",
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
                                onClick = viewModel::loadMenu,
                                colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retry")
                            }
                        }
                    }
                }
                is MenuUiState.Success -> {
                    // Category Filter Row
                    CategoryFilterRow(
                        categories = state.categories,
                        selectedId = selectedCategory,
                        onSelect = viewModel::onCategorySelect
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Items List
                    if (state.items.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No items found matching your search.",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(state.items, key = { it.id }) { item ->
                                MenuItemCard(
                                    item = item,
                                    onClick = { onNavigateToDetail(item.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BrandHeader(
    cartCount: Int,
    onCartClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Espresso),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BK",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Fresh Roast & Bakes",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Brewkery Artisans",
                    color = Espresso,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Box {
            IconButton(
                onClick = onCartClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CremaSurface)
                    .border(1.dp, CremaBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Cart",
                    tint = Espresso,
                    modifier = Modifier.size(18.dp)
                )
            }
            if (cartCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Terracotta),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cartCount.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DynamicHomeBanner(
    activeOrder: com.clickretina.brewkery.domain.model.OrderTicket?,
    storeDeliveryTime: String,
    deliveryFee: Double,
    onTrackOrder: () -> Unit
) {
    val isOrder = activeOrder != null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = if (isOrder) listOf(Color(0xFFE6F4EA), Color(0xFFD4EDDA))
                    else listOf(CremaBannerFrom, CremaSoft)
                )
            )
            .border(1.dp, if (isOrder) Color(0xFFC3E6CB) else CremaBorder, RoundedCornerShape(16.dp))
            .clickable(enabled = isOrder, onClick = onTrackOrder)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CremaSurface)
                    .border(1.dp, CremaBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isOrder) "🟢" else "🛵",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isOrder) "ACTIVE ORDER" else "STORE INFO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isOrder) EmeraldGreen else Terracotta,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isOrder) EmeraldGreen else Terracotta)
                    )
                }

                Text(
                    text = if (isOrder) "Active Order ${activeOrder?.orderId}" else "Delivery in $storeDeliveryTime",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Espresso
                )

                Text(
                    text = if (isOrder) "Preparing (${activeOrder?.estimatedWait})" else String.format(Locale.US, "$%.2f flat fee", deliveryFee),
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isOrder) EmeraldDark else CremaSurface)
                .border(1.dp, if (isOrder) EmeraldDark else CremaBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isOrder) "Track" else "Open",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOrder) Color.White else Espresso
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Search roasts, cold brews, or croissants...",
                color = TextMuted,
                fontSize = 12.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CremaSurface,
            unfocusedContainerColor = CremaSurface,
            focusedBorderColor = Terracotta,
            unfocusedBorderColor = CremaBorder,
            focusedTextColor = Espresso,
            unfocusedTextColor = Espresso
        ),
        singleLine = true
    )
}

@Composable
fun CategoryFilterRow(
    categories: List<Category>,
    selectedId: String,
    onSelect: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        item {
            CategoryChip(
                label = "All Menu",
                icon = "✨",
                isSelected = selectedId == "all",
                onClick = { onSelect("all") }
            )
        }
        items(categories, key = { it.id }) { cat ->
            CategoryChip(
                label = cat.name,
                icon = cat.icon,
                isSelected = selectedId == cat.id,
                onClick = { onSelect(cat.id) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Terracotta else CremaSurface)
            .border(1.dp, if (isSelected) Terracotta else CremaBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else EspressoLight
        )
    }
}

@Composable
fun MenuItemCard(
    item: MenuItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CremaSurface)
            .border(1.dp, CremaBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            if (!item.badge.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AmberBadgeBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.badge,
                        color = AmberBadgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
            }

            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Espresso,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = item.tagline,
                color = TextMuted,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "★", color = AmberStar, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${item.rating} (${item.reviewCount})",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = String.format(Locale.US, "$%.2f", item.basePrice),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = Terracotta
                )
            }
        }
    }
}

@Composable
fun StickyCartBar(
    totalQty: Int,
    subtotal: Double,
    onViewCart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Terracotta, TerracottaDark)
                )
            )
            .clickable(onClick = onViewCart)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = totalQty.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "View Cart",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
                Text(
                    text = String.format(Locale.US, "Subtotal: $%.2f", subtotal),
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Text(
            text = "Proceed →",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}
