package com.cozyfitness

import android.app.Application
import com.cozyfitness.data.local.DefaultDataInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class CozyFitnessApp : Application() {

    @Inject
    lateinit var defaultDataInitializer: DefaultDataInitializer

    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        defaultDataInitializer.initializeIfNeeded(applicationScope)
    }
}