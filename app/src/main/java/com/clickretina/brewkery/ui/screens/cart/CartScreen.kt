package com.clickretina.brewkery.ui.screens.cart

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.ui.theme.Crema
import com.clickretina.brewkery.ui.theme.CremaBorder
import com.clickretina.brewkery.ui.theme.CremaSoft
import com.clickretina.brewkery.ui.theme.CremaSurface
import com.clickretina.brewkery.ui.theme.Espresso
import com.clickretina.brewkery.ui.theme.RoseFavorite
import com.clickretina.brewkery.ui.theme.Terracotta
import com.clickretina.brewkery.ui.theme.TextMuted
import java.util.Locale

@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onNavigateBack: () -> Unit,
    onOrderPlaced: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val cartBill by viewModel.cartBill.collectAsState()

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
                    text = "YOUR CART",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Espresso,
                    letterSpacing = 1.sp
                )

                if (cartItems.isNotEmpty()) {
                    TextButton(onClick = viewModel::clearCart) {
                        Text(
                            text = "Clear Cart",
                            color = RoseFavorite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            val ticket = viewModel.placeOrder()
                            if (ticket != null) {
                                onOrderPlaced()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Place Order Now",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "•", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = String.format(Locale.US, "$%.2f", cartBill.totalPayable),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CremaSurface)
                        .border(1.dp, CremaBorder, RoundedCornerShape(16.dp))
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = CremaBorder,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your in-memory cart is empty.",
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Cart Line Items
                items(cartItems, key = { it.compositeKey }) { item ->
                    CartItemRow(
                        item = item,
                        onDecrement = { viewModel.updateQuantity(item.compositeKey, -1) },
                        onIncrement = { viewModel.updateQuantity(item.compositeKey, 1) }
                    )
                }

                item { Spacer(modifier = Modifier.height(6.dp)) }

                // Bill Breakdown Card matching prototype lines 370-390
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CremaSurface)
                            .border(1.dp, CremaBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BillRow(
                            label = "Subtotal",
                            value = String.format(Locale.US, "$%.2f", cartBill.subtotal)
                        )
                        BillRow(
                            label = "Delivery Fee",
                            value = String.format(Locale.US, "$%.2f", cartBill.deliveryFee)
                        )
                        BillRow(
                            label = "Est. Tax (8.0%)",
                            value = String.format(Locale.US, "$%.2f", cartBill.tax)
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = CremaBorder,
                            thickness = 1.dp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Payable",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Espresso
                            )
                            Text(
                                text = String.format(Locale.US, "$%.2f", cartBill.totalPayable),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = Terracotta,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    val lineTotal = item.unitPrice * item.quantity

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CremaSurface)
            .border(1.dp, CremaBorder, RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Espresso
            )

            val details = buildString {
                append(item.selectedSize.label)
                if (item.selectedMilk != null && item.selectedMilk.name.isNotEmpty()) {
                    append(" • ")
                    append(item.selectedMilk.name)
                }
                if (item.selectedSugar.isNotEmpty()) {
                    append(" • ")
                    append(item.selectedSugar)
                }
            }
            Text(
                text = details,
                fontSize = 10.sp,
                color = TextMuted,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = String.format(Locale.US, "$%.2f", lineTotal),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Terracotta,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Quantity Stepper
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(CremaSoft)
                .border(1.dp, CremaBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "−",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Espresso,
                modifier = Modifier
                    .clickable(onClick = onDecrement)
                    .padding(horizontal = 4.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = item.quantity.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = Espresso,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "+",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Espresso,
                modifier = Modifier
                    .clickable(onClick = onIncrement)
                    .padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun BillRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextMuted
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Espresso,
            fontFamily = FontFamily.Monospace
        )
    }
}
