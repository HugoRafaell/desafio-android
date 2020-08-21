package com.hugo.gitapp.application

import android.app.Application
import com.hugo.gitapp.modules.MainViewModelModule
import com.hugo.gitapp.modules.networkingModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        setupKoin()
    }

    private fun setupKoin() {
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(listOf(
                MainViewModelModule,
                networkingModule
            ))
        }
    }
}