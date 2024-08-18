package com.developbharat.crunchhttp.services

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.http.HttpMethod
import com.developbharat.crunchhttp.ListClientDeviceTasksQuery
import com.developbharat.crunchhttp.SubmitHttpTaskResultsMutation
import com.developbharat.crunchhttp.common.Constants
import com.developbharat.crunchhttp.fragment.HttpTask
import com.developbharat.crunchhttp.type.SubmitHttpTaskResultInput
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit


@HiltWorker
class HttpTaskWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) :
    CoroutineWorker(context, workerParams) {

    val apolloClient: ApolloClient = provideApolloClient()

    @SuppressLint("HardwareIds")
    fun provideApolloClient(): ApolloClient {
        val contentResolver = applicationContext.contentResolver
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        return ApolloClient.Builder()
            .serverUrl(Constants.GRAPHQL_URL)
            .addHttpHeader("Authorization", androidId)
            .addHttpHeader("Content-Type", "application/json")
            .httpMethod(HttpMethod.Post)
            .build()
    }

    override suspend fun doWork(): Result {
        Log.d("service", "Received broadcast event for Coroutine Worker")
        var shouldContinue = true
        while (shouldContinue) {
            val tasks = this.fetchHttpTasks()
            // TODO: Store responses in sqlite database
            val responses = this.solveTasks(tasks)

            // TODO: Remove uploaded id from sqlite database
            val ids = uploadHttpResponses(responses)

            // TODO: Exit the loop once, we have no tasks left to process
            shouldContinue = false
        }
        return Result.success()
    }

    private suspend fun fetchHttpTasks(): List<HttpTask> {
        // fetch tasks from backend
        val tasks = apolloClient.query(ListClientDeviceTasksQuery()).execute()
        if (tasks.hasErrors() || tasks.data == null) return emptyList();
        return tasks.data!!.listClientDeviceTasks.map { it.httpTask }
    }

    private suspend fun uploadHttpResponses(responses: List<SubmitHttpTaskResultInput>): List<String> {
        val ids = apolloClient.mutation(SubmitHttpTaskResultsMutation(responses)).execute()
        if (ids.hasErrors() || ids.data == null) return emptyList()

        // return ids from submission of http responses
        return ids.data!!.submitHttpTaskResults.map { it }

    }

    private suspend fun solveTasks(tasks: List<HttpTask>): List<SubmitHttpTaskResultInput> {
        return tasks.map { solveTask(it) }
    }

    private suspend fun solveTask(task: HttpTask): SubmitHttpTaskResultInput = withContext(Dispatchers.IO) {
        var retries = task.max_retries

        var failedResult = SubmitHttpTaskResultInput(
            task_id = task.id,
            data = "",
            headers = "",
            is_success = false,
            status = "",
            status_code = 0
        )
        while (retries >= 0) {
            // set request path and method
            val connection = URL(task.path).openConnection() as HttpURLConnection
            connection.requestMethod = task.method.rawValue

            // set request headers
            val headersJson = JSONObject(task.headers)
            headersJson.keys().forEach { connection.setRequestProperty(it, headersJson.getString(it)) }

            // set request data
            if (task.data !== null) {
                connection.doOutput = true
                connection.outputStream.write(task.data.toByteArray())
            }

            // make http request
            connection.connect()

            // Parse headers and required data
            val statusCode = connection.responseCode
            val status = connection.responseMessage
            val headers = JSONObject(connection.headerFields.toMap()).toString(2)
            val content = connection.inputStream.bufferedReader().use { it.readText() }

            // Retry for failed request or invalid response
            if (!task.success_status_codes.contains(statusCode)) {
                failedResult = SubmitHttpTaskResultInput(
                    task_id = task.id,
                    data = content,
                    headers = headers,
                    is_success = false,
                    status = status,
                    status_code = statusCode
                )
                retries--;
                delay(1000) // wait for 1 seconds
                continue;
            }


            return@withContext SubmitHttpTaskResultInput(
                task_id = task.id,
                data = content,
                headers = headers,
                is_success = true,
                status = status,
                status_code = statusCode
            )
        }
        return@withContext failedResult
    }


    companion object {
        const val NAME = "HTTP_TASK_WORKER"
        fun schedulePeriodicTask(context: Context) {
            val workManager = WorkManager.getInstance(context)
            val constraints = Constraints
                .Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
            val httpTaskWork = PeriodicWorkRequestBuilder<HttpTaskWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
            workManager.enqueueUniquePeriodicWork(this.NAME, ExistingPeriodicWorkPolicy.KEEP, httpTaskWork)
        }
    }
}
