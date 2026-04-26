import SwiftUI

struct AddItemSheet: View {
    @Environment(\.dismiss) private var dismiss

    let editingItem: GroceryItem?
    let onSave: (String, Double, String, Category, String) -> Void

    // Form fields
    @State private var name:     String   = ""
    @State private var quantity: Double   = 1.0
    @State private var unit:     String   = "un"
    @State private var category: Category = .other
    @State private var note:     String   = ""

    private var isEditing: Bool { editingItem != nil }
    private var isValid:   Bool { !name.trimmingCharacters(in: .whitespaces).isEmpty }

    private let units = ["un", "kg", "g", "L", "ml", "cx", "pct", "dz"]

    var body: some View {
        NavigationStack {
            Form {
                // Name
                Section("Item") {
                    TextField("Nome do item", text: $name)
                        .autocorrectionDisabled()
                }

                // Quantity + Unit
                Section("Quantidade") {
                    HStack {
                        Stepper(value: $quantity, in: 0.1...999, step: 0.5) {
                            HStack {
                                Text("Qtd")
                                    .foregroundStyle(.secondary)
                                Spacer()
                                Text(formattedQuantity)
                                    .monospacedDigit()
                            }
                        }
                    }

                    Picker("Unidade", selection: $unit) {
                        ForEach(units, id: \.self) { Text($0) }
                    }
                }

                // Category
                Section("Categoria") {
                    Picker("Categoria", selection: $category) {
                        ForEach(Category.allCases) { cat in
                            Label(cat.rawValue, title: { Text(cat.rawValue) })
                                .tag(cat)
                        }
                    }
                }

                // Note
                Section("Observação (opcional)") {
                    TextField("Ex: sem lactose, marca X…", text: $note, axis: .vertical)
                        .lineLimit(2...4)
                }
            }
            .navigationTitle(isEditing ? "Editar item" : "Novo item")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancelar") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button(isEditing ? "Salvar" : "Adicionar") {
                        onSave(name, quantity, unit, category, note)
                        dismiss()
                    }
                    .disabled(!isValid)
                }
            }
        }
        .onAppear { prefill() }
    }

    // MARK: Helpers

    private var formattedQuantity: String {
        quantity.truncatingRemainder(dividingBy: 1) == 0
            ? String(Int(quantity))
            : String(format: "%.1f", quantity)
    }

    private func prefill() {
        guard let item = editingItem else { return }
        name     = item.name
        quantity = item.quantity
        unit     = item.unit
        category = item.category
        note     = item.note
    }
}
