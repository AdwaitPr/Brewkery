package com.clickretina.brewkery.domain

import com.clickretina.brewkery.domain.model.CartBill
import com.clickretina.brewkery.domain.model.CartItem
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Pure Kotlin Cart Calculator with zero Android framework dependencies.
 * Implements exact business math matching the web prototype using BigDecimal (HALF_UP, 2 decimal places).
 */
object CartCalculator {

    val FLAT_DELIVERY_FEE: BigDecimal = BigDecimal("2.50")
    val TAX_RATE_PERCENT: BigDecimal = BigDecimal("0.08") // 8.0%

    /**
     * Deterministic composite key generator combining itemId, size, milk/spread option, and sugar/serving level.
     */
    fun generateCompositeKey(
        itemId: Int,
        sizeId: String,
        milkId: String?,
        sugarLevel: String
    ): String {
        val sanitizedMilk = milkId?.trim()?.ifEmpty { "default" } ?: "none"
        val sanitizedSugar = sugarLevel.trim().ifEmpty { "standard" }
        return "${itemId}_${sizeId.trim()}_${sanitizedMilk}_${sanitizedSugar}"
    }

    /**
     * Calculates the dynamic unit price for a customized item: basePrice + modifiers.
     */
    fun calculateUnitPrice(
        basePrice: Double,
        sizeExtraPrice: Double = 0.0,
        milkExtraPrice: Double = 0.0
    ): Double {
        val base = BigDecimal.valueOf(basePrice)
        val sizeExtra = BigDecimal.valueOf(sizeExtraPrice)
        val milkExtra = BigDecimal.valueOf(milkExtraPrice)
        return base.add(sizeExtra).add(milkExtra)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }

    /**
     * Calculates the comprehensive cart bill including subtotal, delivery fee, 8% tax, and total.
     * When cart is empty: subtotal = 0, delivery = 0, tax = 0, total = 0.
     * When cart is non-empty: flat delivery = $2.50, tax = subtotal * 0.08 (HALF_UP scale 2).
     */
    fun calculateBill(items: List<CartItem>): CartBill {
        if (items.isEmpty()) {
            return CartBill(
                subtotal = 0.0,
                deliveryFee = 0.0,
                tax = 0.0,
                totalPayable = 0.0,
                totalItemCount = 0
            )
        }

        var subtotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
        var totalCount = 0

        for (item in items) {
            val unit = BigDecimal.valueOf(item.unitPrice).setScale(2, RoundingMode.HALF_UP)
            val lineTotal = unit.multiply(BigDecimal.valueOf(item.quantity.toLong()))
            subtotal = subtotal.add(lineTotal)
            totalCount += item.quantity
        }

        val delivery = FLAT_DELIVERY_FEE.setScale(2, RoundingMode.HALF_UP)
        val tax = subtotal.multiply(TAX_RATE_PERCENT).setScale(2, RoundingMode.HALF_UP)
        val totalPayable = subtotal.add(delivery).add(tax).setScale(2, RoundingMode.HALF_UP)

        return CartBill(
            subtotal = subtotal.toDouble(),
            deliveryFee = delivery.toDouble(),
            tax = tax.toDouble(),
            totalPayable = totalPayable.toDouble(),
            totalItemCount = totalCount
        )
    }

    /**
     * Merges a new item into the cart list.
     * If an item with identical compositeKey exists, increments quantity;
     * otherwise appends as a new line item.
     */
    fun addItem(currentItems: List<CartItem>, newItem: CartItem): List<CartItem> {
        val existingIndex = currentItems.indexOfFirst { it.compositeKey == newItem.compositeKey }
        return if (existingIndex >= 0) {
            currentItems.mapIndexed { index, item ->
                if (index == existingIndex) {
                    item.copy(quantity = item.quantity + newItem.quantity)
                } else {
                    item
                }
            }
        } else {
            currentItems + newItem
        }
    }

    /**
     * Updates quantity by delta; removes item completely when quantity drops below 1.
     */
    fun updateQuantity(
        currentItems: List<CartItem>,
        compositeKey: String,
        delta: Int
    ): List<CartItem> {
        return currentItems.mapNotNull { item ->
            if (item.compositeKey == compositeKey) {
                val newQty = item.quantity + delta
                if (newQty > 0) item.copy(quantity = newQty) else null
            } else {
                item
            }
        }
    }
}
