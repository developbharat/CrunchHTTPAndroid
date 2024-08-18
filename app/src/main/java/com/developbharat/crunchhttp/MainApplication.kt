package com.developbharat.crunchhttp

import android.app.Application
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.developbharat.crunchhttp.services.HttpTaskWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() {
            return Configuration
                .Builder()
                .setWorkerFactory(workerFactory)
                .build()
        }


    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // TODO: This must be scheduled if user is authenticated
            // And in start screen, when user rechecks authenticated,
            // incase he is authenticated, then it must also be scheduled there too.
            HttpTaskWorker.schedulePeriodicTask(applicationContext)
        }
    }
}