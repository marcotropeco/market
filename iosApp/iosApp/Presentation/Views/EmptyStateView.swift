import SwiftUI

struct EmptyStateView: View {
    let filter: FilterOption

    private var message: String {
        switch filter {
        case .all:     return "Nenhum item na lista.\nToque em + para adicionar."
        case .pending: return "Nenhum item pendente."
        case .checked: return "Nenhum item marcado ainda."
        }
    }

    var body: some View {
        ContentUnavailableView {
            Label("Lista vazia", systemImage: "cart")
        } description: {
            Text(message)
                .multilineTextAlignment(.center)
        }
    }
}
