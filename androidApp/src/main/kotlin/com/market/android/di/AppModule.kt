package com.market.android.di

import com.market.android.data.local.GroceryDatabase
import com.market.android.data.repository.GroceryRepositoryImpl
import com.market.android.presentation.viewmodel.GroceryViewModel
import com.market.shared.domain.repository.GroceryRepository
import com.market.shared.domain.usecase.ClearCheckedItemsUseCase
import com.market.shared.domain.usecase.DeleteGroceryItemUseCase
import com.market.shared.domain.usecase.GetGroceryItemsUseCase
import com.market.shared.domain.usecase.GroceryUseCases
import com.market.shared.domain.usecase.UpsertGroceryItemUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Database
    single { GroceryDatabase.create(androidContext()) }

    // Repository
    single<GroceryRepository> { GroceryRepositoryImpl(get()) }

    // Use Cases
    single {
        GroceryUseCases(
            getItems = GetGroceryItemsUseCase(get()),
            upsertItem = UpsertGroceryItemUseCase(get()),
            deleteItem = DeleteGroceryItemUseCase(get()),
            clearChecked = ClearCheckedItemsUseCase(get())
        )
    }

    // ViewModel
    viewModel { GroceryViewModel(get()) }
}
