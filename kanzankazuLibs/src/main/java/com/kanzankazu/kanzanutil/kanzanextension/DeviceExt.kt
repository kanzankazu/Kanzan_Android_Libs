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
        val intent = openAboutPhoneIntent()

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

fun Context.openAboutPhoneIntent(): Intent {
    val intent = getAboutPhoneIntent()
    intent.apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    }
    return intent
}

/**
 * Membuka halaman Developer Options
 * @return Boolean yang menandakan apakah intent berhasil dijalankan
 */
fun Context.openDeveloperOptions(): Boolean {
    return try {
        val intent = openDeveloperOptionsIntent()
        startActivity(intent)
        true
    } catch (e: Exception) {
        debugMessageError("Gagal membuka Developer Options: ${e.message}")
        false
    }
}

fun Context.openDeveloperOptionsIntent(): Intent {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Untuk Android 8.0 (API 26) dan di atasnya
        Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
    } else {
        // Untuk versi Android di bawah 8.0
        Intent(Settings.ACTION_DEVICE_INFO_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
    return intent
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

/**
 * Mendapatkan Intent About Phone berdasarkan merek perangkat.
 *
 * Supported brands:
 * Samsung, Xiaomi/Redmi/Poco, Oppo, Realme, Vivo, Huawei/Honor,
 * Google Pixel, OnePlus, Asus, Sony, Motorola, Nokia, Lenovo,
 * ZTE, LG, Infinix, Tecno, Itel, Nothing, Meizu, Sharp, Alcatel,
 * Wiko, Fairphone, HTC, Acer, Micromax, Lava, Coolpad, TCL
 */
private fun Context.getAboutPhoneIntent(): Intent {
    return when {
        // DeviceInfoSettingsActivity — Samsung, Xiaomi/Redmi/Poco, Oppo (ColorOS), Realme, Transsion (Infinix/Tecno/Itel), Meizu (Flyme), HTC (Sense)
        isSamsung() || isXiaomi() || isOppo() || isRealme() || isInfinix() || isTecno() || isItel() || isMeizu() || isHTC() -> createIntent(SETTINGS_PKG, "$SETTINGS_PKG.Settings\$DeviceInfoSettingsActivity")

        // VivoDeviceInfoSettings — Vivo (custom)
        isVivo() -> createIntent(SETTINGS_PKG, "$SETTINGS_PKG.Settings\$VivoDeviceInfoSettings")

        // SystemDashboardActivity + fragment — Huawei/Honor
        isHuawei() -> Intent().apply {
            setClassName(SETTINGS_PKG, "$SETTINGS_PKG.Settings\$SystemDashboardActivity")
            putExtra(":settings:show_fragment", "com.android.settings.deviceinfo.HardwareInfo")
        }

        // SystemDashboardActivity — Nokia (Android One), ZTE, LG
        isNokia() || isZTE() || isLG() -> createIntent(SETTINGS_PKG, "$SETTINGS_PKG.Settings\$SystemDashboardActivity")

        // MyDeviceInfoActivity — Google Pixel, OnePlus (OxygenOS), Nothing (Nothing OS), Fairphone
        isGoogle() || isOnePlus() || isNothing() || isFairphone() -> createIntent(SETTINGS_PKG, "$SETTINGS_PKG.Settings\$MyDeviceInfoActivity")

        // AsusDeviceInfoSettings — Asus (ZenUI)
        isAsus() -> createIntent(SETTINGS_PKG, "$SETTINGS_PKG.Settings\$AsusDeviceInfoSettings")

        // DeviceInfoSettings — Sony (Xperia), Motorola, Lenovo, Sharp (AQUOS), Alcatel, TCL, Wiko, Acer, Micromax, Lava, Coolpad
        isSony() || isMotorola() || isLenovo() || isSharp() || isAlcatel() || isTCL() || isWiko() || isAcer() || isMicromax() || isLava() || isCoolpad() -> createIntent(SETTINGS_PKG, "$SETTINGS_PKG.Settings\$DeviceInfoSettings")

        // Default — generic Android
        else -> Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)
    }
}

private fun Context.tryAlternativeIntents(): Boolean {
    val intents = listOf(
        Intent(Settings.ACTION_DEVICE_INFO_SETTINGS),
        createIntent(SETTINGS_PKG, "$SETTINGS_PKG.Settings\$DeviceInfoSettings"),
        createIntent(SETTINGS_PKG, "$SETTINGS_PKG.Settings\$DeviceInfoSettingsActivity"),
        createIntent(SETTINGS_PKG, "$SETTINGS_PKG.Settings\$MyDeviceInfoActivity"),
        createIntent(SETTINGS_PKG, "$SETTINGS_PKG.Settings\$SystemDashboardActivity")
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

// ===== Constants =====

private const val SETTINGS_PKG = "com.android.settings"

// ===== Deteksi Merek Perangkat =====

private fun isSamsung(): Boolean = Build.MANUFACTURER.equals("samsung", true)

private fun isXiaomi(): Boolean = arrayOf("xiaomi", "redmi", "poco", "blackshark", "pocophone")
    .any { Build.MANUFACTURER.contains(it, true) || Build.BRAND.contains(it, true) }

private fun isOppo(): Boolean = Build.MANUFACTURER.equals("oppo", true) ||
        Build.BRAND.equals("oppo", true)

private fun isVivo(): Boolean = Build.MANUFACTURER.equals("vivo", true) ||
        Build.BRAND.equals("vivo", true)

private fun isHuawei(): Boolean = arrayOf("huawei", "honor")
    .any { Build.MANUFACTURER.contains(it, true) || Build.BRAND.contains(it, true) }

private fun isRealme(): Boolean = Build.MANUFACTURER.equals("realme", true) ||
        Build.BRAND.equals("realme", true)

private fun isGoogle(): Boolean = Build.MANUFACTURER.equals("google", true) ||
        Build.BRAND.equals("google", true)

private fun isAsus(): Boolean = Build.MANUFACTURER.equals("asus", true) ||
        Build.BRAND.equals("asus", true)

private fun isSony(): Boolean = Build.MANUFACTURER.equals("sony", true) ||
        Build.BRAND.equals("sony", true)

private fun isOnePlus(): Boolean = Build.MANUFACTURER.equals("oneplus", true) ||
        Build.BRAND.equals("oneplus", true)

private fun isMotorola(): Boolean = Build.MANUFACTURER.equals("motorola", true) ||
        Build.BRAND.equals("motorola", true)

private fun isNokia(): Boolean = Build.MANUFACTURER.equals("nokia", true) ||
        Build.BRAND.equals("nokia", true) ||
        Build.MANUFACTURER.equals("hmd global", true)

private fun isLenovo(): Boolean = Build.MANUFACTURER.equals("lenovo", true) ||
        Build.BRAND.equals("lenovo", true)

private fun isZTE(): Boolean = Build.MANUFACTURER.equals("zte", true) ||
        Build.BRAND.equals("zte", true)

private fun isLG(): Boolean = arrayOf("lge", "lg")
    .any { Build.MANUFACTURER.equals(it, true) || Build.BRAND.equals(it, true) }

// Transsion group (populer di Indonesia & emerging markets)
private fun isInfinix(): Boolean = Build.MANUFACTURER.equals("infinix", true) ||
        Build.BRAND.equals("infinix", true)

private fun isTecno(): Boolean = Build.MANUFACTURER.equals("tecno", true) ||
        Build.BRAND.equals("tecno", true)

private fun isItel(): Boolean = Build.MANUFACTURER.equals("itel", true) ||
        Build.BRAND.equals("itel", true)

// Brand lainnya
private fun isNothing(): Boolean = Build.MANUFACTURER.equals("nothing", true) ||
        Build.BRAND.equals("nothing", true)

private fun isMeizu(): Boolean = Build.MANUFACTURER.equals("meizu", true) ||
        Build.BRAND.equals("meizu", true)

private fun isSharp(): Boolean = Build.MANUFACTURER.equals("sharp", true) ||
        Build.BRAND.equals("sharp", true)

private fun isAlcatel(): Boolean = Build.MANUFACTURER.equals("alcatel", true) ||
        Build.BRAND.equals("alcatel", true)

private fun isTCL(): Boolean = Build.MANUFACTURER.equals("tcl", true) ||
        Build.BRAND.equals("tcl", true)

private fun isWiko(): Boolean = Build.MANUFACTURER.equals("wiko", true) ||
        Build.BRAND.equals("wiko", true)

private fun isFairphone(): Boolean = Build.MANUFACTURER.equals("fairphone", true) ||
        Build.BRAND.equals("fairphone", true)

private fun isHTC(): Boolean = Build.MANUFACTURER.equals("htc", true) ||
        Build.BRAND.equals("htc", true)

private fun isAcer(): Boolean = Build.MANUFACTURER.equals("acer", true) ||
        Build.BRAND.equals("acer", true)

private fun isMicromax(): Boolean = Build.MANUFACTURER.equals("micromax", true) ||
        Build.BRAND.equals("micromax", true)

private fun isLava(): Boolean = Build.MANUFACTURER.equals("lava", true) ||
        Build.BRAND.equals("lava", true)

private fun isCoolpad(): Boolean = Build.MANUFACTURER.equals("coolpad", true) ||
        Build.BRAND.equals("coolpad", true)
