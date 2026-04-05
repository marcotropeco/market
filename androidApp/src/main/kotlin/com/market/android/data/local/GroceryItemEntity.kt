package com.market.android.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grocery_items")
data class GroceryItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val category: String,
    val isChecked: Boolean,
    val note: String,
    val createdAt: Long
)
