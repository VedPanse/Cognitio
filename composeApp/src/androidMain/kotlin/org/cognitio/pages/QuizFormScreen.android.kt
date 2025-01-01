package org.cognitio.pages


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import org.cognitio.AppContextProvider

actual fun isInternetAvailable(): Boolean {
    val connectivityManager = AppContextProvider.getContext()
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCapabilities = connectivityManager.activeNetwork?.let {
        connectivityManager.getNetworkCapabilities(it)
    }

    return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}
