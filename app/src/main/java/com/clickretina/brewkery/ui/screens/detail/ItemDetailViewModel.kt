package com.clickretina.brewkery.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clickretina.brewkery.data.repository.BrewkeryRepository
import com.clickretina.brewkery.domain.CartCalculator
import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.domain.model.MenuItem
import com.clickretina.brewkery.domain.model.MilkOption
import com.clickretina.brewkery.domain.model.SizeOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ItemDetailUiState {
    data object Loading : ItemDetailUiState
    data class Success(val item: MenuItem) : ItemDetailUiState
    data class Error(val message: String) : ItemDetailUiState
}

class ItemDetailViewModel(
    private val itemId: Int,
    private val repository: BrewkeryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ItemDetailUiState>(ItemDetailUiState.Loading)
    val uiState: StateFlow<ItemDetailUiState> = _uiState.asStateFlow()

    private val _selectedSize = MutableStateFlow<SizeOption?>(null)
    val selectedSize: StateFlow<SizeOption?> = _selectedSize.asStateFlow()

    private val _selectedMilk = MutableStateFlow<MilkOption?>(null)
    val selectedMilk: StateFlow<MilkOption?> = _selectedMilk.asStateFlow()

    private val _selectedSugar = MutableStateFlow("")
    val selectedSugar: StateFlow<String> = _selectedSugar.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    // Calculated dynamic unit price
    val dynamicUnitPrice: StateFlow<Double> = combine(
        _uiState,
        _selectedSize,
        _selectedMilk
    ) { state, size, milk ->
        if (state is ItemDetailUiState.Success) {
            CartCalculator.calculateUnitPrice(
                basePrice = state.item.basePrice,
                sizeExtraPrice = size?.extraPrice ?: 0.0,
                milkExtraPrice = milk?.extraPrice ?: 0.0
            )
        } else {
            0.0
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Calculated dynamic total price for the selected quantity
    val dynamicTotal: StateFlow<Double> = combine(
        dynamicUnitPrice,
        _quantity
    ) { unitPrice, qty ->
        (unitPrice * qty)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        loadItem()
    }

    fun loadItem() {
        viewModelScope.launch {
            _uiState.value = ItemDetailUiState.Loading
            val result = repository.getItemDetail(itemId)
            result.onSuccess { item ->
                _uiState.value = ItemDetailUiState.Success(item)
                // Set initial defaults matching prototype
                if (item.sizes.isNotEmpty() && _selectedSize.value == null) {
                    _selectedSize.value = item.sizes[0]
                }
                if (item.milkOptions.isNotEmpty() && _selectedMilk.value == null) {
                    _selectedMilk.value = item.milkOptions[0]
                }
                if (item.sugarLevels.isNotEmpty() && _selectedSugar.value.isEmpty()) {
                    _selectedSugar.value = item.sugarLevels[0]
                }
            }.onFailure { throwable ->
                _uiState.value = ItemDetailUiState.Error(
                    throwable.localizedMessage ?: "Failed to load item details"
                )
            }
        }
    }

    fun selectSize(size: SizeOption) {
        _selectedSize.value = size
    }

    fun selectMilk(milk: MilkOption) {
        _selectedMilk.value = milk
    }

    fun selectSugar(sugar: String) {
        _selectedSugar.value = sugar
    }

    fun adjustQuantity(delta: Int) {
        val next = _quantity.value + delta
        if (next >= 1) {
            _quantity.value = next
        }
    }

    fun toggleFavorite() {
        _isFavorite.value = !_isFavorite.value
    }

    fun addToCart(): Boolean {
        val state = _uiState.value as? ItemDetailUiState.Success ?: return false
        val item = state.item
        val size = _selectedSize.value ?: item.sizes.firstOrNull() ?: SizeOption("default", "Regular", 0.0)
        val milk = _selectedMilk.value
        val sugar = _selectedSugar.value.ifEmpty { item.sugarLevels.firstOrNull() ?: "Standard" }

        val key = CartCalculator.generateCompositeKey(
            itemId = item.id,
            sizeId = size.id,
            milkId = milk?.id,
            sugarLevel = sugar
        )

        val unit = CartCalculator.calculateUnitPrice(
            basePrice = item.basePrice,
            sizeExtraPrice = size.extraPrice,
            milkExtraPrice = milk?.extraPrice ?: 0.0
        )

        val cartItem = CartItem(
            compositeKey = key,
            itemId = item.id,
            name = item.name,
            imageUrl = item.imageUrl,
            selectedSize = size,
            selectedMilk = milk,
            selectedSugar = sugar,
            unitPrice = unit,
            quantity = _quantity.value
        )

        repository.addToCart(cartItem)
        return true
    }

    companion object {
        fun provideFactory(itemId: Int, repository: BrewkeryRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ItemDetailViewModel(itemId, repository) as T
                }
            }
    }
}
