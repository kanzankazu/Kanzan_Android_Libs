@file:Suppress("KotlinConstantConditions")

package com.kanzankazu.kanzannetwork

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

class NetworkLiveData(context: Context) : LiveData<NetworkStatus>() {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
//            debugMessage("NetworkLiveData onAvailable || $network")
//            debugMessage("NetworkLiveData onAvailable -------------------------------------------------------------------------------------------------------------------------")
            postValue(NetworkStatus.Available)
        }

        override fun onUnavailable() {
            super.onUnavailable()
//            debugMessage("NetworkLiveData onUnavailable")
//            debugMessage("NetworkLiveData onUnavailable -------------------------------------------------------------------------------------------------------------------------")
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
//            debugMessage("onLosing || $network || $maxMsToLive")
//            debugMessage("NetworkLiveData onLosing -------------------------------------------------------------------------------------------------------------------------")
            postValue(NetworkStatus.Unavailable)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
//            debugMessage("onLost || $network")
//            debugMessage("NetworkLiveData onLosing -------------------------------------------------------------------------------------------------------------------------")
            postValue(NetworkStatus.Unavailable)
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities)
//            debugMessage("NetworkLiveData onCapabilitiesChanged 1 || $network || $networkCapabilities")
//            debugMessage(
//                "NetworkLiveData onCapabilitiesChanged 2 || $network || ${
//                    networkCapabilities.hasCapability(
//                        NetworkCapabilities.NET_CAPABILITY_INTERNET
//                    )
//                }"
//            )
//            debugMessage(
//                "NetworkLiveData onCapabilitiesChanged 3 || $network || ${
//                    networkCapabilities.hasTransport(
//                        NetworkCapabilities.TRANSPORT_WIFI
//                    )
//                }"
//            )
//            debugMessage(
//                "NetworkLiveData onCapabilitiesChanged 3 || $network || ${
//                    networkCapabilities.hasTransport(
//                        NetworkCapabilities.TRANSPORT_CELLULAR
//                    )
//                }"
//            )
//            debugMessage("NetworkLiveData onCapabilitiesChanged 4 || $network || ${networkCapabilities.linkUpstreamBandwidthKbps}")
//            debugMessage("NetworkLiveData onCapabilitiesChanged 5 || $network || ${networkCapabilities.linkDownstreamBandwidthKbps}")
//            debugMessage("NetworkLiveData onCapabilitiesChanged -------------------------------------------------------------------------------------------------------------------------")
            postValue(NetworkStatus.Available)
        }

        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties)
//            debugMessage("NetworkLiveData onLinkPropertiesChanged 1 || $network || $linkProperties")
//            debugMessage("NetworkLiveData onLinkPropertiesChanged 2 || $network || ${linkProperties.linkAddresses}")
//            debugMessage("NetworkLiveData onLinkPropertiesChanged 3 || $network || ${linkProperties.dnsServers}")
//            debugMessage("NetworkLiveData onLinkPropertiesChanged -------------------------------------------------------------------------------------------------------------------------")
        }

        override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
            super.onBlockedStatusChanged(network, blocked)
//            debugMessage("NetworkLiveData onBlockedStatusChanged || $network || $blocked")
//            debugMessage("NetworkLiveData onBlockedStatusChanged -------------------------------------------------------------------------------------------------------------------------")
        }
    }

    override fun onActive() {
        super.onActive()
        val builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
