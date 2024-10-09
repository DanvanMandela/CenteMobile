package com.elmacentemobile.data.source.remote.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject


class ConnectionMonitor @Inject constructor(@ApplicationContext context: Context) :
    ConnectionObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @RequiresApi(Build.VERSION_CODES.N)
    override fun observe(): Flow<ConnectionObserver.ConnectionEnum> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(ConnectionObserver.ConnectionEnum.Available) }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { send(ConnectionObserver.ConnectionEnum.Losing) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(ConnectionObserver.ConnectionEnum.Lost) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(ConnectionObserver.ConnectionEnum.UnAvailable) }
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    launch {
                        when {
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                                    || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                                    || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> send(
                                ConnectionObserver.ConnectionEnum.Capable
                            )
                            else -> send(ConnectionObserver.ConnectionEnum.UnCapable)
                        }

                    }
                }

            }

            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }

}

interface ConnectionObserver {
    fun observe(): Flow<ConnectionEnum>
    enum class ConnectionEnum {
        Available, UnAvailable, Losing, Lost, Capable, UnCapable
    }
}


