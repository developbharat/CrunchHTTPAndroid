package com.developbharat.crunchhttp.domain.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.developbharat.crunchhttp.domain.data.database.entities.HttpTaskResultRecord


@Dao
interface HttpTaskResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: List<HttpTaskResultRecord>)

    @Delete
    suspend fun delete(data: List<HttpTaskResultRecord>)

    @Query("SELECT * FROM task_results;")
    suspend fun listTaskResults(): List<HttpTaskResultRecord>

    @Query("SELECT count(id) FROM task_results;")
    suspend fun count(): Int

    @Query("DELETE FROM task_results WHERE task_id IN (:taskIds);")
    suspend fun deleteWithTaskIds(taskIds: List<String>)
}