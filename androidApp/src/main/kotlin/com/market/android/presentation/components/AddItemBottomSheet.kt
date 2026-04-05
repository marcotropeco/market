package com.market.android.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.market.android.presentation.theme.toColor
import com.market.android.presentation.theme.toContainerColor
import com.market.shared.domain.model.Category
import com.market.shared.domain.model.GroceryItem

private val UNITS = listOf("un", "kg", "g", "L", "ml", "cx", "pct", "dz", "rl", "par")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddItemBottomSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, quantity: Double, unit: String, category: Category, note: String) -> Unit,
    initialItem: GroceryItem? = null
) {
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var quantity by remember { mutableDoubleStateOf(initialItem?.quantity ?: 1.0) }
    var unit by remember { mutableStateOf(initialItem?.unit ?: "un") }
    var selectedCategory by remember { mutableStateOf(initialItem?.category ?: Category.OTHER) }
    var note by remember { mutableStateOf(initialItem?.note ?: "") }
    var nameError by remember { mutableStateOf(false) }
    val isEditing = initialItem != null

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .imePadding()
        ) {
            Text(
                text = if (isEditing) "Editar Item" else "Adicionar Item",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(20.dp))

            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                label = { Text("Nome do item *") },
                isError = nameError,
                supportingText = if (nameError) ({ Text("Informe o nome do item") }) else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // Quantity + Unit row
            Text(
                text = "Quantidade",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledTonalIconButton(
                    onClick = { if (quantity > 0.5) quantity -= if (quantity <= 1) 0.5 else 1.0 }
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Diminuir")
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = formatQty(quantity),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.width(56.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.width(12.dp))
                FilledTonalIconButton(
                    onClick = { quantity += if (quantity < 1) 0.5 else 1.0 }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                }
                Spacer(Modifier.width(16.dp))

                // Unit chips
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    UNITS.forEach { u ->
                        FilterChip(
                            selected = unit == u,
                            onClick = { unit = u },
                            label = { Text(u) },
                            modifier = Modifier.height(32.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Category
            Text(
                text = "Categoria",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Category.entries.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        onClick = { selectedCategory = cat },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) cat.toContainerColor() else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) BorderStroke(2.dp, cat.toColor()) else null,
                        tonalElevation = if (isSelected) 0.dp else 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = cat.emoji, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = cat.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) cat.toColor()
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Note field
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Observação (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                singleLine = true
            )

            Spacer(Modifier.height(24.dp))

            // Save button
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    onSave(name, quantity, unit, selectedCategory, note)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isEditing) "Salvar Alterações" else "Salvar Item",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun formatQty(qty: Double): String =
    if (qty == qty.toLong().toDouble()) qty.toLong().toString() else qty.toString()
