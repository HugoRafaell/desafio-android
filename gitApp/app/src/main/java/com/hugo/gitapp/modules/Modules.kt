package com.hugo.gitapp.modules

import com.hugo.gitapp.view.MainViewModel
import com.hugo.gitapp.retrofit.RetrofitClient
import com.hugo.gitapp.view.DetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val MainViewModelModule = module {
    viewModel{ MainViewModel(get()) }
}

val DetailsViewModelModule = module {
    viewModel{ DetailsViewModel(get()) }
}

val networkingModule = module {
    single { RetrofitClient(get()) }
}