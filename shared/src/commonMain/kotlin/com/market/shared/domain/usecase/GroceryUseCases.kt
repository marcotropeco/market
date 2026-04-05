package com.market.shared.domain.usecase

import com.market.shared.domain.model.GroceryItem
import com.market.shared.domain.repository.GroceryRepository
import kotlinx.coroutines.flow.Flow

class GetGroceryItemsUseCase(private val repository: GroceryRepository) {
    operator fun invoke(): Flow<List<GroceryItem>> = repository.getAllItems()
}

class UpsertGroceryItemUseCase(private val repository: GroceryRepository) {
    suspend operator fun invoke(item: GroceryItem) = repository.upsertItem(item)
}

class DeleteGroceryItemUseCase(private val repository: GroceryRepository) {
    suspend operator fun invoke(id: String) = repository.deleteItem(id)
}

class ClearCheckedItemsUseCase(private val repository: GroceryRepository) {
    suspend operator fun invoke() = repository.clearCheckedItems()
}

data class GroceryUseCases(
    val getItems: GetGroceryItemsUseCase,
    val upsertItem: UpsertGroceryItemUseCase,
    val deleteItem: DeleteGroceryItemUseCase,
    val clearChecked: ClearCheckedItemsUseCase
)
