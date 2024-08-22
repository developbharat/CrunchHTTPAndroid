package com.developbharat.crunchhttp.domain.repos.device

interface IDeviceDetails {
    fun useDeviceId(): String
    fun useDatabasePassword(): String
    fun isInternetConnected(): Boolean
}