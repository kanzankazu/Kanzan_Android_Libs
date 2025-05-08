@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import com.kanzankazu.kanzanmodel.GeneralOption
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.SocketAddress
import java.net.URL
import java.util.Collections


fun String?.toMultipart(multipartName: String): MultipartBody.Part? {
    return this?.run { File(this).toMultipart(multipartName) } ?: kotlin.run { null }
}

fun File?.toMultipart(multipartName: String): MultipartBody.Part? {
    return this?.run {
        val profileImageRequestBody = this.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        MultipartBody.Part.createFormData(multipartName, this.name, profileImageRequestBody)
    } ?: kotlin.run { null }
}

@SuppressLint("MissingPermission", "HardwareIds")
fun isSimAvailable(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        val sManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val infoSim1 = sManager.getActiveSubscriptionInfoForSimSlotIndex(0)
        val infoSim2 = sManager.getActiveSubscriptionInfoForSimSlotIndex(1)
        if (infoSim1 != null || infoSim2 != null) return true
    } else {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (telephonyManager.simSerialNumber != null) return true
    }
    return false
}

fun Context.isConnected(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

fun Context.isInternetConnect(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = connectivityManager.allNetworkInfo
    for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) return true
    return false
}

fun isInternetWorking0(): Boolean {
    var success = false
    try {
        val url = URL("https://google.com")
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 10000
        connection.connect()
        success = connection.responseCode == 200
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return success
}

fun isInternetWorking1(): Boolean {
    try {
        val p1 = Runtime.getRuntime().exec("ping -c 1 www.google.com")
        val returnVal = p1.waitFor()
        return returnVal == 0
    } catch (e: Exception) {
        e.debugMessageError("isInternetWorking1")
    }
    return false
}

fun isInternetWorking2(): Boolean {
    val runtime = Runtime.getRuntime()
    try {
        val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
        val exitValue = ipProcess.waitFor()
        return exitValue == 0
    } catch (e: IOException) {
        e.debugMessageError("isInternetWorking2")
    } catch (e: InterruptedException) {
        e.debugMessageError("isInternetWorking22")
    }
    return false
}

fun isInternetWorkingByPort(): Boolean = try {
    val timeoutMs = 2000
    val sock = Socket()
    val sockaddr: SocketAddress = InetSocketAddress("8.8.8.8", 53)
    sock.connect(sockaddr, timeoutMs)
    sock.close()
    true
} catch (e: IOException) {
    e.debugMessageError("isInternetWorkingByPort")
    false
}

fun Context.isWifiEnabled(): Boolean {
    val wifiManager = this.getSystemService(Context.WIFI_SERVICE) as WifiManager
    return wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED
}

fun Context.isWifiConnected(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    return wifiInfo!!.isConnected
}

fun Context.setWifiEnabled(isEnable: Boolean) {
    try {
        val wifi = this.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wc = WifiConfiguration()
        wc.SSID = "\"SSIDName\""
        wc.preSharedKey = "\"password\""
        wc.hiddenSSID = true
        wc.status = WifiConfiguration.Status.ENABLED
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
        wifi.isWifiEnabled = isEnable
        if (wifi.isWifiEnabled) simpleToast("Wifi turn on")
        else simpleToast("Wifi turn off")
    } catch (e: Exception) {
        e.debugMessageError("setWifiEnabled")
    }
}

fun Context.setMobileDataEnabled(b: Boolean) {
    try {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val conmanClass = Class.forName(connectivityManager.javaClass.name)
        val iConnectivityManagerField = conmanClass.getDeclaredField("mService")
        iConnectivityManagerField.isAccessible = b
        val iConnectivityManager = iConnectivityManagerField[connectivityManager]
        val iConnectivityManagerClass = Class.forName(iConnectivityManager.javaClass.name)
        val setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", java.lang.Boolean.TYPE)
        setMobileDataEnabledMethod.isAccessible = b
        setMobileDataEnabledMethod.invoke(iConnectivityManager, b)
    } catch (e: Exception) {
        e.debugMessageError("setMobileDataEnabled")
    } catch (e: ClassNotFoundException) {
        e.debugMessageError("setMobileDataEnabled1")
    } catch (e: InvocationTargetException) {
        e.debugMessageError("setMobileDataEnabled2")
    } catch (e: NoSuchMethodException) {
        e.debugMessageError("setMobileDataEnabled3")
    } catch (e: IllegalAccessException) {
        e.debugMessageError("setMobileDataEnabled4")
    } catch (e: NoSuchFieldException) {
        e.debugMessageError("setMobileDataEnabled5")
    }
}

fun Context.scanAllDeviceInWifi(timeout: Int = 1000) {
    val deviceInfoList: MutableList<GeneralOption> = ArrayList<GeneralOption>()
    try {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val dhcpInfo = wifiManager.dhcpInfo
        val subnet = get3PointIpAddress(wifiManager.dhcpInfo.gateway)
        for (i in 1..255) {
            val host = "$subnet.$i"
            if (InetAddress.getByName(host).isReachable(timeout)) {
                val strMacAddress: String = getMacAddressFromIP(host)
                "scanAllDeviceInWifi Reachable Host: $host and Mac : $strMacAddress is reachable!".debugMessageDebug(" - scanAllDeviceInWifi1")
                val localDeviceInfo = GeneralOption(title = host, desc = strMacAddress)
                deviceInfoList.add(localDeviceInfo)
            } else {
                "scanAllDeviceInWifi ❌ Not Reachable Host: $host".debugMessageDebug(" - scanAllDeviceInWifi2")
            }
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

private fun get3PointIpAddress(address: Int): String {
    return String.format(
        "%d.%d.%d",
        address and 0xff,
        address shr 8 and 0xff,
        address shr 16 and 0xff
    )
}

private fun getMacAddressFromIP(ipFinding: String?): String {
    Log.i("IPScanning", "Scan was started!")
    var bufferedReader: BufferedReader? = null
    try {
        bufferedReader = BufferedReader(FileReader("/proc/net/arp"))
        var line: String
        while (bufferedReader.readLine().also { line = it } != null) {
            val splitted = line.split(" +").toTypedArray()
            if (splitted.size >= 4) {
                val ip = splitted[0]
                val mac = splitted[3]
                if (mac.matches(Regex("..:..:..:..:..:.."))) if (ip.equals(ipFinding, true)) return mac
            }
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            bufferedReader?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return "00:00:00:00"
}

fun Context.isMobileDataEnabled(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
    return if (mobileInfo != null) {
        mobileInfo.state == NetworkInfo.State.CONNECTED
    } else {
        false
    }
}

fun Context.isToggleMobileDataConnection(): Boolean {
    var mobileDataEnabled = false // Assume disabled
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    try {
        val cmClass = Class.forName(cm.javaClass.name)
        val method = cmClass.getDeclaredMethod("getMobileDataEnabled")
        method.isAccessible = true // method is callable
        mobileDataEnabled = method.invoke(cm) as Boolean
    } catch (e: Exception) {
        e.debugMessageError("isToggleMobileDataConnection")
    }
    return mobileDataEnabled
}

fun getMacAddress(): String {
    try {
        val all: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (networkInterface in all) {
            if (!networkInterface.name.equals("wlan0", ignoreCase = true)) continue
            val macBytes = networkInterface.hardwareAddress ?: return ""
            val stringBuilder = StringBuilder()
            for (b in macBytes) stringBuilder.append(String.format("%02X:", b))
            if (stringBuilder.isNotEmpty()) stringBuilder.deleteCharAt(stringBuilder.length - 1)
            return stringBuilder.toString().trim()
        }
    } catch (e: Exception) {
        e.debugMessageError("getMacAddress")
    }
    return "02:00:00:00:00:00"
}