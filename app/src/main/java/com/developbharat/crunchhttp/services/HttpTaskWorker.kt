package com.developbharat.crunchhttp.services

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit


class HttpTaskWorker(private val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        Log.d("service", "Received broadcast event for Coroutine Worker")
        return Result.success()
//        TODO("Not yet implemented")
    }

    companion object {
        fun schedulePeriodicTask(context: Context) {
            val workManager = WorkManager.getInstance(context)
            val constraints = Constraints
                .Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
            val httpTaskWork = PeriodicWorkRequestBuilder<HttpTaskWorker>(5, TimeUnit.SECONDS)
                .setConstraints(constraints)
                .build()
            workManager.enqueue(httpTaskWork)
        }
    }
}

