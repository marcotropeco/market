import SwiftUI

struct ProgressCard: View {
    let checked: Int
    let total: Int
    let progress: Float

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text("Lista de compras")
                    .font(.headline)
                Spacer()
                Text("\(checked)/\(total)")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }

            ProgressView(value: progress)
                .tint(progress == 1 ? .green : .accentColor)
                .animation(.easeInOut, value: progress)
        }
        .padding()
        .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 12))
        .padding(.horizontal)
    }
}
