package com.kanzankazu.kanzanutil.kanzanextension

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError

/**
 * Ekstensi untuk menangani fungsi-fungsi terkait perangkat
 */

/**
 * Membuka halaman About Phone dengan dukungan untuk berbagai merek perangkat
 * @return Boolean yang menandakan apakah intent berhasil dijalankan
 */
fun Context.openAboutPhone(): Boolean {
    if (!isValidContext()) {
        debugMessageError("Context tidak valid")
        return false
    }

    return try {
        val intent = getAboutPhoneIntent()

        intent.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
            addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        }

        if (isIntentAvailable(intent)) {
            startActivity(intent)
            true
        } else {
            tryAlternativeIntents()
        }
    } catch (e: Exception) {
        debugMessageError("Gagal membuka About Phone: ${e.message}")
        false
    }
}

/**
 * Membuka halaman Developer Options
 * @return Boolean yang menandakan apakah intent berhasil dijalankan
 */
fun Context.openDeveloperOptions(): Boolean {
    return try {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Untuk Android 8.0 (API 26) dan di atasnya
            Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
        } else {
            // Untuk versi Android di bawah 8.0
            Intent(Settings.ACTION_DEVICE_INFO_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        startActivity(intent)
        true
    } catch (e: Exception) {
        debugMessageError("Gagal membuka Developer Options: ${e.message}")
        false
    }
}

/**
 * Mengecek apakah Developer Options sudah diaktifkan
 * @return Boolean yang menandakan apakah Developer Options aktif
 */
fun Context.isDeveloperOptionsEnabled(): Boolean {
    return Settings.Global.getInt(
        contentResolver,
        Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
        0
    ) != 0
}

// ===== Fungsi Bantuan Privat =====

private fun Context.getAboutPhoneIntent(): Intent {
    return when {
        isSamsung() -> createIntent("com.android.settings", "com.android.settings.Settings\$DeviceInfoSettingsActivity")
        isXiaomi() -> createIntent("com.android.settings", "com.android.settings.Settings\$DeviceInfoSettingsActivity")
        isOppo() || isRealme() -> createIntent("com.coloros.safecenter", "com.coloros.safecenter.PermissionAppAboutActivity")
        isVivo() -> createIntent("com.android.settings", "com.android.settings.Settings\$VivoDeviceInfoSettings")
        isHuawei() -> Intent().apply {
            setClassName("com.android.settings", "com.android.settings.Settings\$SystemDashboardActivity")
            putExtra(":settings:show_fragment", "com.android.settings.deviceinfo.HardwareInfo")
        }

        isAsus() -> createIntent("com.android.settings", "com.android.settings.Settings\$AsusDeviceInfoSettings")
        isSony() -> createIntent("com.android.settings", "com.android.settings.Settings\$DeviceInfoSettings")
        isOnePlus() -> createIntent("com.android.settings", "com.android.settings.Settings\$MyDeviceInfoActivity")
        isMotorola() -> createIntent("com.android.settings", "com.android.settings.Settings\$DeviceInfoSettings")
        isNokia() -> createIntent("com.android.settings", "com.android.settings.Settings\$SystemDashboardActivity")
        isLenovo() -> createIntent("com.android.settings", "com.android.settings.Settings\$DeviceInfoSettings")
        isZTE() -> createIntent("com.android.settings", "com.android.settings.Settings\$SystemDashboardActivity")
        isLG() -> createIntent("com.android.settings", "com.android.settings.Settings\$SystemDashboardActivity")
        else -> Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)
    }
}

private fun Context.tryAlternativeIntents(): Boolean {
    val intents = listOf(
        Intent(Settings.ACTION_DEVICE_INFO_SETTINGS),
        createIntent("com.android.settings", "com.android.settings.Settings\$DeviceInfoSettings"),
        createIntent("com.android.settings", "com.android.settings.Settings\$SystemDashboardActivity")
    )

    for (intent in intents) {
        try {
            if (isIntentAvailable(intent)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                return true
            }
        } catch (e: Exception) {
            debugMessageError("Error dengan intent alternatif: ${e.message}")
        }
    }
    return false
}

private fun Context?.isValidContext(): Boolean {
    return this != null
}

private fun Context.isIntentAvailable(intent: Intent): Boolean {
    return try {
        packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty()
    } catch (e: Exception) {
        debugMessageError("Error memeriksa ketersediaan intent: ${e.message}")
        false
    }
}

private fun createIntent(packageName: String, className: String): Intent {
    return Intent().setClassName(packageName, className)
}

// ===== Deteksi Merek Perangkat =====

private fun isSamsung(): Boolean = Build.MANUFACTURER.equals("samsung", true)

private fun isXiaomi(): Boolean = arrayOf("xiaomi", "redmi", "poco", "blackshark", "pocophone")
    .any { Build.MANUFACTURER.contains(it, true) }

private fun isOppo(): Boolean = Build.MANUFACTURER.equals("oppo", true) ||
        Build.BRAND.equals("oppo", true)

private fun isVivo(): Boolean = Build.MANUFACTURER.equals("vivo", true) ||
        Build.BRAND.equals("vivo", true)

private fun isHuawei(): Boolean = arrayOf("huawei", "honor")
    .any { Build.MANUFACTURER.contains(it, true) }

private fun isRealme(): Boolean = Build.MANUFACTURER.equals("realme", true) ||
        Build.BRAND.equals("realme", true)

private fun isAsus(): Boolean = Build.MANUFACTURER.equals("asus", true) ||
        Build.BRAND.equals("asus", true)

private fun isSony(): Boolean = Build.MANUFACTURER.equals("sony", true) ||
        Build.BRAND.equals("sony", true)

private fun isOnePlus(): Boolean = Build.MANUFACTURER.equals("oneplus", true) ||
        Build.BRAND.equals("oneplus", true)

private fun isMotorola(): Boolean = Build.MANUFACTURER.equals("motorola", true) ||
        Build.BRAND.equals("motorola", true)

private fun isNokia(): Boolean = Build.MANUFACTURER.equals("nokia", true) ||
        Build.BRAND.equals("nokia", true)

private fun isLenovo(): Boolean = Build.MANUFACTURER.equals("lenovo", true) ||
        Build.BRAND.equals("lenovo", true)

private fun isZTE(): Boolean = Build.MANUFACTURER.equals("zte", true) ||
        Build.BRAND.equals("zte", true)

private fun isLG(): Boolean = Build.MANUFACTURER.equals("lge", true) ||
        Build.BRAND.equals("lg", true) ||
        Build.MANUFACTURER.equals("lg", true) ||
        Build.BRAND.equals("lge", true)

// Constants untuk menghindari peringatan
private const val DeviceInfoSettings = "DeviceInfoSettings"
private const val VivoDeviceInfoSettings = "VivoDeviceInfoSettings"
private const val SystemDashboardActivity = "SystemDashboardActivity"
private const val AsusDeviceInfoSettings = "AsusDeviceInfoSettings"
