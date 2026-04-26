# 🛒 Mercado

Aplicativo de lista de compras desenvolvido com **Kotlin Multiplatform (KMP)**. A ideia é simples: a esposa cadastra os itens em casa e o marido marca na hora da compra.

---

## 📱 Screenshots

> _Adicione screenshots nas pastas abaixo e elas aparecerão aqui automaticamente._

| Lista principal | Adicionar item | Editar item | Confirmação de remoção |
|:-:|:-:|:-:|:-:|
| ![Lista](docs/screenshots/lista.png) | ![Adicionar](docs/screenshots/adicionar.png) | ![Editar](docs/screenshots/editar.png) | ![Remover](docs/screenshots/remover.png) |

---

## ✨ Funcionalidades

### Lista de compras
- Itens agrupados por **categoria**, com cabeçalhos fixos (sticky headers) durante a rolagem
- **Barra de progresso** mostrando quantos itens já foram coletados e o percentual de conclusão
- Filtros rápidos: **Todos · Pendentes · Feitos**
- **Busca em tempo real** pelo nome do item
- Itens marcados aparecem com tachado e opacidade reduzida, separados visualmente dos pendentes

### Cadastro de itens
- Nome, quantidade (com botões `+` / `−`), unidade, categoria e observação
- **10 unidades disponíveis:** `un · kg · g · L · ml · cx · pct · dz · rl · par`
- **10 categorias com emoji:** Hortifruti, Laticínios, Carnes, Padaria, Bebidas, Congelados, Higiene, Limpeza, Snacks, Outros

### Edição de itens
- Botão de **lápis** (✏️) visível em cada card para abrir o formulário de edição
- O formulário abre pré-preenchido com todos os dados do item selecionado
- Preserva o estado de marcado (`isChecked`) e a data de criação ao salvar

### Remoção de itens
- **Swipe para a esquerda** revela o fundo vermelho com ícone de lixeira
- Antes de remover, um **diálogo de confirmação** pergunta "Deseja remover 'X' da lista?"
- Botão **"Limpar marcados"** na barra superior remove todos os itens já coletados de uma vez

---

## 🏗️ Arquitetura

O projeto segue **Clean Architecture** com separação clara entre as camadas:

```
market/
├── shared/                         # Módulo KMP compartilhado
│   └── commonMain/
│       └── domain/
│           ├── model/              # GroceryItem, Category
│           ├── repository/         # Interface GroceryRepository
│           └── usecase/            # GetItems, UpsertItem, DeleteItem, ClearChecked
│
├── androidApp/                     # Aplicativo Android
│   └── src/main/kotlin/
│       ├── data/
│       │   ├── local/              # Room: GroceryDatabase, GroceryItemDao, GroceryItemEntity
│       │   └── repository/         # GroceryRepositoryImpl
│       ├── di/                     # AppModule (Koin)
│       └── presentation/
│           ├── viewmodel/          # GroceryViewModel, GroceryUiState, FilterOption
│           ├── screen/             # GroceryListScreen
│           ├── components/         # GroceryItemCard, AddItemBottomSheet, EmptyStateView
│           └── theme/              # Material 3: cores, tipografia
│
└── iosApp/                         # Aplicativo iOS (SwiftUI + SwiftData)
    └── iosApp/
        ├── Domain/                 # GroceryItem.swift, Category.swift
        ├── Data/                   # GroceryStore.swift (SwiftData @Model + repositório)
        └── Presentation/
            ├── ViewModels/         # GroceryViewModel.swift (@Observable)
            └── Views/              # GroceryListView, GroceryItemRow, AddItemSheet,
                                    # ProgressCard, FilterRow, EmptyStateView
```

### Fluxo de dados

```
Ação do usuário
    → ViewModel (viewModelScope + StateFlow)
        → Use Case
            → Repository
                → Room DAO
                    → SQLite
                        → Flow<List<GroceryItem>> emitido
                            → UiState atualizado
                                → Recomposição da UI
```

### Padrões utilizados

| Padrão | Aplicação |
|---|---|
| **MVVM** | `GroceryViewModel` + `GroceryUiState` com `StateFlow` |
| **Repository** | Interface no `shared`, implementação no `androidApp` |
| **Use Cases** | Um por operação, operador `invoke` |
| **Koin DI** | Injeção de dependências via `appModule` |
| **Reactive UI** | `combine` de múltiplos `Flow`s no ViewModel |

