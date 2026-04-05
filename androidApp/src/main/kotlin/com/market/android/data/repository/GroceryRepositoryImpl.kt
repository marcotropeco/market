package com.market.android.data.repository

import com.market.android.data.local.GroceryDatabase
import com.market.android.data.local.GroceryItemEntity
import com.market.shared.domain.model.Category
import com.market.shared.domain.model.GroceryItem
import com.market.shared.domain.repository.GroceryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GroceryRepositoryImpl(database: GroceryDatabase) : GroceryRepository {

    private val dao = database.groceryItemDao()

    override fun getAllItems(): Flow<List<GroceryItem>> =
        dao.getAllItems().map { list -> list.map { it.toDomain() } }

    override suspend fun getItemById(id: String): GroceryItem? =
        dao.getItemById(id)?.toDomain()

    override suspend fun upsertItem(item: GroceryItem) =
        dao.upsertItem(item.toEntity())

    override suspend fun deleteItem(id: String) =
        dao.deleteItem(id)

    override suspend fun clearCheckedItems() =
        dao.clearCheckedItems()

    private fun GroceryItemEntity.toDomain() = GroceryItem(
        id = id,
        name = name,
        quantity = quantity,
        unit = unit,
        category = Category.valueOf(category),
        isChecked = isChecked,
        note = note,
        createdAt = createdAt
    )

    private fun GroceryItem.toEntity() = GroceryItemEntity(
        id = id,
        name = name,
        quantity = quantity,
        unit = unit,
        category = category.name,
        isChecked = isChecked,
        note = note,
        createdAt = createdAt
    )
}
