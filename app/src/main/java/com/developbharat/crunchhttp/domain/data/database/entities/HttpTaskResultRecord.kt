package com.developbharat.crunchhttp.domain.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "task_results")
data class HttpTaskResultRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "task_id")
    val taskId: String,

    @ColumnInfo(name = "data")
    val data: String,

    @ColumnInfo(name = "headers")
    val headers: String,

    @ColumnInfo(name = "is_success")
    val isSuccess: Boolean,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "status_code")
    val statusCode: Int,
)