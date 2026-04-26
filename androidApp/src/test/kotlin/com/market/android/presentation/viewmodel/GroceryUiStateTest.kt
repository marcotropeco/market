package com.market.android.presentation.viewmodel

import com.market.shared.domain.model.Category
import com.market.shared.domain.model.GroceryItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GroceryUiStateTest {

    // ── filteredItems ────────────────────────────────────────────────

    @Test
    fun `filteredItems com ALL retorna todos os itens`() {
        val state = stateWith(
            item("1", checked = false),
            item("2", checked = true)
        )
        assertEquals(2, state.filteredItems.size)
    }

    @Test
    fun `filteredItems com PENDING retorna apenas nao marcados`() {
        val state = stateWith(
            item("1", checked = false),
            item("2", checked = true),
            filter = FilterOption.PENDING
        )
        assertEquals(1, state.filteredItems.size)
        assertTrue(state.filteredItems.none { it.isChecked })
    }

    @Test
    fun `filteredItems com CHECKED retorna apenas marcados`() {
        val state = stateWith(
            item("1", checked = false),
            item("2", checked = true),
            item("3", checked = true),
            filter = FilterOption.CHECKED
        )
        assertEquals(2, state.filteredItems.size)
        assertTrue(state.filteredItems.all { it.isChecked })
    }

    @Test
    fun `filteredItems com searchQuery filtra por nome case insensitive`() {
        val state = stateWith(
            item("1", name = "Leite Integral"),
            item("2", name = "Pão Francês"),
            item("3", name = "Leite Condensado"),
            searchQuery = "leite"
        )
        assertEquals(2, state.filteredItems.size)
        assertTrue(state.filteredItems.all { it.name.contains("Leite", ignoreCase = true) })
    }

    @Test
    fun `filteredItems combina filtro e busca`() {
        val state = stateWith(
            item("1", name = "Leite", checked = false),
            item("2", name = "Leite", checked = true),
            item("3", name = "Pão", checked = false),
            filter = FilterOption.PENDING,
            searchQuery = "leite"
        )
        assertEquals(1, state.filteredItems.size)
        assertEquals("Leite", state.filteredItems[0].name)
        assertTrue(!state.filteredItems[0].isChecked)
    }

    @Test
    fun `filteredItems com searchQuery vazia nao filtra`() {
        val state = stateWith(item("1"), item("2"), item("3"), searchQuery = "")
        assertEquals(3, state.filteredItems.size)
    }

    // ── groupedItems ─────────────────────────────────────────────────

    @Test
    fun `groupedItems agrupa por categoria`() {
        val state = stateWith(
            item("1", category = Category.DAIRY),
            item("2", category = Category.DAIRY),
            item("3", category = Category.BAKERY)
        )
        assertEquals(2, state.groupedItems.keys.size)
        assertEquals(2, state.groupedItems[Category.DAIRY]?.size)
        assertEquals(1, state.groupedItems[Category.BAKERY]?.size)
    }

    @Test
    fun `groupedItems reflete itens filtrados`() {
        val state = stateWith(
            item("1", category = Category.DAIRY, checked = false),
            item("2", category = Category.DAIRY, checked = true),
            filter = FilterOption.PENDING
        )
        assertEquals(1, state.groupedItems[Category.DAIRY]?.size)
    }

    // ── progress ─────────────────────────────────────────────────────

    @Test
    fun `progress e zero quando lista vazia`() {
        val state = GroceryUiState()
        assertEquals(0f, state.progress)
    }

    @Test
    fun `progress e zero quando nenhum marcado`() {
        val state = stateWith(item("1", checked = false), item("2", checked = false))
        assertEquals(0f, state.progress)
    }

    @Test
    fun `progress e 1 quando todos marcados`() {
        val state = stateWith(item("1", checked = true), item("2", checked = true))
        assertEquals(1f, state.progress)
    }

    @Test
    fun `progress e 0_5 quando metade marcada`() {
        val state = stateWith(item("1", checked = true), item("2", checked = false))
        assertEquals(0.5f, state.progress)
    }

    // ── checkedCount / totalCount ─────────────────────────────────────

    @Test
    fun `checkedCount conta apenas marcados`() {
        val state = stateWith(
            item("1", checked = true),
            item("2", checked = false),
            item("3", checked = true)
        )
        assertEquals(2, state.checkedCount)
        assertEquals(3, state.totalCount)
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private fun item(
        id: String,
        name: String = "Item $id",
        checked: Boolean = false,
        category: Category = Category.OTHER
    ) = GroceryItem(id = id, name = name, quantity = 1.0, unit = "un",
        category = category, isChecked = checked, note = "", createdAt = 0L)

    private fun stateWith(
        vararg items: GroceryItem,
        filter: FilterOption = FilterOption.ALL,
        searchQuery: String = ""
    ) = GroceryUiState(allItems = items.toList(), isLoading = false,
        filter = filter, searchQuery = searchQuery)
}
