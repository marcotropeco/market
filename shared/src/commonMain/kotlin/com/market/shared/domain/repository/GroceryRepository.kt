package com.market.shared.domain.repository

import com.market.shared.domain.model.GroceryItem
import kotlinx.coroutines.flow.Flow

interface GroceryRepository {
    fun getAllItems(): Flow<List<GroceryItem>>
    suspend fun getItemById(id: String): GroceryItem?
    suspend fun upsertItem(item: GroceryItem)
    suspend fun deleteItem(id: String)
    suspend fun clearCheckedItems()
}
