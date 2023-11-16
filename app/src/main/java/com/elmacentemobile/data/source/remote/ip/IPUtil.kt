package com.elmacentemobile.data.source.remote.ip

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import java.util.Locale


fun Context.getDeviceIpAddress(): String {
    val wifiManager =
        applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiInfo = wifiManager.connectionInfo

    if (wifiInfo != null) {
        val ipAddress = wifiInfo.ipAddress
        return String.format(
            Locale.getDefault(),
            "%d.%d.%d.%d",
            ipAddress and 0xff,
            ipAddress shr 8 and 0xff,
            ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff
        )
    }
    return ""
}

fun getMobileIPAddress(): String? {
    try {
        val interfaces: List<NetworkInterface> =
            Collections.list(NetworkInterface.getNetworkInterfaces())
        for (opp in interfaces) {
            val adds: List<InetAddress> = Collections.list(opp.inetAddresses)
            for (add in adds) {
                if (!add.isLoopbackAddress) {
                    return add.hostAddress
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return ""
}

@RequiresApi(Build.VERSION_CODES.M)
fun Context.isWifi(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)

    return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
}

@RequiresApi(Build.VERSION_CODES.M)
fun Context.isCellular(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)

    return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
}

@RequiresApi(Build.VERSION_CODES.M)
fun Context.ipAddress(): String? {
    return when {
        isWifi() -> getDeviceIpAddress()
        isCellular() -> getMobileIPAddress()
        else -> String()
    }
}




