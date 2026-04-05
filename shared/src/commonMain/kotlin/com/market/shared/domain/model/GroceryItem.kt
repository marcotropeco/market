package com.market.shared.domain.model

data class GroceryItem(
    val id: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val category: Category,
    val isChecked: Boolean,
    val note: String,
    val createdAt: Long
)
