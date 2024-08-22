package com.developbharat.crunchhttp.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.apollographql.apollo.ApolloClient
import com.developbharat.crunchhttp.ListClientDeviceTasksQuery
import com.developbharat.crunchhttp.SubmitHttpTaskResultsMutation
import com.developbharat.crunchhttp.domain.data.database.MainDatabase
import com.developbharat.crunchhttp.domain.models.HttpTaskResult
import com.developbharat.crunchhttp.fragment.HttpTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
    private val apolloClient: ApolloClient,
    private val db: MainDatabase
) : CoroutineWorker(context, workerParams) {
    private var lastBlankAttemptsCount: Int = 0


    override suspend fun doWork(): Result {
        Log.d("service", "Received broadcast event for Coroutine Worker")
        while (lastBlankAttemptsCount < 5) {
            // Fetch tasks to process from api
            val tasks = this.fetchHttpTasks()
            Log.d("service", "Fetched ${tasks.size} tasks")

            // increase lastBlankAttempt count if we have 0 tasks
            // and continue loop next iteration.
            // otherwise reset attempts to 0
            if (tasks.isEmpty()) {
                this.lastBlankAttemptsCount++
                delay(2000) // wait for 2 seconds
                continue
            } else {
                this.lastBlankAttemptsCount = 0
            }

            /**
             * TODO: storing in db is useless as of now, as we don't have worker to upload them periodically
             * will add a worker in future that runs every 1 hour and uploads all tasks(Task Results) whose associated task id is not yet expired.
             * This will also require us to store tasks along with their ids (And only persist whose expiry is a bit forward)
             *
             * TODO: And it also doesn't make sense to store them, as backend is automatically assigning them to another device or reassigning to current device after every 30 minutes.
             * so it will be useful only if tasks are submitted within 30 minutes from the time they were fetched from backend.
             */
            val responses = this.solveTasks(tasks)
//            db.httpTaskResultDao().insert(responses.map { it.toDatabaseRecord() })
            Log.d("service", "Solved ${responses.size} tasks")

            // TODO: Remove uploaded ids from sqlite database
            val ids = uploadHttpResponses(responses)
//            db.httpTaskResultDao().deleteWithTaskIds(ids)
            Log.d("service", "Uploaded ${ids.size} tasks")
        }
        return Result.success()
    }

    private suspend fun fetchHttpTasks(): List<HttpTask> {
        // fetch tasks from backend
        val tasks = apolloClient.query(ListClientDeviceTasksQuery()).execute()
        if (tasks.hasErrors() || tasks.data == null) return emptyList()
        return tasks.data!!.listClientDeviceTasks.map { it.httpTask }
    }

    private suspend fun uploadHttpResponses(responses: List<HttpTaskResult>): List<String> {
        val data = responses.map { it.toGraphQLInput() }
        val ids = apolloClient.mutation(SubmitHttpTaskResultsMutation(data)).execute()
        if (ids.hasErrors() || ids.data == null) return emptyList()

        // return ids from submission of http responses
        return ids.data!!.submitHttpTaskResults.map { it }

    }

    private suspend fun solveTasks(tasks: List<HttpTask>): List<HttpTaskResult> =
        withContext(Dispatchers.IO) {
            return@withContext tasks.map { async { solveTask(it) } }.awaitAll()
        }

    private suspend fun solveTask(task: HttpTask): HttpTaskResult {
        var attempts = 0
        var failedResult = HttpTaskResult(
            taskId = task.id,
            data = "",
            headers = "",
            status = "Android Client Device Worker Exception Occurred.",
            statusCode = 0,
            isSuccess = false
        )
        while (attempts <= task.max_retries) {
            try {
                val result = this.makeHttpRequest(task)
                if (result.isSuccess) return result

                // update failed result with failed response
                failedResult = result
            } catch (ex: Exception) {
                Log.d("service", "Failed to Solve Task with ID: ${task.id} due to: ${ex.localizedMessage}")
                failedResult =
                    failedResult.copy(status = "Android Client Device Worker Exception Occurred: ${ex.localizedMessage}")
            } finally {
                // increment attempts and wait for 2 seconds before next attempt
                attempts++
                delay(2000) // wait for 2 seconds
            }
        }
        return failedResult
    }

    private suspend fun makeHttpRequest(task: HttpTask): HttpTaskResult = withContext(Dispatchers.IO) {
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
        val resHeaders = mutableMapOf<String, String>()
        connection.headerFields.keys.forEach { if (it is String) resHeaders[it] = connection.getHeaderField(it) }
        val headers = JSONObject(resHeaders.toMap()).toString()
        val content = connection.inputStream.bufferedReader().use { it.readText() }

        // Return failure response if request failed
        if (!task.success_status_codes.contains(statusCode)) {
            return@withContext HttpTaskResult(
                taskId = task.id,
                data = content,
                headers = headers,
                isSuccess = false,
                status = status,
                statusCode = statusCode
            )
        }

        // return success response if request succeeds
        return@withContext HttpTaskResult(
            taskId = task.id,
            data = content,
            headers = headers,
            isSuccess = true,
            status = status,
            statusCode = statusCode
        )
    }


    companion object {
        private const val NAME = "HTTP_TASK_WORKER"
        fun schedulePeriodicTask(context: Context) {
            val workManager = WorkManager.getInstance(context)
            val constraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true)
                    .build()
            val httpTaskWork =
                PeriodicWorkRequestBuilder<HttpTaskWorker>(15, TimeUnit.MINUTES).setConstraints(constraints).build()
            workManager.enqueueUniquePeriodicWork(this.NAME, ExistingPeriodicWorkPolicy.KEEP, httpTaskWork)
        }

        fun scheduleOneTimeTask(context: Context) {
            val workManager = WorkManager.getInstance(context)
            val constraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true)
                    .build()
            val httpTaskWork = OneTimeWorkRequestBuilder<HttpTaskWorker>().setConstraints(constraints).build()
            workManager.enqueueUniqueWork(this.NAME, ExistingWorkPolicy.REPLACE, httpTaskWork)
        }
    }
}
