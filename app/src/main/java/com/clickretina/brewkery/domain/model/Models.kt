package com.clickretina.brewkery.domain.model

data class StoreMeta(
    val app: String = "Brewkery",
    val version: String = "1.0.0",
    val tagline: String = "Artisanal Coffee & Fresh Oven Bakes",
    val currency: String = "USD",
    val currencySymbol: String = "$",
    val deliveryFee: Double = 2.50,
    val taxRatePercent: Double = 8.0,
    val estimatedDeliveryTime: String = "20 - 30 mins"
)

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val itemCount: Int
)

data class SizeOption(
    val id: String,
    val label: String,
    val extraPrice: Double = 0.0
)

data class MilkOption(
    val id: String,
    val name: String,
    val extraPrice: Double = 0.0
)

data class MenuItem(
    val id: Int,
    val categoryId: String,
    val name: String,
    val tagline: String,
    val description: String,
    val basePrice: Double,
    val rating: Double,
    val reviewCount: Int,
    val prepTime: String,
    val calories: Int,
    val imageUrl: String,
    val badge: String? = null,
    val ingredients: List<String> = emptyList(),
    val sizes: List<SizeOption> = emptyList(),
    val sugarLevels: List<String> = emptyList(),
    val milkOptions: List<MilkOption> = emptyList()
)

data class CartItem(
    val compositeKey: String,
    val itemId: Int,
    val name: String,
    val imageUrl: String,
    val selectedSize: SizeOption,
    val selectedMilk: MilkOption? = null,
    val selectedSugar: String = "",
    val unitPrice: Double,
    val quantity: Int = 1
)

data class CartBill(
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val tax: Double = 0.0,
    val totalPayable: Double = 0.0,
    val totalItemCount: Int = 0
)

data class OrderTicket(
    val orderId: String,
    val status: String = "PREPARING",
    val estimatedWait: String = "20 - 30 minutes",
    val itemCount: Int,
    val totalAmount: Double,
    val timestamp: Long = System.currentTimeMillis()
)
