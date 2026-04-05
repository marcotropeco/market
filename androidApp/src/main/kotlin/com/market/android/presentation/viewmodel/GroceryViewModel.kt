package com.market.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.shared.domain.model.Category
import com.market.shared.domain.model.GroceryItem
import com.market.shared.domain.usecase.GroceryUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class FilterOption(val label: String) {
    ALL("Todos"),
    PENDING("Pendentes"),
    CHECKED("Feitos")
}

data class GroceryUiState(
    val allItems: List<GroceryItem> = emptyList(),
    val isLoading: Boolean = true,
    val filter: FilterOption = FilterOption.ALL,
    val searchQuery: String = ""
) {
    val filteredItems: List<GroceryItem>
        get() {
            val byFilter = when (filter) {
                FilterOption.ALL     -> allItems
                FilterOption.PENDING -> allItems.filter { !it.isChecked }
                FilterOption.CHECKED -> allItems.filter { it.isChecked }
            }
            return if (searchQuery.isBlank()) byFilter
            else byFilter.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

    val groupedItems: Map<Category, List<GroceryItem>>
        get() = filteredItems
            .sortedWith(compareBy({ it.isChecked }, { it.name }))
            .groupBy { it.category }
            .entries
            .sortedBy { it.key.ordinal }
            .associate { it.key to it.value }

    val checkedCount: Int get() = allItems.count { it.isChecked }
    val totalCount: Int get() = allItems.size
    val progress: Float get() = if (totalCount == 0) 0f else checkedCount / totalCount.toFloat()
}

class GroceryViewModel(private val useCases: GroceryUseCases) : ViewModel() {

    private val _filter = MutableStateFlow(FilterOption.ALL)
    private val _searchQuery = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(true)

    val uiState = combine(
        useCases.getItems(),
        _filter,
        _searchQuery,
        _isLoading
    ) { items, filter, query, loading ->
        GroceryUiState(
            allItems = items,
            isLoading = loading,
            filter = filter,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GroceryUiState()
    )

    init {
        viewModelScope.launch {
            useCases.getItems().collect { _isLoading.value = false }
        }
    }

    fun addItem(
        name: String,
        quantity: Double,
        unit: String,
        category: Category,
        note: String
    ) = viewModelScope.launch {
        useCases.upsertItem(
            GroceryItem(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                quantity = quantity,
                unit = unit,
                category = category,
                isChecked = false,
                note = note.trim(),
                createdAt = System.currentTimeMillis()
            )
        )
    }

    fun updateItem(
        original: GroceryItem,
        name: String,
        quantity: Double,
        unit: String,
        category: Category,
        note: String
    ) = viewModelScope.launch {
        useCases.upsertItem(
            original.copy(
                name = name.trim(),
                quantity = quantity,
                unit = unit,
                category = category,
                note = note.trim()
            )
        )
    }

    fun toggleItem(item: GroceryItem) = viewModelScope.launch {
        useCases.upsertItem(item.copy(isChecked = !item.isChecked))
    }

    fun deleteItem(id: String) = viewModelScope.launch {
        useCases.deleteItem(id)
    }

    fun clearChecked() = viewModelScope.launch {
        useCases.clearChecked()
    }

    fun setFilter(filter: FilterOption) = _filter.update { filter }

    fun setSearchQuery(query: String) = _searchQuery.update { query }
}
