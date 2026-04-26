package com.market.shared.domain.usecase

import com.market.shared.FakeGroceryRepository
import com.market.shared.domain.model.Category
import com.market.shared.domain.model.GroceryItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GroceryUseCasesTest {

    private lateinit var repository: FakeGroceryRepository
    private lateinit var getItems: GetGroceryItemsUseCase
    private lateinit var upsertItem: UpsertGroceryItemUseCase
    private lateinit var deleteItem: DeleteGroceryItemUseCase
    private lateinit var clearChecked: ClearCheckedItemsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeGroceryRepository()
        getItems = GetGroceryItemsUseCase(repository)
        upsertItem = UpsertGroceryItemUseCase(repository)
        deleteItem = DeleteGroceryItemUseCase(repository)
        clearChecked = ClearCheckedItemsUseCase(repository)
    }

    // ── GetGroceryItemsUseCase ────────────────────────────────────────

    @Test
    fun `getItems emits lista vazia inicialmente`() = runTest {
        val items = getItems().first()
        assertTrue(items.isEmpty())
    }

    @Test
    fun `getItems emite atualizacao apos upsert`() = runTest {
        upsertItem(item("1", "Leite"))
        val items = getItems().first()
        assertEquals(1, items.size)
        assertEquals("Leite", items[0].name)
    }

    // ── UpsertGroceryItemUseCase ──────────────────────────────────────

    @Test
    fun `upsertItem adiciona novo item`() = runTest {
        upsertItem(item("1", "Leite"))
        val items = getItems().first()
        assertEquals(1, items.size)
    }

    @Test
    fun `upsertItem atualiza item existente pelo id`() = runTest {
        upsertItem(item("1", "Leite"))
        upsertItem(item("1", "Leite Integral"))
        val items = getItems().first()
        assertEquals(1, items.size)
        assertEquals("Leite Integral", items[0].name)
    }

    @Test
    fun `upsertItem preserva outros itens ao atualizar`() = runTest {
        upsertItem(item("1", "Leite"))
        upsertItem(item("2", "Pão"))
        upsertItem(item("1", "Leite Integral"))
        val items = getItems().first()
        assertEquals(2, items.size)
    }

    // ── DeleteGroceryItemUseCase ──────────────────────────────────────

    @Test
    fun `deleteItem remove item pelo id`() = runTest {
        upsertItem(item("1", "Leite"))
        upsertItem(item("2", "Pão"))
        deleteItem("1")
        val items = getItems().first()
        assertEquals(1, items.size)
        assertEquals("Pão", items[0].name)
    }

    @Test
    fun `deleteItem nao afeta outros itens`() = runTest {
        upsertItem(item("1", "Leite"))
        upsertItem(item("2", "Pão"))
        upsertItem(item("3", "Manteiga"))
        deleteItem("2")
        val items = getItems().first()
        assertEquals(2, items.size)
        assertFalse(items.any { it.id == "2" })
    }

    @Test
    fun `deleteItem de id inexistente nao causa erro`() = runTest {
        upsertItem(item("1", "Leite"))
        deleteItem("999")
        assertEquals(1, getItems().first().size)
    }

    @Test
    fun `getItemById retorna item correto`() = runTest {
        upsertItem(item("1", "Leite"))
        upsertItem(item("2", "Pão"))
        val found = repository.getItemById("2")
        assertEquals("Pão", found?.name)
    }

    @Test
    fun `getItemById retorna null para id inexistente`() = runTest {
        upsertItem(item("1", "Leite"))
        assertNull(repository.getItemById("999"))
    }

    // ── ClearCheckedItemsUseCase ──────────────────────────────────────

    @Test
    fun `clearChecked remove apenas itens marcados`() = runTest {
        upsertItem(item("1", "Leite", isChecked = true))
        upsertItem(item("2", "Pão", isChecked = false))
        upsertItem(item("3", "Manteiga", isChecked = true))
        clearChecked()
        val items = getItems().first()
        assertEquals(1, items.size)
        assertEquals("Pão", items[0].name)
    }

    @Test
    fun `clearChecked nao remove itens nao marcados`() = runTest {
        upsertItem(item("1", "Leite", isChecked = false))
        upsertItem(item("2", "Pão", isChecked = false))
        clearChecked()
        assertEquals(2, getItems().first().size)
    }

    @Test
    fun `clearChecked em lista vazia nao causa erro`() = runTest {
        clearChecked()
        assertTrue(getItems().first().isEmpty())
    }

    @Test
    fun `clearChecked quando todos marcados deixa lista vazia`() = runTest {
        upsertItem(item("1", "Leite", isChecked = true))
        upsertItem(item("2", "Pão", isChecked = true))
        clearChecked()
        assertTrue(getItems().first().isEmpty())
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private fun item(
        id: String,
        name: String,
        isChecked: Boolean = false,
        category: Category = Category.OTHER
    ) = GroceryItem(
        id = id,
        name = name,
        quantity = 1.0,
        unit = "un",
        category = category,
        isChecked = isChecked,
        note = "",
        createdAt = 0L
    )
}
