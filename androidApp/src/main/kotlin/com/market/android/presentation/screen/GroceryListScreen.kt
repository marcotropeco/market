package com.market.android.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.market.android.presentation.components.AddItemBottomSheet
import com.market.android.presentation.components.EmptyStateView
import com.market.android.presentation.components.GroceryItemCard
import com.market.android.presentation.theme.toColor
import com.market.android.presentation.viewmodel.FilterOption
import com.market.android.presentation.viewmodel.GroceryViewModel
import com.market.shared.domain.model.Category
import com.market.shared.domain.model.GroceryItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GroceryListScreen(
    viewModel: GroceryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddSheet by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<GroceryItem?>(null) }
    var searchActive by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (!searchActive) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🛒", style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Mercado",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { searchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar")
                        }
                        if (uiState.checkedCount > 0) {
                            IconButton(onClick = { showClearConfirmDialog = true }) {
                                Icon(
                                    Icons.Default.DeleteSweep,
                                    contentDescription = "Limpar marcados",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    scrollBehavior = scrollBehavior
                )
            } else {
                Surface(modifier = Modifier.fillMaxWidth()) {
                    DockedSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::setSearchQuery,
                        onSearch = { searchActive = false },
                        active = false,
                        onActiveChange = {},
                        placeholder = { Text("Buscar itens...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        trailingIcon = {
                            IconButton(onClick = {
                                viewModel.setSearchQuery("")
                                searchActive = false
                            }) {
                                Icon(Icons.Default.Clear, "Fechar busca")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        content = {}
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar item", modifier = Modifier.size(28.dp))
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {

            // Progress card
            if (uiState.totalCount > 0) {
                item(key = "progress") {
                    ProgressCard(
                        checked = uiState.checkedCount,
                        total = uiState.totalCount,
                        progress = uiState.progress,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .animateItem()
                    )
                }
            }

            // Filter chips
            item(key = "filters") {
                FilterRow(
                    selected = uiState.filter,
                    onSelect = viewModel::setFilter,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (uiState.filteredItems.isEmpty()) {
                item(key = "empty") {
                    EmptyStateView(
                        isFiltered = uiState.filter != FilterOption.ALL || uiState.searchQuery.isNotBlank(),
                        modifier = Modifier.padding(top = 48.dp)
                    )
                }
            } else {
                uiState.groupedItems.forEach { (category, items) ->
                    stickyHeader(key = "header_${category.name}") {
                        CategoryHeader(category = category)
                    }
                    items(
                        items = items,
                        key = { it.id }
                    ) { item ->
                        GroceryItemCard(
                            item = item,
                            onToggle = { viewModel.toggleItem(item) },
                            onDelete = { viewModel.deleteItem(item.id) },
                            onEdit = { itemToEdit = item },
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .animateItem()
                        )
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        AddItemBottomSheet(
            onDismiss = { showAddSheet = false },
            onSave = { name, qty, unit, cat, note ->
                viewModel.addItem(name, qty, unit, cat, note)
            }
        )
    }

    itemToEdit?.let { item ->
        AddItemBottomSheet(
            onDismiss = { itemToEdit = null },
            onSave = { name, qty, unit, cat, note ->
                viewModel.updateItem(item, name, qty, unit, cat, note)
            },
            initialItem = item
        )
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Remover marcados") },
            text = { Text("Deseja remover todos os ${uiState.checkedCount} itens marcados da lista?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearChecked()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Remover")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ProgressCard(
    checked: Int,
    total: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$checked de $total itens",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (progress >= 1f) "Tudo coletado! 🎉"
                        else "${((1f - progress) * total).toInt()} itens restantes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterRow(
    selected: FilterOption,
    onSelect: (FilterOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterOption.entries.forEach { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelect(option) },
                label = { Text(option.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
private fun CategoryHeader(
    category: Category,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = category.emoji, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.width(6.dp))
            Text(
                text = category.label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = category.toColor(),
                letterSpacing = androidx.compose.ui.unit.TextUnit(
                    1.5f,
                    androidx.compose.ui.unit.TextUnitType.Sp
                )
            )
        }
    }
}
