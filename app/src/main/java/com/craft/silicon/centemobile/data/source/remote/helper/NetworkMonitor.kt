package com.craft.silicon.centemobile.data.source.remote.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import com.craft.silicon.centemobile.util.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
) : ConnectivityManager.NetworkCallback(), NetworkDataSource {
    private var networkRequest: NetworkRequest? = null
    private var networkIsh: NetworkIsh? = null


    private var connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    init {
        networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
    }

    override fun activeNetwork() {
        if (!isNetwork()) {
            networkIsh?.onNetwork(false)
        }
    }

    override fun connection(networkIsh: NetworkIsh) {
        this.networkIsh = networkIsh
    }

    override fun enable() {
        AppLogger.instance.appLog("NetworkCall", "Enabled")
        connectivityManager.registerNetworkCallback(networkRequest!!, this)
        activeNetwork()
    }

    override fun disable() {
        try {
            AppLogger.instance.appLog("NetworkCall", "Disabled")
            connectivityManager.unregisterNetworkCallback(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        networkIsh?.onNetwork(true)

    }


    override fun onLost(network: Network) {
        super.onLost(network)
        networkIsh?.onNetwork(false)

    }

    private fun isNetwork(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }


}

interface NetworkIsh {
    fun onNetwork(boolean: Boolean)
}

