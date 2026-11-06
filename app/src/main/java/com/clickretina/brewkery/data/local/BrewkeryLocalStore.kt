package com.clickretina.brewkery.data.local

import android.content.Context
import android.content.SharedPreferences
import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.domain.model.OrderTicket
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Clean Gson-backed local storage manager persisting the user's in-progress Cart
 * and active Order Ticket across application restarts without KSP/Kapt conflicts.
 */
class BrewkeryLocalStore(
    context: Context,
    private val gson: Gson = Gson()
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveCart(items: List<CartItem>) {
        val json = gson.toJson(items)
        prefs.edit().putString(KEY_CART, json).apply()
    }

    fun getCart(): List<CartItem> {
        val json = prefs.getString(KEY_CART, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<CartItem>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun clearCart() {
        prefs.edit().remove(KEY_CART).apply()
    }

    fun saveActiveOrder(order: OrderTicket?) {
        if (order == null) {
            prefs.edit().remove(KEY_ACTIVE_ORDER).apply()
        } else {
            val json = gson.toJson(order)
            prefs.edit().putString(KEY_ACTIVE_ORDER, json).apply()
        }
    }

    fun getActiveOrder(): OrderTicket? {
        val json = prefs.getString(KEY_ACTIVE_ORDER, null) ?: return null
        return try {
            gson.fromJson(json, OrderTicket::class.java)
        } catch (_: Exception) {
            null
        }
    }

    fun clearActiveOrder() {
        prefs.edit().remove(KEY_ACTIVE_ORDER).apply()
    }

    companion object {
        private const val PREFS_NAME = "brewkery_preferences"
        private const val KEY_CART = "key_cart_items"
        private const val KEY_ACTIVE_ORDER = "key_active_order"
    }
}
