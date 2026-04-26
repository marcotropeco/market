package com.market.android.presentation.viewmodel

import com.market.android.FakeGroceryRepository
import com.market.shared.domain.model.Category
import com.market.shared.domain.model.GroceryItem
import com.market.shared.domain.usecase.ClearCheckedItemsUseCase
import com.market.shared.domain.usecase.DeleteGroceryItemUseCase
import com.market.shared.domain.usecase.GetGroceryItemsUseCase
import com.market.shared.domain.usecase.GroceryUseCases
import com.market.shared.domain.usecase.UpsertGroceryItemUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GroceryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeGroceryRepository
    private lateinit var viewModel: GroceryViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeGroceryRepository()
        viewModel = GroceryViewModel(
            GroceryUseCases(
                getItems    = GetGroceryItemsUseCase(repository),
                upsertItem  = UpsertGroceryItemUseCase(repository),
                deleteItem  = DeleteGroceryItemUseCase(repository),
                clearChecked = ClearCheckedItemsUseCase(repository)
            )
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Estado inicial ───────────────────────────────────────────────

    @Test
    fun `estado inicial tem lista vazia`() = runTest {
        collectState {
            val state = viewModel.uiState.value
            assertTrue(state.allItems.isEmpty())
        }
    }

    @Test
    fun `estado inicial tem filtro ALL`() = runTest {
        collectState {
            assertEquals(FilterOption.ALL, viewModel.uiState.value.filter)
        }
    }

    // ── addItem ──────────────────────────────────────────────────────

    @Test
    fun `addItem adiciona item na lista`() = runTest {
        collectState {
            viewModel.addItem("Leite", 1.0, "L", Category.DAIRY, "")
            advanceUntilIdle()
            assertEquals(1, viewModel.uiState.value.allItems.size)
            assertEquals("Leite", viewModel.uiState.value.allItems[0].name)
        }
    }

    @Test
    fun `addItem define isChecked como false`() = runTest {
        collectState {
            viewModel.addItem("Leite", 1.0, "L", Category.DAIRY, "")
            advanceUntilIdle()
            assertFalse(viewModel.uiState.value.allItems[0].isChecked)
        }
    }

    @Test
    fun `addItem faz trim no nome e nota`() = runTest {
        collectState {
            viewModel.addItem("  Leite  ", 1.0, "L", Category.DAIRY, "  Marca X  ")
            advanceUntilIdle()
            val item = viewModel.uiState.value.allItems[0]
            assertEquals("Leite", item.name)
            assertEquals("Marca X", item.note)
        }
    }

    @Test
    fun `addItem gera ids unicos para cada item`() = runTest {
        collectState {
            viewModel.addItem("Leite", 1.0, "L", Category.DAIRY, "")
            viewModel.addItem("Pão", 2.0, "un", Category.BAKERY, "")
            advanceUntilIdle()
            val ids = viewModel.uiState.value.allItems.map { it.id }
            assertEquals(2, ids.distinct().size)
        }
    }

    // ── updateItem ───────────────────────────────────────────────────

    @Test
    fun `updateItem altera nome preservando id`() = runTest {
        collectState {
            viewModel.addItem("Leite", 1.0, "L", Category.DAIRY, "")
            advanceUntilIdle()
            val original = viewModel.uiState.value.allItems[0]
            viewModel.updateItem(original, "Leite Integral", 2.0, "L", Category.DAIRY, "")
            advanceUntilIdle()
            val items = viewModel.uiState.value.allItems
            assertEquals(1, items.size)
            assertEquals(original.id, items[0].id)
            assertEquals("Leite Integral", items[0].name)
        }
    }

    @Test
    fun `updateItem preserva isChecked original`() = runTest {
        collectState {
            viewModel.addItem("Leite", 1.0, "L", Category.DAIRY, "")
            advanceUntilIdle()
            val original = viewModel.uiState.value.allItems[0]
            viewModel.toggleItem(original)
            advanceUntilIdle()
            val checked = viewModel.uiState.value.allItems[0]
            viewModel.updateItem(checked, "Leite Integral", 1.0, "L", Category.DAIRY, "")
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.allItems[0].isChecked)
        }
    }

    @Test
    fun `updateItem preserva createdAt original`() = runTest {
        collectState {
            viewModel.addItem("Leite", 1.0, "L", Category.DAIRY, "")
            advanceUntilIdle()
            val original = viewModel.uiState.value.allItems[0]
            viewModel.updateItem(original, "Leite Integral", 2.0, "L", Category.DAIRY, "")
            advanceUntilIdle()
            assertEquals(original.createdAt, viewModel.uiState.value.allItems[0].createdAt)
        }
    }

    // ── toggleItem ───────────────────────────────────────────────────

    @Test
    fun `toggleItem marca item nao marcado`() = runTest {
        collectState {
            viewModel.addItem("Leite", 1.0, "L", Category.DAIRY, "")
            advanceUntilIdle()
            val item = viewModel.uiState.value.allItems[0]
            assertFalse(item.isChecked)
            viewModel.toggleItem(item)
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.allItems[0].isChecked)
        }
    }

    @Test
    fun `toggleItem desmarca item marcado`() = runTest {
        collectState {
            viewModel.addItem("Leite", 1.0, "L", Category.DAIRY, "")
            advanceUntilIdle()
            val item = viewModel.uiState.value.allItems[0]
            viewModel.toggleItem(item)
            advanceUntilIdle()
            val checked = viewModel.uiState.value.allItems[0]
            viewModel.toggleItem(checked)
            advanceUntilIdle()
            assertFalse(viewModel.uiState.value.allItems[0].isChecked)
        }
    }

    // ── deleteItem ───────────────────────────────────────────────────

    @Test
    fun `deleteItem remove item da lista`() = runTest {
        collectState {
            viewModel.addItem("Leite", 1.0, "L", Category.DAIRY, "")
            viewModel.addItem("Pão", 1.0, "un", Category.BAKERY, "")
            advanceUntilIdle()
            val id = viewModel.uiState.value.allItems[0].id
            viewModel.deleteItem(id)
            advanceUntilIdle()
            assertEquals(1, viewModel.uiState.value.allItems.size)
            assertFalse(viewModel.uiState.value.allItems.any { it.id == id })
        }
    }

    // ── clearChecked ─────────────────────────────────────────────────

    @Test
    fun `clearChecked remove apenas itens marcados`() = runTest {
        collectState {
            viewModel.addItem("Leite", 1.0, "L", Category.DAIRY, "")
            viewModel.addItem("Pão", 1.0, "un", Category.BAKERY, "")
            advanceUntilIdle()
            viewModel.toggleItem(viewModel.uiState.value.allItems[0])
            advanceUntilIdle()
            viewModel.clearChecked()
            advanceUntilIdle()
            assertEquals(1, viewModel.uiState.value.allItems.size)
            assertFalse(viewModel.uiState.value.allItems[0].isChecked)
        }
    }

    // ── setFilter / setSearchQuery ────────────────────────────────────

    @Test
    fun `setFilter atualiza filtro no estado`() = runTest {
        collectState {
            viewModel.setFilter(FilterOption.PENDING)
            assertEquals(FilterOption.PENDING, viewModel.uiState.value.filter)
        }
    }

    @Test
    fun `setSearchQuery atualiza query no estado`() = runTest {
        collectState {
            viewModel.setSearchQuery("leite")
            assertEquals("leite", viewModel.uiState.value.searchQuery)
        }
    }

    @Test
    fun `filteredItems reflete filtro e busca combinados`() = runTest {
        collectState {
            viewModel.addItem("Leite", 1.0, "L", Category.DAIRY, "")
            viewModel.addItem("Leite Condensado", 1.0, "cx", Category.DAIRY, "")
            viewModel.addItem("Pão", 1.0, "un", Category.BAKERY, "")
            advanceUntilIdle()
            viewModel.setSearchQuery("leite")
            assertEquals(2, viewModel.uiState.value.filteredItems.size)
        }
    }

    // ── checkedCount no uiState ───────────────────────────────────────

    @Test
    fun `checkedCount reflete numero de itens marcados`() = runTest {
        collectState {
            viewModel.addItem("Leite", 1.0, "L", Category.DAIRY, "")
            viewModel.addItem("Pão", 1.0, "un", Category.BAKERY, "")
            advanceUntilIdle()
            viewModel.toggleItem(viewModel.uiState.value.allItems[0])
            advanceUntilIdle()
            assertEquals(1, viewModel.uiState.value.checkedCount)
        }
    }

    // ── Helper ────────────────────────────────────────────────────────

    private suspend fun collectState(block: suspend () -> Unit) {
        val job = launch(testDispatcher) { viewModel.uiState.collect { } }
        block()
        job.cancel()
    }
}
