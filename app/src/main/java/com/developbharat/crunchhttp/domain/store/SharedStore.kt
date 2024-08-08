package com.developbharat.crunchhttp.domain.store

import android.content.SharedPreferences
import com.google.gson.Gson


class SharedStore(private val sharedPreferences: SharedPreferences, private val gson: Gson) :
    ISharedStore {
    override fun <T> setValue(name: String, value: T) {
        sharedPreferences.edit().putString(name, gson.toJson(value)).apply()
    }

    override fun <T> useValue(name: String, clazz: Class<T>): T {
        val value = sharedPreferences.getString(name, null)
            ?: throw Exception("Value not found for key: $name in SharedStore, kindly invoke isValueAvailable() prior to calling useValue.")
        return gson.fromJson(value, clazz)
            ?: throw Exception("Failed to Serialize value in SharedStore for $name")
    }


    override fun deleteValue(name: String) {
        sharedPreferences.edit().remove(name).apply()
    }

    override fun isValueAvailable(name: String): Boolean {
        return sharedPreferences.contains(name)
    }
}