package com.hugo.gitapp.modules

import com.hugo.gitapp.presentation.MainViewModel
import com.hugo.gitapp.retrofit.RetrofitClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val MainViewModelModule = module {
    viewModel{ MainViewModel(get()) }
}

val networkingModule = module {
    single { RetrofitClient(get()) }
}