package com.clickretina.brewkery.domain

import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.domain.model.MilkOption
import com.clickretina.brewkery.domain.model.SizeOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CartCalculatorTest {

    @Test
    fun testExactPrototypeBillCalculation() {
        // Item 1: Toasted Caramel Macchiato - Grande (12 oz, +0.65) + Oat Milk (0.00) + 50% Mild
        // Base 4.85 + 0.65 = 5.50
        val macchiatoSize = SizeOption(id = "sz_medium", label = "Grande (12 oz)", extraPrice = 0.65)
        val macchiatoMilk = MilkOption(id = "m_oat", name = "Oat Milk (Barista Blend)", extraPrice = 0.00)
        val macchiatoPrice = CartCalculator.calculateUnitPrice(4.85, macchiatoSize.extraPrice, macchiatoMilk.extraPrice)
        assertEquals(5.50, macchiatoPrice, 0.001)

        val macchiatoKey = CartCalculator.generateCompositeKey(1, macchiatoSize.id, macchiatoMilk.id, "50% Mild")
        val item1 = CartItem(
            compositeKey = macchiatoKey,
            itemId = 1,
            name = "Toasted Caramel Macchiato",
            imageUrl = "",
            selectedSize = macchiatoSize,
            selectedMilk = macchiatoMilk,
            selectedSugar = "50% Mild",
            unitPrice = macchiatoPrice,
            quantity = 1
        )

        // Item 3: Golden Normandy Croissant - Single Piece (+0.00) + Classic No Spread (0.00) + Warm & Crisp
        // Base 3.90 + 0.00 = 3.90
        val croissantSize = SizeOption(id = "sz_single", label = "Single Piece", extraPrice = 0.00)
        val croissantMilk = MilkOption(id = "top_plain", name = "Classic (No Spread)", extraPrice = 0.00)
        val croissantPrice = CartCalculator.calculateUnitPrice(3.90, croissantSize.extraPrice, croissantMilk.extraPrice)
        assertEquals(3.90, croissantPrice, 0.001)

        val croissantKey = CartCalculator.generateCompositeKey(3, croissantSize.id, croissantMilk.id, "Warm & Crisp (Recommended)")
        val item2 = CartItem(
            compositeKey = croissantKey,
            itemId = 3,
            name = "Golden Normandy Croissant",
            imageUrl = "",
            selectedSize = croissantSize,
            selectedMilk = croissantMilk,
            selectedSugar = "Warm & Crisp (Recommended)",
            unitPrice = croissantPrice,
            quantity = 1
        )

        val bill = CartCalculator.calculateBill(listOf(item1, item2))

        // Exactly matches prototype HTML lines 375-385:
        // Subtotal: $9.40, Delivery: $2.50, Est. Tax: $0.75, Total: $12.65
        assertEquals(9.40, bill.subtotal, 0.001)
        assertEquals(2.50, bill.deliveryFee, 0.001)
        assertEquals(0.75, bill.tax, 0.001)
        assertEquals(12.65, bill.totalPayable, 0.001)
        assertEquals(2, bill.totalItemCount)
    }

    @Test
    fun testDynamicUnitPriceWithModifiers() {
        // Base 4.85 + Venti (1.25) + Almond Milk (0.50) = 6.60
        val unitPrice = CartCalculator.calculateUnitPrice(
            basePrice = 4.85,
            sizeExtraPrice = 1.25,
            milkExtraPrice = 0.50
        )
        assertEquals(6.60, unitPrice, 0.001)
    }

    @Test
    fun testCompositeKeyCollisionPreventionAndQuantityIncrement() {
        val sizeTall = SizeOption("sz_small", "Tall (8 oz)", 0.0)
        val sizeGrande = SizeOption("sz_medium", "Grande (12 oz)", 0.65)
        val milkOat = MilkOption("m_oat", "Oat Milk", 0.0)

        val key1 = CartCalculator.generateCompositeKey(1, sizeTall.id, milkOat.id, "0% Unsweetened")
        val key2 = CartCalculator.generateCompositeKey(1, sizeGrande.id, milkOat.id, "0% Unsweetened")
        val key3 = CartCalculator.generateCompositeKey(1, sizeTall.id, milkOat.id, "100% Standard Sweet")

        // Ensure keys differ when any customization differs
        assertTrue(key1 != key2)
        assertTrue(key1 != key3)
        assertTrue(key2 != key3)

        // Adding identical item increments quantity
        val itemA = CartItem(
            compositeKey = key1,
            itemId = 1,
            name = "Coffee",
            imageUrl = "",
            selectedSize = sizeTall,
            selectedMilk = milkOat,
            selectedSugar = "0% Unsweetened",
            unitPrice = 4.85,
            quantity = 1
        )

        var list = CartCalculator.addItem(emptyList(), itemA)
        assertEquals(1, list.size)
        assertEquals(1, list[0].quantity)

        val itemASecond = itemA.copy(quantity = 2)
        list = CartCalculator.addItem(list, itemASecond)
        assertEquals(1, list.size)
        assertEquals(3, list[0].quantity)

        // Adding different item creates new row
        val itemB = CartItem(
            compositeKey = key2,
            itemId = 1,
            name = "Coffee",
            imageUrl = "",
            selectedSize = sizeGrande,
            selectedMilk = milkOat,
            selectedSugar = "0% Unsweetened",
            unitPrice = 5.50,
            quantity = 1
        )
        list = CartCalculator.addItem(list, itemB)
        assertEquals(2, list.size)
    }

    @Test
    fun testQuantityDecrementAndRemoval() {
        val size = SizeOption("sz_small", "Tall", 0.0)
        val key = CartCalculator.generateCompositeKey(1, size.id, null, "standard")
        val item = CartItem(
            compositeKey = key,
            itemId = 1,
            name = "Coffee",
            imageUrl = "",
            selectedSize = size,
            selectedMilk = null,
            selectedSugar = "standard",
            unitPrice = 4.00,
            quantity = 2
        )

        var list = listOf(item)
        list = CartCalculator.updateQuantity(list, key, -1)
        assertEquals(1, list.size)
        assertEquals(1, list[0].quantity)

        // Decrement below 1 removes from cart
        list = CartCalculator.updateQuantity(list, key, -1)
        assertTrue(list.isEmpty())
    }

    @Test
    fun testEmptyCartCalculation() {
        val emptyBill = CartCalculator.calculateBill(emptyList())
        assertEquals(0.0, emptyBill.subtotal, 0.001)
        assertEquals(0.0, emptyBill.deliveryFee, 0.001)
        assertEquals(0.0, emptyBill.tax, 0.001)
        assertEquals(0.0, emptyBill.totalPayable, 0.001)
        assertEquals(0, emptyBill.totalItemCount)
    }
}
