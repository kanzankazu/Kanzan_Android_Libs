package com.kanzankazu.kanzanbase

import android.app.Activity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.kanzankazu.kanzanutil.kanzanextension.simpleToast
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessage
import com.kanzankazu.kanzanutil.kanzanextension.view.visible

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
class BaseAdmob(private val activity: Activity) {
    var isCloseApps = false
    var isRewardVideoComplete = false
    var idAdsRewardVideo: String? = null
    var mInterstitialAd: InterstitialAd? = null
    lateinit var mRewardedVideoAd: RewardedVideoAd

    fun setupBannerAds(adView: AdView) {
        MobileAds.initialize(activity)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adView.visible()
            }
        }
    }

    fun setupInterstitialAds(idInterstitial: String): InterstitialAd {
        MobileAds.initialize(activity)

        mInterstitialAd = InterstitialAd(activity)
        mInterstitialAd!!.adUnitId = idInterstitial
        val adRequest = AdRequest.Builder().build()
        mInterstitialAd!!.loadAd(adRequest)
        return mInterstitialAd as InterstitialAd
    }

    fun showInterstitialAds(mInterstitialAd: InterstitialAd?) {
        if (mInterstitialAd != null) {
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
                setupInterstitialAds(mInterstitialAd.adUnitId)
            } else "showInterstitialAds BaseAdmob The interstitial wasn't loaded yet.".debugMessage()
        } else "showInterstitialAds BaseAdmob The interstitial wasn't loaded yet.".debugMessage()
    }

    fun setupRewardVideoLegacyApi(idAdsApp: String, idAdsRewardVideo: String, isCompleteDestroy: Boolean): RewardedVideoAd {
        MobileAds.initialize(activity)

        this.idAdsRewardVideo = idAdsRewardVideo
        MobileAds.initialize(activity, idAdsApp)
        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity)
        mRewardedVideoAd.rewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onRewardedVideoCompleted() {
                "onRewardedVideoCompleted BaseAdmob".debugMessage()
            }

            override fun onRewardedVideoAdClosed() {
                if (isRewardVideoComplete) {
                    activity.simpleToast("Terima kasih sudah berdonasi d(^u^)b.")
                    if (isCloseApps) activity.finish()
                } else {
                    activity.simpleToast("Yah anda menutup iklan (TnT)")
                }
                "onRewardedVideoAdClosed BaseAdmob".debugMessage()
            }

            override fun onRewarded(p0: RewardItem) {
                isRewardVideoComplete = true
                "onRewarded BaseAdmob ${p0.type}".debugMessage()
                "onRewarded BaseAdmob ${p0.amount}".debugMessage()
                if (isCloseApps) onRewardedVideoAdClosed()
            }

            override fun onRewardedVideoAdOpened() {
                isRewardVideoComplete = false
                activity.simpleToast("Silahkan menonton sampai habis ya d(^u^)b ...")
                "onRewardedVideoAdOpened BaseAdmob".debugMessage()
            }

            override fun onRewardedVideoStarted() {
                isRewardVideoComplete = false
                loadRewardedVideoAds()
                "onRewardedVideoStarted BaseAdmob".debugMessage()
            }

            override fun onRewardedVideoAdLeftApplication() {
                activity.simpleToast("Yah anda menutup iklan dan aplikasi kami(TnT)")
                "onRewardedVideoAdLeftApplication BaseAdmob".debugMessage()
            }

            override fun onRewardedVideoAdFailedToLoad(p0: Int) {
                "onRewardedVideoAdFailedToLoad BaseAdmob $p0".debugMessage()
            }

            override fun onRewardedVideoAdLoaded() {
                "onRewardedVideoAdLoaded BaseAdmob".debugMessage()
            }
        }

        loadRewardedVideoAds()
        return mRewardedVideoAd
    }

    fun loadRewardedVideoAds() {
        if (idAdsRewardVideo.isNullOrEmpty()) "loadRewardedVideoAds BaseAdmob not yet setupRewardVideoLegacyApi".debugMessage()
        else mRewardedVideoAd.loadAd(idAdsRewardVideo, AdRequest.Builder().build())
    }

    fun showRewardedVideoAds(isCloseApp: Boolean?) {
        if (mRewardedVideoAd.isLoaded) {
            isCloseApps = isCloseApp!!
            mRewardedVideoAd.show()
        } else {
            "showRewardedVideoAds BaseAdmob The reward video wasn't loaded yet.".debugMessage()
        }
    }
}
