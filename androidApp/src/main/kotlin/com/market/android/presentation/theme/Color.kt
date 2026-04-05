package com.market.android.presentation.theme

import androidx.compose.ui.graphics.Color
import com.market.shared.domain.model.Category

// Primary — Forest Green
val Green10 = Color(0xFF002204)
val Green20 = Color(0xFF004A0B)
val Green30 = Color(0xFF006E18)
val Green40 = Color(0xFF1E8A2E)
val Green80 = Color(0xFF89D890)
val Green90 = Color(0xFFA4F5AB)

// Secondary — Teal
val Teal10 = Color(0xFF00201C)
val Teal20 = Color(0xFF003731)
val Teal30 = Color(0xFF004F47)
val Teal40 = Color(0xFF00695F)
val Teal80 = Color(0xFF4FDBD0)
val Teal90 = Color(0xFF6FF7EC)

// Tertiary — Amber
val Amber10 = Color(0xFF261A00)
val Amber20 = Color(0xFF432F00)
val Amber30 = Color(0xFF624600)
val Amber40 = Color(0xFF835D00)
val Amber80 = Color(0xFFFFBA3A)
val Amber90 = Color(0xFFFFDC8E)

// Neutral
val Grey10 = Color(0xFF191C1A)
val Grey20 = Color(0xFF2E312E)
val Grey90 = Color(0xFFE1E3DF)
val Grey95 = Color(0xFFF0F1EC)
val Grey99 = Color(0xFFFBFDF7)

// Category accent colors
fun Category.toColor(): Color = when (this) {
    Category.PRODUCE      -> Color(0xFF2E7D32)
    Category.DAIRY        -> Color(0xFF1565C0)
    Category.MEAT         -> Color(0xFFC62828)
    Category.BAKERY       -> Color(0xFFE65100)
    Category.BEVERAGES    -> Color(0xFF6A1B9A)
    Category.FROZEN       -> Color(0xFF00838F)
    Category.PERSONAL_CARE -> Color(0xFFAD1457)
    Category.CLEANING     -> Color(0xFF37474F)
    Category.SNACKS       -> Color(0xFFBF360C)
    Category.OTHER        -> Color(0xFF4E342E)
}

fun Category.toContainerColor(): Color = when (this) {
    Category.PRODUCE      -> Color(0xFFE8F5E9)
    Category.DAIRY        -> Color(0xFFE3F2FD)
    Category.MEAT         -> Color(0xFFFFEBEE)
    Category.BAKERY       -> Color(0xFFFFF3E0)
    Category.BEVERAGES    -> Color(0xFFF3E5F5)
    Category.FROZEN       -> Color(0xFFE0F7FA)
    Category.PERSONAL_CARE -> Color(0xFFFCE4EC)
    Category.CLEANING     -> Color(0xFFECEFF1)
    Category.SNACKS       -> Color(0xFFFBE9E7)
    Category.OTHER        -> Color(0xFFEFEBE9)
}
