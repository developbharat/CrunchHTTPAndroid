package com.developbharat.crunchhttp.domain.models

import com.developbharat.crunchhttp.domain.data.database.entities.HttpTaskResultRecord
import com.developbharat.crunchhttp.type.SubmitHttpTaskResultInput

data class HttpTaskResult(
    val taskId: String,
    val data: String,
    val headers: String,
    val isSuccess: Boolean,
    val status: String,
    val statusCode: Int,
) {
    fun toDatabaseRecord(): HttpTaskResultRecord {
        return HttpTaskResultRecord(
            taskId = this.taskId,
            data = this.data,
            headers = this.headers,
            isSuccess = this.isSuccess,
            status = this.status,
            statusCode = this.statusCode
        )
    }

    fun toGraphQLInput(): SubmitHttpTaskResultInput {
        return SubmitHttpTaskResultInput(
            task_id = this.taskId,
            data = this.data,
            headers = this.headers,
            is_success = this.isSuccess,
            status = this.status,
            status_code = this.statusCode
        )
    }
}
