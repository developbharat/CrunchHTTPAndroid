package com.developbharat.crunchhttp.domain.store


interface ISharedStore {
    fun <T> setValue(name: String, value: T)
    fun <T> useValue(name: String, clazz: Class<T>): T
    fun deleteValue(name: String)
    fun isValueAvailable(name: String): Boolean
}