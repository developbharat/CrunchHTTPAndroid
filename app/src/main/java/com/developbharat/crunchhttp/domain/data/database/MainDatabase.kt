package com.developbharat.crunchhttp.domain.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.developbharat.crunchhttp.domain.data.database.dao.HttpTaskResultDao
import com.developbharat.crunchhttp.domain.data.database.entities.HttpTaskResultRecord
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(entities = [HttpTaskResultRecord::class], version = 1, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {
    abstract fun httpTaskResultDao(): HttpTaskResultDao

    companion object {
        private const val DB_FILENAME = "data.db"

        fun createDatabaseInstance(context: Context, password: String): MainDatabase {
            val passphrase: ByteArray = SQLiteDatabase.getBytes(password.toCharArray())
            val factory = SupportFactory(passphrase)

            return Room.databaseBuilder(
                context,
                MainDatabase::class.java,
                DB_FILENAME
            )
                .openHelperFactory(factory)
                .build()
        }
    }
}