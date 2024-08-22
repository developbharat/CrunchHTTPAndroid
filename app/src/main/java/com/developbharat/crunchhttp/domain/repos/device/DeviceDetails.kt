package com.developbharat.crunchhttp.domain.repos.device

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.SecureRandom
import javax.inject.Inject

class DeviceDetails @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) : IDeviceDetails {

    @SuppressLint("HardwareIds")
    override fun useDeviceId(): String {
        val contentResolver = context.contentResolver
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun useDatabasePassword(): String {
        // check if password is available in shared preferences
        val existingPassword = sharedPreferences.getString("DATABASE_PASSWORD", null)

        // return password if already exists.
        if (existingPassword != null) return existingPassword

        // generate and update password in shared prefs
        val password =
            (1..12).joinToString("") { "%02x".format(SecureRandom().nextInt(94)) }
        sharedPreferences.edit().putString("DATABASE_PASSWORD", password).apply()
        return password
    }

    override fun isInternetConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = cm.activeNetwork ?: return false
        val actNw = cm.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
//            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            //for check internet over Bluetooth
//            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }
}