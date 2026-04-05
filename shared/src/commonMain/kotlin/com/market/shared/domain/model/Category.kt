package com.market.shared.domain.model

enum class Category(val label: String, val emoji: String) {
    PRODUCE("Hortifruti", "🥦"),
    DAIRY("Laticínios", "🥛"),
    MEAT("Carnes", "🥩"),
    BAKERY("Padaria", "🍞"),
    BEVERAGES("Bebidas", "🥤"),
    FROZEN("Congelados", "🧊"),
    PERSONAL_CARE("Higiene", "🧴"),
    CLEANING("Limpeza", "🧹"),
    SNACKS("Snacks", "🍿"),
    OTHER("Outros", "🛒")
}
