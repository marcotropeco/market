package com.market.android

import com.market.shared.domain.model.GroceryItem
import com.market.shared.domain.repository.GroceryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeGroceryRepository : GroceryRepository {

    private val _items = MutableStateFlow<List<GroceryItem>>(emptyList())

    override fun getAllItems(): Flow<List<GroceryItem>> = _items.asStateFlow()

    override suspend fun getItemById(id: String): GroceryItem? =
        _items.value.find { it.id == id }

    override suspend fun upsertItem(item: GroceryItem) {
        val current = _items.value.toMutableList()
        val index = current.indexOfFirst { it.id == item.id }
        if (index >= 0) current[index] = item else current.add(item)
        _items.value = current
    }

    override suspend fun deleteItem(id: String) {
        _items.value = _items.value.filter { it.id != id }
    }

    override suspend fun clearCheckedItems() {
        _items.value = _items.value.filter { !it.isChecked }
    }
}
