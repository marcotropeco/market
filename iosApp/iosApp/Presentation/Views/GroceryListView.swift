import SwiftUI

struct GroceryListView: View {
    @State private var viewModel = GroceryViewModel()

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Progress bar
                if viewModel.totalCount > 0 {
                    ProgressCard(
                        checked:  viewModel.checkedCount,
                        total:    viewModel.totalCount,
                        progress: viewModel.progress
                    )
                    .padding(.top, 8)
                }

                // Filters
                FilterRow(selected: $viewModel.filter)
                    .padding(.vertical, 8)

                // Search bar
                HStack {
                    Image(systemName: "magnifyingglass")
                        .foregroundStyle(.secondary)
                    TextField("Buscar item…", text: $viewModel.searchQuery)
                        .autocorrectionDisabled()
                    if !viewModel.searchQuery.isEmpty {
                        Button { viewModel.searchQuery = "" } label: {
                            Image(systemName: "xmark.circle.fill")
                                .foregroundStyle(.secondary)
                        }
                    }
                }
                .padding(10)
                .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 10))
                .padding(.horizontal)
                .padding(.bottom, 8)

                Divider()

                // List
                if viewModel.filteredItems.isEmpty {
                    EmptyStateView(filter: viewModel.filter)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    List {
                        ForEach(viewModel.groupedItems, id: \.key) { group in
                            Section {
                                ForEach(group.value) { item in
                                    GroceryItemRow(
                                        item:     item,
                                        onToggle: { viewModel.toggleItem(item) },
                                        onEdit:   { viewModel.openEditSheet(for: item) },
                                        onDelete: { viewModel.confirmDelete(id: item.id) }
                                    )
                                }
                            } header: {
                                HStack(spacing: 6) {
                                    Text(group.key.emoji)
                                    Text(group.key.rawValue)
                                        .font(.subheadline.weight(.semibold))
                                }
                            }
                        }
                    }
                    .listStyle(.insetGrouped)
                }
            }
            .navigationTitle("Market")
            .toolbar {
                // Clear checked
                if viewModel.checkedCount > 0 {
                    ToolbarItem(placement: .topBarLeading) {
                        Button {
                            viewModel.showClearConfirmDialog = true
                        } label: {
                            Label("Remover marcados", systemImage: "trash")
                        }
                        .tint(.red)
                    }
                }

                // Add item
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        viewModel.openAddSheet()
                    } label: {
                        Image(systemName: "plus")
                    }
                }
            }
            // Add / Edit sheet
            .sheet(isPresented: $viewModel.showItemSheet) {
                AddItemSheet(editingItem: viewModel.editingItem) { name, qty, unit, cat, note in
                    if let editing = viewModel.editingItem {
                        viewModel.updateItem(editing, name: name, quantity: qty,
                                             unit: unit, category: cat, note: note)
                    } else {
                        viewModel.addItem(name: name, quantity: qty,
                                          unit: unit, category: cat, note: note)
                    }
                }
            }
            // Delete single item confirmation
            .alert("Remover item?", isPresented: Binding(
                get:  { viewModel.itemPendingDelete != nil },
                set:  { if !$0 { viewModel.itemPendingDelete = nil } }
            )) {
                Button("Remover", role: .destructive) {
                    if let id = viewModel.itemPendingDelete {
                        viewModel.deleteItem(id: id)
                    }
                    viewModel.itemPendingDelete = nil
                }
                Button("Cancelar", role: .cancel) {
                    viewModel.itemPendingDelete = nil
                }
            } message: {
                Text("Esta ação não pode ser desfeita.")
            }
            // Clear checked confirmation
            .alert("Remover marcados", isPresented: $viewModel.showClearConfirmDialog) {
                Button("Remover \(viewModel.checkedCount) itens", role: .destructive) {
                    viewModel.clearChecked()
                }
                Button("Cancelar", role: .cancel) { }
            } message: {
                Text("Deseja remover todos os \(viewModel.checkedCount) itens marcados da lista?")
            }
        }
    }
}
