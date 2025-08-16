package com.kanzankazu.kanzanutil

import android.widget.TextView
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallErrorCode
import com.google.android.play.core.install.model.InstallStatus

class InAppUpdateTest(
    private val fakeAppUpdateManager: FakeAppUpdateManager,
    private val inAppUpdate: InAppUpdate,
    private val statusView: TextView, // Untuk menampilkan status
) {

    // Fungsi untuk memeriksa apakah update tersedia
    fun checkForUpdate() {
        fakeAppUpdateManager.setUpdateAvailable(99) // Simulasi update tersedia
        val appUpdateInfoTask = fakeAppUpdateManager.appUpdateInfo

        if (appUpdateInfoTask.isSuccessful) {
            statusView.text = "Update tersedia! Versi terbaru: 2"
        } else {
            statusView.text = "Tidak ada update yang tersedia."
        }
    }

    // Fungsi untuk mengetes update fleksibel
    fun testFlexibleUpdate() {
        fakeAppUpdateManager.setUpdateAvailable(99) // Simulasi update tersedia
        val appUpdateInfo = fakeAppUpdateManager.appUpdateInfo.result
        if (appUpdateInfo != null) {
            inAppUpdate.setupInAppUpdate(AppUpdateType.FLEXIBLE)
            inAppUpdate.updateStart(appUpdateInfo)
            statusView.text = "Testing Flexible Update: Proses started..."
        } else {
            statusView.text = "Tidak ada update untuk Flexible Update."
        }
    }

    // Fungsi untuk mengetes update segera (immediate)
    fun testImmediateUpdate() {
        fakeAppUpdateManager.setUpdateAvailable(99) // Simulasi update tersedia
        val appUpdateInfo = fakeAppUpdateManager.appUpdateInfo.result
        if (appUpdateInfo != null) {
            inAppUpdate.setupInAppUpdate(AppUpdateType.IMMEDIATE)
            inAppUpdate.updateStart(appUpdateInfo)
            statusView.text = "Testing Immediate Update: Proses started..."
        } else {
            statusView.text = "Tidak ada update untuk Immediate Update."
        }
    }

    // Fungsi untuk menyelesaikan update
    fun completeUpdate() {
        fakeAppUpdateManager.registerListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                inAppUpdate.updateComplete()
                statusView.text = "Update telah selesai!"
            }
        }
        fakeAppUpdateManager.setInstallErrorCode(InstallErrorCode.NO_ERROR_PARTIALLY_ALLOWED) // Simulasi perubahan state
        inAppUpdate.updateComplete()
        statusView.text = "Update telah selesai!"
    }

    // Fungsi untuk mengetes error dalam FakeAppUpdateManager
    fun simulateError() {
        fakeAppUpdateManager.setUpdateAvailable(0) // Simulasikan tidak ada update
        val appUpdateInfoTask = fakeAppUpdateManager.appUpdateInfo

        if (!appUpdateInfoTask.isSuccessful) {
            statusView.text = "Gagal memeriksa update: Simulasi error terjadi."
        } else {
            statusView.text = "Error tidak terjadi; pastikan skenario benar."
        }
    }
}