---

## 🛠️ Tecnologias

### Android
| Tecnologia | Versão | Uso |
|---|---|---|
| Kotlin Multiplatform | 2.0.0 | Compartilhamento de código entre plataformas |
| Jetpack Compose | BOM 2024.09.03 | UI declarativa |
| Material Design 3 | — | Componentes, tema, cores dinâmicas |
| Room | 2.6.1 | Banco de dados local (SQLite) |
| Koin | 3.5.6 | Injeção de dependências |
| Coroutines / Flow | 1.8.1 | Programação assíncrona e reativa |
| Lifecycle / ViewModel | 2.8.6 | Gerenciamento de estado e ciclo de vida |

### iOS
| Tecnologia | Versão | Uso |
|---|---|---|
| SwiftUI | iOS 17+ | UI declarativa |
| SwiftData | iOS 17+ | Banco de dados local (persistência) |
| Swift Observation (`@Observable`) | iOS 17+ | Gerenciamento de estado reativo |

**Alvos de build:**
- Android: `minSdk 26` · `targetSdk 34` · `compileSdk 34`
- iOS: `minDeploymentTarget 17.0` · `iPhone + iPad`
- Java: target 17

---

## 🎨 Tema e cores

O app usa **Material You** com cores dinâmicas no Android 12+. O esquema padrão é baseado em verde floresta como cor primária e teal como secundária. Cada categoria tem sua própria cor de destaque:

| Categoria | Cor |
|---|---|
| 🥦 Hortifruti | Verde |
| 🥛 Laticínios | Azul |
| 🥩 Carnes | Vermelho |
| 🍞 Padaria | Laranja |
| 🥤 Bebidas | Roxo |
| 🧊 Congelados | Ciano |
| 🧴 Higiene | Rosa |
| 🧹 Limpeza | Cinza |
| 🍿 Snacks | Laranja escuro |
| 🛒 Outros | Marrom |

---

## ✅ Testes

O projeto inclui testes unitários cobrindo as três camadas principais:

### `shared/commonTest` — Use Cases
- `GroceryUseCasesTest` — 13 testes cobrindo `GetGroceryItemsUseCase`, `UpsertGroceryItemUseCase`, `DeleteGroceryItemUseCase` e `ClearCheckedItemsUseCase`
- Usa `FakeGroceryRepository` baseado em `MutableStateFlow` — sem mocking library

### `androidApp/test` — ViewModel e UiState
- `GroceryUiStateTest` — 12 testes das propriedades computadas (`filteredItems`, `groupedItems`, `progress`, `checkedCount`)
- `GroceryViewModelTest` — 17 testes do `GroceryViewModel` com `UnconfinedTestDispatcher` e `Dispatchers.setMain`

```bash
# Rodar todos os testes
./gradlew test
./gradlew :shared:allTests
```

---

## 🚀 Como rodar

### Pré-requisitos
- Android Studio Hedgehog ou superior
- JDK 17
- Android SDK com `compileSdk 34`

### Android
1. Clone o repositório
2. Abra no Android Studio
3. Aguarde a sincronização do Gradle
4. Execute em um emulador ou dispositivo com Android 8.0+

### iOS
1. Abra `iosApp/iosApp.xcodeproj` no Xcode 15+
2. Selecione um simulador iOS 17+
3. Execute com `Cmd+R`

---

## 📂 Estrutura de modelos

### `GroceryItem`
```kotlin
data class GroceryItem(
    val id: String,           // UUID
    val name: String,         // Nome do item
    val quantity: Double,     // Quantidade
    val unit: String,         // Unidade (un, kg, g, L, ml, cx, pct, dz, rl, par)
    val category: Category,   // Categoria (enum)
    val isChecked: Boolean,   // Coletado?
    val note: String,         // Observação opcional
    val createdAt: Long       // Timestamp em milissegundos
)
```

### `GroceryRepository`
```kotlin
interface GroceryRepository {
    fun getAllItems(): Flow<List<GroceryItem>>
    suspend fun getItemById(id: String): GroceryItem?
    suspend fun upsertItem(item: GroceryItem)
    suspend fun deleteItem(id: String)
    suspend fun clearCheckedItems()
}
```
