import Foundation

struct GroceryItem: Identifiable, Equatable {
    let id: String
    var name: String
    var quantity: Double
    var unit: String
    var category: Category
    var isChecked: Bool
    var note: String
    let createdAt: Date

    init(
        id: String = UUID().uuidString,
        name: String,
        quantity: Double = 1.0,
        unit: String = "un",
        category: Category = .other,
        isChecked: Bool = false,
        note: String = "",
        createdAt: Date = Date()
    ) {
        self.id = id
        self.name = name
        self.quantity = quantity
        self.unit = unit
        self.category = category
        self.isChecked = isChecked
        self.note = note
        self.createdAt = createdAt
    }

    var formattedQuantity: String {
        let q = quantity.truncatingRemainder(dividingBy: 1) == 0
            ? String(Int(quantity))
            : String(format: "%.1f", quantity)
        return "\(q) \(unit)"
    }
}
