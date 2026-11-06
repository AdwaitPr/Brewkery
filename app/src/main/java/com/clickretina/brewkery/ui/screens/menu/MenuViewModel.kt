package com.clickretina.brewkery.ui.screens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.clickretina.brewkery.data.repository.BrewkeryRepository
import com.clickretina.brewkery.domain.model.CartBill
import com.clickretina.brewkery.domain.model.CartItem
import com.clickretina.brewkery.domain.model.Category
import com.clickretina.brewkery.domain.model.MenuItem
import com.clickretina.brewkery.domain.model.OrderTicket
import com.clickretina.brewkery.domain.model.StoreMeta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface MenuUiState {
    data object Loading : MenuUiState
    data class Success(
        val categories: List<Category>,
        val items: List<MenuItem>
    ) : MenuUiState
    data class Error(val message: String) : MenuUiState
}

class MenuViewModel(
    private val repository: BrewkeryRepository
) : ViewModel() {

    val storeMeta: StateFlow<StoreMeta> = repository.storeMeta
    val cartBill: StateFlow<CartBill> = repository.cartBill
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
    val activeOrder: StateFlow<OrderTicket?> = repository.activeOrder

    private data class MenuDataState(
        val isLoading: Boolean = true,
        val errorMessage: String? = null,
        val categories: List<Category> = emptyList(),
        val items: List<MenuItem> = emptyList()
    )

    private val _menuData = MutableStateFlow(MenuDataState())

    private val _selectedCategoryId = MutableStateFlow("all")
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<MenuUiState> = combine(
        _menuData,
        _selectedCategoryId,
        _searchQuery
    ) { data, selectedCategory, query ->
        when {
            data.isLoading -> MenuUiState.Loading
            data.errorMessage != null -> MenuUiState.Error(data.errorMessage)
            else -> {
                val filtered = data.items.filter { item ->
                    val matchesCategory = selectedCategory == "all" || item.categoryId == selectedCategory
                    val matchesQuery = query.isBlank() ||
                            item.name.contains(query, ignoreCase = true) ||
                            item.tagline.contains(query, ignoreCase = true) ||
                            item.description.contains(query, ignoreCase = true)
                    matchesCategory && matchesQuery
                }
                MenuUiState.Success(
                    categories = data.categories,
                    items = filtered
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MenuUiState.Loading)

    init {
        loadMenu()
    }

    fun loadMenu() {
        viewModelScope.launch {
            _menuData.value = _menuData.value.copy(isLoading = true, errorMessage = null)
            val result = repository.getMenuData()
            result.onSuccess { (categories, items) ->
                _menuData.value = MenuDataState(
                    isLoading = false,
                    errorMessage = null,
                    categories = categories,
                    items = items
                )
            }.onFailure { throwable ->
                _menuData.value = _menuData.value.copy(
                    isLoading = false,
                    errorMessage = throwable.localizedMessage ?: "Failed to load menu"
                )
            }
        }
    }

    fun onCategorySelect(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    companion object {
        fun provideFactory(repository: BrewkeryRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MenuViewModel(repository) as T
                }
            }
    }
}
