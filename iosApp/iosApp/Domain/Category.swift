import Foundation

enum Category: String, CaseIterable, Identifiable, Codable {
    case fruits      = "Frutas"
    case vegetables  = "Legumes e Verduras"
    case meat        = "Carnes"
    case dairy       = "Laticínios"
    case bakery      = "Padaria"
    case beverages   = "Bebidas"
    case cleaning    = "Limpeza"
    case hygiene     = "Higiene"
    case frozen      = "Congelados"
    case grains      = "Grãos e Cereais"
    case condiments  = "Condimentos"
    case snacks      = "Snacks"
    case other       = "Outros"

    var id: String { rawValue }

    var emoji: String {
        switch self {
        case .fruits:     return "🍎"
        case .vegetables: return "🥦"
        case .meat:       return "🥩"
        case .dairy:      return "🥛"
        case .bakery:     return "🍞"
        case .beverages:  return "🥤"
        case .cleaning:   return "🧹"
        case .hygiene:    return "🧴"
        case .frozen:     return "🧊"
        case .grains:     return "🌾"
        case .condiments: return "🧂"
        case .snacks:     return "🍿"
        case .other:      return "🛒"
        }
    }
}
