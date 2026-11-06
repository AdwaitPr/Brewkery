package com.clickretina.brewkery.data.remote.dto

import com.clickretina.brewkery.domain.model.Category
import com.clickretina.brewkery.domain.model.MenuItem
import com.clickretina.brewkery.domain.model.MilkOption
import com.clickretina.brewkery.domain.model.SizeOption
import com.clickretina.brewkery.domain.model.StoreMeta
import com.google.gson.annotations.SerializedName

data class MenuResponseDto(
    @SerializedName("meta") val meta: StoreMetaDto? = null,
    @SerializedName("categories") val categories: List<CategoryDto>? = null,
    @SerializedName("items") val items: List<MenuItemDto>? = null
)

data class StoreMetaDto(
    @SerializedName("app") val app: String? = null,
    @SerializedName("version") val version: String? = null,
    @SerializedName("tagline") val tagline: String? = null,
    @SerializedName("currency") val currency: String? = null,
    @SerializedName("currency_symbol") val currencySymbol: String? = null,
    @SerializedName("delivery_fee") val deliveryFee: Double? = null,
    @SerializedName("tax_rate_percent") val taxRatePercent: Double? = null,
    @SerializedName("estimated_delivery_time") val estimatedDeliveryTime: String? = null
) {
    fun toDomain(): StoreMeta {
        return StoreMeta(
            app = app ?: "Brewkery",
            version = version ?: "1.0.0",
            tagline = tagline ?: "Artisanal Coffee & Fresh Oven Bakes",
            currency = currency ?: "USD",
            currencySymbol = currencySymbol ?: "$",
            deliveryFee = deliveryFee ?: 2.50,
            taxRatePercent = taxRatePercent ?: 8.0,
            estimatedDeliveryTime = estimatedDeliveryTime ?: "20 - 30 mins"
        )
    }
}

data class CategoryDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("item_count") val itemCount: Int? = null
) {
    fun toDomain(): Category {
        return Category(
            id = id.orEmpty(),
            name = name.orEmpty(),
            icon = icon ?: "☕",
            itemCount = itemCount ?: 0
        )
    }
}

data class MenuItemDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("category_id") val categoryId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("tagline") val tagline: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("base_price") val basePrice: Double? = null,
    @SerializedName("rating") val rating: Double? = null,
    @SerializedName("review_count") val reviewCount: Int? = null,
    @SerializedName("prep_time") val prepTime: String? = null,
    @SerializedName("calories") val calories: Int? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("badge") val badge: String? = null,
    @SerializedName("ingredients") val ingredients: List<String>? = null,
    @SerializedName("customizations") val customizations: CustomizationsDto? = null
) {
    fun toDomain(): MenuItem {
        return MenuItem(
            id = id,
            categoryId = categoryId.orEmpty(),
            name = name.orEmpty(),
            tagline = tagline.orEmpty(),
            description = description.orEmpty(),
            basePrice = basePrice ?: 0.0,
            rating = rating ?: 0.0,
            reviewCount = reviewCount ?: 0,
            prepTime = prepTime ?: "Ready to serve",
            calories = calories ?: 0,
            imageUrl = imageUrl.orEmpty(),
            badge = badge,
            ingredients = ingredients ?: emptyList(),
            sizes = customizations?.sizes?.map { it.toDomain() } ?: emptyList(),
            sugarLevels = customizations?.sugarLevels ?: emptyList(),
            milkOptions = customizations?.milkOptions?.map { it.toDomain() } ?: emptyList()
        )
    }
}

data class CustomizationsDto(
    @SerializedName("sizes") val sizes: List<SizeOptionDto>? = null,
    @SerializedName("sugar_levels") val sugarLevels: List<String>? = null,
    @SerializedName("milk_options") val milkOptions: List<MilkOptionDto>? = null
)

data class SizeOptionDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("label") val label: String? = null,
    @SerializedName("extra_price") val extraPrice: Double? = null
) {
    fun toDomain(): SizeOption {
        return SizeOption(
            id = id.orEmpty(),
            label = label.orEmpty(),
            extraPrice = extraPrice ?: 0.0
        )
    }
}

data class MilkOptionDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("extra_price") val extraPrice: Double? = null
) {
    fun toDomain(): MilkOption {
        return MilkOption(
            id = id.orEmpty(),
            name = name.orEmpty(),
            extraPrice = extraPrice ?: 0.0
        )
    }
}
