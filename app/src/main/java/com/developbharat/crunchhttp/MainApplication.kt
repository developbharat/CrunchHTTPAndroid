package com.developbharat.crunchhttp

import android.app.Application
import android.os.Build
import com.developbharat.crunchhttp.services.HttpTaskWorker

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            HttpTaskWorker.schedulePeriodicTask(applicationContext)
        }
    }
}