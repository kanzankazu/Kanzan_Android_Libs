@file:Suppress("KotlinConstantConditions")

package com.kanzankazu.kanzannetwork

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

/**
 * A subclass of `LiveData` that provides real-time updates on the network status of the device
 * by leveraging Android's `ConnectivityManager` and `NetworkCallback`.
 *
 * This class observes changes in the network state, such as availability, loss, or capability updates, and
 * posts the appropriate `NetworkStatus` values (`Available` or `Unavailable`) to the observers.
 *
 * @constructor Initializes the class with the provided [context], which is used to access the `ConnectivityManager` service for network monitoring.
 *
 * Example:
 * ```kotlin
 * val networkLiveData = NetworkLiveData(context)
 * networkLiveData.observe(this) { networkStatus ->
 *     when (networkStatus) {
 *         is NetworkStatus.Available -> // Handle available network
 *         is NetworkStatus.Unavailable -> // Handle unavailable network
 *     }
 * }
 * ```
 */
class NetworkLiveData(context: Context) : LiveData<NetworkStatus>() {

    /**
     * Responsible for managing network connectivity within the `NetworkLiveData` class by providing access to the device's network services.
     * It is used to register and unregister network callbacks for monitoring changes in network status.
     *
     * The `connectivityManager` interacts with Android's `ConnectivityManager` system service to observe
     * real-time changes in network connectivity, such as availability, loss, or capability updates.
     * It also serves as the key component for listening to network updates through the `networkCallback`,
     * ensuring that the network status is properly reflected in the `NetworkLiveData` instance.
     *
     * Usage Example:
     * ```kotlin
     * override fun onActive() {
     *     super.onActive()
     *     val builder = NetworkRequest.Builder()
     *     connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
     * }
     *
     * override fun onInactive() {
     *     super.onInactive()
     *     connectivityManager.unregisterNetworkCallback(networkCallback)
     * }
     * ```
     */
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * The `networkCallback` is a private instance of `ConnectivityManager.NetworkCallback` responsible for monitoring changes in the device's network
     *  connectivity.
     * It triggers specific actions based on different network states (e.g., available, lost, losing connection, etc.).
     * This callback is used by the `NetworkLiveData` class to update the live data with the current network status (`NetworkStatus.Available` or `Network
     * Status.Unavailable`).
     *
     * Key Methods:
     * - `onAvailable(network: Network)`: Invoked when a network becomes available. Updates live data to `NetworkStatus.Available`.
     * - `onUnavailable()`: Invoked when a network is completely unavailable.
     * - `onLosing(network: Network, maxMsToLive: Int)`: Invoked when the network connection is about to be lost. Updates live data to `NetworkStatus
     * .Unavailable`.
     * - `onLost(network: Network)`: Invoked when the network is lost. Updates live data to `NetworkStatus.Unavailable`.
     * - `onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities)`: Invoked when the capabilities of an active network change
     * , such as bandwidth or transport type. Updates live data to `NetworkStatus.Available`.
     * - `onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties)`: Invoked when the properties of a network's link, such as DNS servers
     * , change.
     * - `onBlockedStatusChanged(network: Network, blocked: Boolean)`: Invoked when a network's blocked status changes (e.g., network is restricted or
     *  unblocked).
     *
     * This callback is registered in the `onActive()` method of `NetworkLiveData` and unregistered in the `onInactive()` method, ensuring it is active
     *  only when the `NetworkLiveData` is being observed.
     */
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        /**
         * Handles the event triggered when a network becomes available.
         * Updates the LiveData with the status of the network as `NetworkStatus.Available`.
         *
         * @param network The `Network` instance representing the network that has become available.
         */
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
//            debugMessage("NetworkLiveData onAvailable || $network")
//            debugMessage("NetworkLiveData onAvailable -------------------------------------------------------------------------------------------------------------------------")
            postValue(NetworkStatus.Available)
        }

        /**
         * Invoked when the network becomes unavailable. This method serves as a callback
         * to handle the event where no network connection can be established.
         *
         * By default, it calls the superclass implementation of `onUnavailable()`
         * and can be customized further to provide additional behavior such as logging
         * or updating the application's state regarding network unavailability.
         *
         * Example:
         * ```kotlin
         * override fun onUnavailable() {
         *     super.onUnavailable()
         *     // Custom logic for handling network unavailability
         *     // e.g., show a "No Internet Connection" message to the user
         * }
         * ```
         */
        override fun onUnavailable() {
            super.onUnavailable()
//            debugMessage("NetworkLiveData onUnavailable")
//            debugMessage("NetworkLiveData onUnavailable -------------------------------------------------------------------------------------------------------------------------")
        }

        /**
         * Called when the system detects that a particular network is about to be lost.
         * Provides the network object and the estimated time in milliseconds before it is considered unavailable.
         * Posts a `NetworkStatus.Unavailable` value to indicate the network loss.
         *
         * @param network The `Network` object that is being lost.
         * @param maxMsToLive The estimated time in milliseconds before the network is fully lost.
         */
        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
