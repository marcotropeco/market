package com.market.android.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryItemDao {

    @Query("SELECT * FROM grocery_items ORDER BY isChecked ASC, category ASC, name ASC")
    fun getAllItems(): Flow<List<GroceryItemEntity>>

    @Query("SELECT * FROM grocery_items WHERE id = :id")
    suspend fun getItemById(id: String): GroceryItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertItem(item: GroceryItemEntity)

    @Query("DELETE FROM grocery_items WHERE id = :id")
    suspend fun deleteItem(id: String)

    @Query("DELETE FROM grocery_items WHERE isChecked = 1")
    suspend fun clearCheckedItems()
}
