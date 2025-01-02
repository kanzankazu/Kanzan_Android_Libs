@file:Suppress("MemberVisibilityCanBePrivate")

package com.kanzankazu.kanzanbase

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError
import com.kanzankazu.kanzanutil.kanzanextension.view.visible

/**
 * Kelas BaseAdmob digunakan untuk mengelola iklan Google AdMob di dalam aplikasi.
 * Kelas ini menyediakan metode untuk menyiapkan iklan banner, iklan interstisial, dan iklan reward.
 * 
 * @param activity Aktivitas yang terkait dengan pengelolaan iklan.
 */
class BaseAdmob(private val activity: Activity) {
    var mInterstitialAd: InterstitialAd? = null // Variabel untuk menyimpan iklan interstisial
    var mRewardedAd: RewardedAd? = null // Variabel untuk menyimpan iklan reward

    private var idRewardAd = "" // ID untuk iklan reward

    // Fungsi lambda untuk memberikan callback saat iklan reward sedang dimuat
    var onRewardAdLoading: (isLoading: Boolean) -> Unit = {}

    // Fungsi lambda untuk memberikan callback saat iklan reward siap ditampilkan
    var onRewardAdReady: (isReady: Boolean) -> Unit = {}

    // Fungsi lambda untuk memberikan callback saat iklan reward selesai ditampilkan
    var onRewardAdFinish: () -> Unit = {}

    /**
     * Menyiapkan iklan banner.
     * 
     * @param adView Tampilan iklan banner yang akan dimuat.
     */
    fun setupBannerAds(adView: AdView) {
        MobileAds.initialize(activity) // Inisialisasi MobileAds

        val adRequest = AdRequest.Builder().build() // Membuat AdRequest baru
        adView.loadAd(adRequest) // Memuat iklan ke dalam adView
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adView.visible() // Menampilkan adView saat iklan berhasil dimuat
            }
        }
    }

    /**
     * Menyiapkan iklan interstisial.
     * 
     * @param idInterstitialAd ID unit iklan interstisial yang akan dimuat.
     */
    fun setupInterstitialAds(idInterstitialAd: String) {
        val adRequest = AdRequest.Builder().build() // Membuat AdRequest baru

        InterstitialAd.load(
            activity,
            idInterstitialAd,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd // Menyimpan iklan interstisial yang berhasil dimuat
                    interstitialAd.debugMessageDebug("BaseAdmob - onAdLoaded - setupInterstitialAds")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    "BaseAdmob - onAdFailedToLoad - setupInterstitialAds - $adError".debugMessageDebug()
                    mInterstitialAd = null // Mengatur mInterstitialAd menjadi null jika gagal memuat iklan
                }
            }
        )
    }

    /**
     * Menampilkan iklan interstisial jika sudah dimuat.
     * 
     * @param mInterstitialAd Iklan interstisial yang akan ditampilkan.
     */
    fun showInterstitialAds(mInterstitialAd: InterstitialAd?) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity) // Menampilkan iklan interstisial
            setupInterstitialAds(idInterstitialAd = mInterstitialAd.adUnitId) // Menyiapkan kembali iklan interstisial
        } else {
            "showInterstitialAds BaseAdmob The interstitial wasn't loaded yet.".debugMessageDebug()
        }
    }

    /**
     * Menyiapkan iklan reward.
     * 
     * @param idAdsRewardVideo ID unit iklan reward yang akan dimuat.
     * @return Iklan reward yang sudah dimuat atau null jika gagal.
     */
    fun setupRewardAd(idAdsRewardVideo: String): RewardedAd? {
        onRewardAdLoading.invoke(true) // Memberitahu bahwa iklan reward sedang dimuat
        this.idRewardAd = idAdsRewardVideo

        if (idAdsRewardVideo.isNotEmpty()) {
            RewardedAd.load(
                activity,
                idAdsRewardVideo,
                AdRequest.Builder().build(),
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        loadAdError.debugMessageDebug("BaseAdmob - setupRewardVideoLegacyApi - onAdFailedToLoad")
                        onRewardAdLoading.invoke(false) // Memberitahu bahwa iklan reward gagal dimuat
                        mRewardedAd = null
                        onRewardAdReady.invoke(false) // Memberitahu bahwa iklan reward tidak siap
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        rewardedAd.debugMessageDebug("BaseAdmob - setupRewardVideoLegacyApi - onAdLoaded")
                        onRewardAdLoading.invoke(false) // Memberitahu bahwa iklan reward berhasil dimuat
                        mRewardedAd = rewardedAd
                        mRewardedAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                "BaseAdmob - setupRewardVideoLegacyApi - onAdFailedToShowFullScreenContent".debugMessageDebug()
                                mRewardedAd = null
                                onRewardAdReady.invoke(false) // Memberitahu bahwa iklan reward tidak siap
                            }

                            override fun onAdShowedFullScreenContent() {
                                "BaseAdmob - setupRewardVideoLegacyApi - onAdShowedFullScreenContent".debugMessageDebug()
                            }

                            override fun onAdClicked() {
                                "BaseAdmob - setupRewardVideoLegacyApi - onAdClicked".debugMessageDebug()
                            }

                            override fun onAdImpression() {
                                "BaseAdmob - setupRewardVideoLegacyApi - onAdImpression".debugMessageDebug()
                            }

                            override fun onAdDismissedFullScreenContent() {
                                "BaseAdmob - setupRewardVideoLegacyApi - onAdDismissedFullScreenContent".debugMessageDebug()
                                mRewardedAd = null
                                onRewardAdReady.invoke(false) // Memberitahu bahwa iklan reward tidak siap
                                onRewardAdFinish.invoke() // Memberitahu bahwa iklan reward selesai ditampilkan
                            }
                        }
                        onRewardAdReady.invoke(true) // Memberitahu bahwa iklan reward siap
                    }
                }
            )
        } else {
            "setupRewardVideoLegacyApi BaseAdmob not yet setupRewardVideoLegacyApi".debugMessageError("BaseAdmob - setupRewardVideoLegacyApi")
            onRewardAdLoading.invoke(false) // Memberitahu bahwa iklan reward gagal dimuat
        }
        return mRewardedAd
    }

    /**
     * Menampilkan iklan reward jika sudah dimuat.
     * 
     * @param isFinishClose Menentukan apakah aktivitas harus ditutup setelah iklan selesai.
     * @param onRetryShowRewardAd Fungsi lambda untuk mengulang memuat iklan reward jika gagal.
     */
    fun showRewardAd(isFinishClose: Boolean, onRetryShowRewardAd: () -> Unit = {}) {
        mRewardedAd?.let { rewardedAd ->
            rewardedAd.debugMessageDebug("BaseAdmob - showRewardedVideoAds - rewardedAd")
            rewardedAd.show(activity) { rewardItem ->
                rewardItem.type.debugMessageDebug("BaseAdmob - showRewardedVideoAds - rewardItem")
                rewardItem.amount.debugMessageDebug("BaseAdmob - showRewardedVideoAds - rewardItem")
                if (isFinishClose) activity.finish() // Menutup aktivitas jika isFinishClose adalah true
            }
        } ?: run {
            "showRewardedVideoAds BaseAdmob The reward video wasn't loaded yet.".debugMessageError("BaseAdmob - showRewardedVideoAds")
            if (idRewardAd.isNotEmpty()) {
                onRetryShowRewardAd.invoke() // Memanggil fungsi retry jika gagal memuat
                setupRewardAd(idAdsRewardVideo = idRewardAd) // Menyiapkan kembali iklan reward
            }
        }
    }
}