//            debugMessage("onLosing || $network || $maxMsToLive")
//            debugMessage("NetworkLiveData onLosing -------------------------------------------------------------------------------------------------------------------------")
            postValue(NetworkStatus.Unavailable)
        }

        /**
         * Triggered when the network is lost and no longer available.
         * Updates the live data to reflect the network being unavailable.
         *
         * @param network The {@link Network} instance that was lost.
         */
        override fun onLost(network: Network) {
            super.onLost(network)
//            debugMessage("onLost || $network")
//            debugMessage("NetworkLiveData onLosing -------------------------------------------------------------------------------------------------------------------------")
            postValue(NetworkStatus.Unavailable)
        }

        /**
         * Callback method triggered when the network capabilities change for a specific network.
         * This is used to monitor changes in the internet capability, transport type (WiFi, Cellular, etc.),
         * and other network attributes, enabling updated network status in the application.
         *
         * @param network The network whose capabilities have changed.
         * @param networkCapabilities The updated capabilities of the specified network. Contains details
         * such as internet capability, bandwidth, and transport type.
         */
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

        /**
         * Notifies when the properties of a network link, such as IP addresses or DNS servers, are updated.
         * This method is invoked whenever there are changes to the link properties associated with the specified network.
         *
         * @param network The [Network] whose link properties have changed.
         * @param linkProperties The updated [LinkProperties] of the specified network, including details about IP addresses, DNS servers, etc.
         */
        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties)
//            debugMessage("NetworkLiveData onLinkPropertiesChanged 1 || $network || $linkProperties")
//            debugMessage("NetworkLiveData onLinkPropertiesChanged 2 || $network || ${linkProperties.linkAddresses}")
//            debugMessage("NetworkLiveData onLinkPropertiesChanged 3 || $network || ${linkProperties.dnsServers}")
//            debugMessage("NetworkLiveData onLinkPropertiesChanged -------------------------------------------------------------------------------------------------------------------------")
        }

        /**
         * Called when the blocked status of a network changes. This method is triggered when a network's
         * ability to operate, due to privacy or policy restrictions, is either enabled or disabled.
         *
         * @param network The {@code Network} whose blocked status has changed.
         * @param blocked {@code true} if the network is now blocked, {@code false} if the network is unblocked.
         */
        override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
            super.onBlockedStatusChanged(network, blocked)
//            debugMessage("NetworkLiveData onBlockedStatusChanged || $network || $blocked")
//            debugMessage("NetworkLiveData onBlockedStatusChanged -------------------------------------------------------------------------------------------------------------------------")
        }
    }

    /**
     * Called when the `NetworkLiveData` is active (i.e., it has active observers).
     * Registers a network callback to monitor network connectivity changes using the `ConnectivityManager`.
     * This callback will notify whenever the network availability or properties change, allowing the
     * `NetworkLiveData` to emit updates about the current network status.
     *
     * The registered callback observes the following network states:
     * - Network becoming available.
     * - Network losing connectivity imminently.
     * - Network lost connectivity.
     * - Network capabilities changing (e.g., connection type, bandwidth).
     * - Other network property changes (e.g., DNS server updates).
     *
     * The network callback is created when this method is executed, using a `NetworkRequest.Builder`
     * to define the required network request parameters.
     *
     * This method is typically called automatically by the lifecycle system when there are observers
     * subscribed to the `NetworkLiveData` instance.
     *
     * Example:
     * ```kotlin
     * val networkLiveData = NetworkLiveData(context)
     * networkLiveData.observe(this, Observer { networkStatus ->
     *     when (networkStatus) {
     *         is NetworkStatus.Available -> println("Network is available")
     *         is NetworkStatus.Unavailable -> println("Network is unavailable")
     *     }
     * })
     * ```
     */
    override fun onActive() {
        super.onActive()
        val builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
    }

    /**
     * This method is triggered when the `LiveData` object becomes inactive. It unregisters the
     * network callback from the `ConnectivityManager` to stop receiving network updates as the
     * `LiveData` no longer has any active observers.
     *
     * The `onInactive` method is typically used to clean up resources when the `LiveData` is
     * no longer observed, ensuring that unnecessary operations are not carried out.
     *
     * Overrides:
     * - `onInactive()` in `LiveData<NetworkStatus>`
     *
     * Example:
     * ```kotlin
     * val networkLiveData = NetworkLiveData(context)
     * networkLiveData.removeObservers(lifecycleOwner) // Triggers onInactive
     * ```
     */
    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
