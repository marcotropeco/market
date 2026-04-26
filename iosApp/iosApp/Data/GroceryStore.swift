import Foundation
import SwiftData

// MARK: - SwiftData Model

@Model
final class GroceryItemModel {
    @Attribute(.unique) var id: String
    var name: String
    var quantity: Double
    var unit: String
    var categoryRaw: String
    var isChecked: Bool
    var note: String
    var createdAt: Date

    init(from item: GroceryItem) {
        self.id          = item.id
        self.name        = item.name
        self.quantity    = item.quantity
        self.unit        = item.unit
        self.categoryRaw = item.category.rawValue
        self.isChecked   = item.isChecked
        self.note        = item.note
        self.createdAt   = item.createdAt
    }

    func toDomain() -> GroceryItem {
        GroceryItem(
            id:        id,
            name:      name,
            quantity:  quantity,
            unit:      unit,
            category:  Category(rawValue: categoryRaw) ?? .other,
            isChecked: isChecked,
            note:      note,
            createdAt: createdAt
        )
    }

    func update(from item: GroceryItem) {
        name        = item.name
        quantity    = item.quantity
        unit        = item.unit
        categoryRaw = item.category.rawValue
        isChecked   = item.isChecked
        note        = item.note
    }
}

// MARK: - Repository

@MainActor
final class GroceryStore {

    static let shared = GroceryStore()

    private let container: ModelContainer

    private init() {
        let schema = Schema([GroceryItemModel.self])
        let config = ModelConfiguration(schema: schema, isStoredInMemoryOnly: false)
        do {
            container = try ModelContainer(for: schema, configurations: config)
        } catch {
            fatalError("Failed to create ModelContainer: \(error)")
        }
    }

    private var context: ModelContext { container.mainContext }

    // MARK: Fetch

    func fetchAll() throws -> [GroceryItem] {
        let descriptor = FetchDescriptor<GroceryItemModel>(
            sortBy: [SortDescriptor(\.createdAt, order: .forward)]
        )
        return try context.fetch(descriptor).map { $0.toDomain() }
    }

    // MARK: Upsert

    func upsert(_ item: GroceryItem) throws {
        let id = item.id
        let descriptor = FetchDescriptor<GroceryItemModel>(
            predicate: #Predicate { $0.id == id }
        )
        if let existing = try context.fetch(descriptor).first {
            existing.update(from: item)
        } else {
            context.insert(GroceryItemModel(from: item))
        }
        try context.save()
    }

    // MARK: Delete

    func delete(id: String) throws {
        let descriptor = FetchDescriptor<GroceryItemModel>(
            predicate: #Predicate { $0.id == id }
        )
        if let model = try context.fetch(descriptor).first {
            context.delete(model)
            try context.save()
        }
    }

    // MARK: Clear Checked

    func clearChecked() throws {
        let descriptor = FetchDescriptor<GroceryItemModel>(
            predicate: #Predicate { $0.isChecked == true }
        )
        let checked = try context.fetch(descriptor)
        checked.forEach { context.delete($0) }
        try context.save()
    }
}
