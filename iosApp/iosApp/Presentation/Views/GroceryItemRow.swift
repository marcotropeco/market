import SwiftUI

struct GroceryItemRow: View {
    let item: GroceryItem
    let onToggle: () -> Void
    let onEdit:   () -> Void
    let onDelete: () -> Void

    var body: some View {
        HStack(spacing: 12) {
            // Checkbox
            Button(action: onToggle) {
                Image(systemName: item.isChecked ? "checkmark.circle.fill" : "circle")
                    .font(.title2)
                    .foregroundStyle(item.isChecked ? .green : .secondary)
            }
            .buttonStyle(.plain)

            // Info
            VStack(alignment: .leading, spacing: 2) {
                Text(item.name)
                    .font(.body)
                    .strikethrough(item.isChecked)
                    .foregroundStyle(item.isChecked ? .secondary : .primary)

                HStack(spacing: 6) {
                    Text(item.formattedQuantity)
                        .font(.caption)
                        .foregroundStyle(.secondary)

                    if !item.note.isEmpty {
                        Text("·")
                            .foregroundStyle(.secondary)
                        Text(item.note)
                            .font(.caption)
                            .foregroundStyle(.secondary)
                            .lineLimit(1)
                    }
                }
            }

            Spacer()

            // Category emoji
            Text(item.category.emoji)
                .font(.title3)
        }
        .padding(.vertical, 4)
        .contentShape(Rectangle())
        .onTapGesture { onEdit() }
        .swipeActions(edge: .trailing, allowsFullSwipe: true) {
            Button(role: .destructive, action: onDelete) {
                Label("Remover", systemImage: "trash")
            }
        }
        .swipeActions(edge: .leading, allowsFullSwipe: true) {
            Button(action: onToggle) {
                Label(item.isChecked ? "Desmarcar" : "Marcar",
                      systemImage: item.isChecked ? "arrow.uturn.backward" : "checkmark")
            }
            .tint(.green)
        }
    }
}
