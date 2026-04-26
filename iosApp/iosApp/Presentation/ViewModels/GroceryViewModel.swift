import Foundation
import Observation

// MARK: - Filter

enum FilterOption: String, CaseIterable, Identifiable {
    case all     = "Todos"
    case pending = "Pendentes"
    case checked = "Marcados"

    var id: String { rawValue }
}

// MARK: - ViewModel

@Observable
@MainActor
final class GroceryViewModel {

    // Raw list kept in sync with SwiftData
    private(set) var allItems: [GroceryItem] = []

    // UI controls
    var filter: FilterOption = .all
    var searchQuery: String  = ""

    // Confirmation dialogs
    var showClearConfirmDialog  = false
    var itemPendingDelete: String? = nil   // id of the item waiting for delete confirmation

    // Add / Edit sheet
    var showItemSheet   = false
    var editingItem: GroceryItem? = nil    // nil = new item

    private let store: GroceryStore

    init(store: GroceryStore = .shared) {
        self.store = store
        loadItems()
    }

    // MARK: Derived state

    var filteredItems: [GroceryItem] {
        var items = allItems

        switch filter {
        case .pending: items = items.filter { !$0.isChecked }
        case .checked: items = items.filter {  $0.isChecked }
        case .all:     break
        }

        if !searchQuery.isEmpty {
            items = items.filter { $0.name.localizedCaseInsensitiveContains(searchQuery) }
        }

        return items
    }

    var groupedItems: [(key: Category, value: [GroceryItem])] {
        let dict = Dictionary(grouping: filteredItems, by: \.category)
        return dict
            .map { (key: $0.key, value: $0.value) }
            .sorted { $0.key.rawValue < $1.key.rawValue }
    }

    var checkedCount: Int { allItems.filter(\.isChecked).count }
    var totalCount:   Int { allItems.count }

    var progress: Float {
        guard totalCount > 0 else { return 0 }
        return Float(checkedCount) / Float(totalCount)
    }

    // MARK: Actions

    func addItem(name: String, quantity: Double, unit: String, category: Category, note: String) {
        let item = GroceryItem(
            name:     name.trimmingCharacters(in: .whitespaces),
            quantity: quantity,
            unit:     unit,
            category: category,
            note:     note.trimmingCharacters(in: .whitespaces)
        )
        save(item)
    }

    func updateItem(_ original: GroceryItem, name: String, quantity: Double,
                    unit: String, category: Category, note: String) {
        let updated = GroceryItem(
            id:        original.id,
            name:      name.trimmingCharacters(in: .whitespaces),
            quantity:  quantity,
            unit:      unit,
            category:  category,
            isChecked: original.isChecked,
            note:      note.trimmingCharacters(in: .whitespaces),
            createdAt: original.createdAt
        )
        save(updated)
    }

    func toggleItem(_ item: GroceryItem) {
        var toggled = item
        toggled.isChecked.toggle()
        save(toggled)
    }

    func deleteItem(id: String) {
        try? store.delete(id: id)
        loadItems()
    }

    func confirmDelete(id: String) {
        itemPendingDelete = id
    }

    func clearChecked() {
        try? store.clearChecked()
        loadItems()
    }

    func openAddSheet() {
        editingItem  = nil
        showItemSheet = true
    }

    func openEditSheet(for item: GroceryItem) {
        editingItem  = item
        showItemSheet = true
    }

    // MARK: Private

    private func save(_ item: GroceryItem) {
        try? store.upsert(item)
        loadItems()
    }

    private func loadItems() {
        allItems = (try? store.fetchAll()) ?? []
    }
}
