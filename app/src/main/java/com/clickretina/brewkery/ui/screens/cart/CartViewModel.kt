package com.clickretina.brewkery.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clickretina.brewkery.data.repository.BrewkeryRepository
import com.clickretina.brewkery.domain.model.CartBill
import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.domain.model.OrderTicket
import kotlinx.coroutines.flow.StateFlow

class CartViewModel(
    private val repository: BrewkeryRepository
) : ViewModel() {

    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
    val cartBill: StateFlow<CartBill> = repository.cartBill

    fun updateQuantity(compositeKey: String, delta: Int) {
        repository.updateCartQuantity(compositeKey, delta)
    }

    fun clearCart() {
        repository.clearCart()
    }

    fun placeOrder(): OrderTicket? {
        return repository.placeOrder()
    }

    companion object {
        fun provideFactory(repository: BrewkeryRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CartViewModel(repository) as T
                }
            }
    }
}
