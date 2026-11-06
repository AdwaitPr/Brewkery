package com.clickretina.brewkery.data.repository

import com.clickretina.brewkery.data.local.BrewkeryLocalStore
import com.clickretina.brewkery.data.remote.BrewkeryApiService
import com.clickretina.brewkery.domain.CartCalculator
import com.clickretina.brewkery.domain.model.CartBill
import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.domain.model.Category
import com.clickretina.brewkery.domain.model.MenuItem
import com.clickretina.brewkery.domain.model.OrderTicket
import com.clickretina.brewkery.domain.model.StoreMeta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class BrewkeryRepository(
    private val apiService: BrewkeryApiService,
    private val localStore: BrewkeryLocalStore
) {
    private val _storeMeta = MutableStateFlow(StoreMeta())
    val storeMeta: StateFlow<StoreMeta> = _storeMeta.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(localStore.getCart())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cartBill = MutableStateFlow(CartCalculator.calculateBill(_cartItems.value))
    val cartBill: StateFlow<CartBill> = _cartBill.asStateFlow()

    private val _activeOrder = MutableStateFlow<OrderTicket?>(localStore.getActiveOrder())
    val activeOrder: StateFlow<OrderTicket?> = _activeOrder.asStateFlow()

    suspend fun getMenuData(): Result<Pair<List<Category>, List<MenuItem>>> {
        return try {
            val response = apiService.getMenuData()
            response.meta?.let { _storeMeta.value = it.toDomain() }
            val categories = response.categories?.map { it.toDomain() } ?: emptyList()
            val items = response.items?.map { it.toDomain() } ?: emptyList()
            Result.success(Pair(categories, items))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getItemDetail(id: Int): Result<MenuItem> {
        return try {
            val dto = apiService.getItemDetail(id)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun addToCart(newItem: CartItem) {
        val updated = CartCalculator.addItem(_cartItems.value, newItem)
        updateCartState(updated)
    }

    fun updateCartQuantity(compositeKey: String, delta: Int) {
        val updated = CartCalculator.updateQuantity(_cartItems.value, compositeKey, delta)
        updateCartState(updated)
    }

    fun clearCart() {
        updateCartState(emptyList())
    }

    private fun updateCartState(newItems: List<CartItem>) {
        _cartItems.value = newItems
        _cartBill.value = CartCalculator.calculateBill(newItems)
        localStore.saveCart(newItems)
    }

    fun placeOrder(): OrderTicket? {
        val currentItems = _cartItems.value
        if (currentItems.isEmpty()) return null

        val bill = _cartBill.value
        val code = "#BK-${Random.nextInt(10000, 100000)}"
        val ticket = OrderTicket(
            orderId = code,
            status = "PREPARING",
            estimatedWait = "20 - 30 minutes",
            itemCount = bill.totalItemCount,
            totalAmount = bill.totalPayable,
            timestamp = System.currentTimeMillis()
        )

        _activeOrder.value = ticket
        localStore.saveActiveOrder(ticket)
        clearCart()
        return ticket
    }
}
