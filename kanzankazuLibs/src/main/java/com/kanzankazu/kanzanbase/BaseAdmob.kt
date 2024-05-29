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
import com.kanzankazu.kanzanutil.kanzanextension.isNull
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessage
import com.kanzankazu.kanzanutil.kanzanextension.view.visible

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
class BaseAdmob(private val activity: Activity) {
    var isCloseApps = false
    var isRewardVideoComplete = false
    var idInterstitialAd: String = ""
    var idAdsRewardVideo: String = ""
    var mInterstitialAd: InterstitialAd? = null
    var mRewardedAd: RewardedAd? = null

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

    fun setupInterstitialAds(idInterstitialAd: String): InterstitialAd? {
        this.idInterstitialAd = idInterstitialAd
        MobileAds.initialize(activity)

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(activity, this.idInterstitialAd, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                "onAdFailedToLoad BaseAdmob $adError".debugMessage()
                mInterstitialAd = null
            }
        })

        return mInterstitialAd
    }

    fun showInterstitialAds(mInterstitialAd: InterstitialAd?) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity)
            setupInterstitialAds(mInterstitialAd.adUnitId)
        } else "showInterstitialAds BaseAdmob The interstitial wasn't loaded yet.".debugMessage()
    }

    fun setupRewardVideoLegacyApi(idAdsRewardVideo: String): RewardedAd? {
        this.idAdsRewardVideo = idAdsRewardVideo
        MobileAds.initialize(activity)

        loadRewardedVideoAds()

        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                "BaseAdmob - onAdClicked".debugMessage()
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                "BaseAdmob - onAdDismissedFullScreenContent".debugMessage()
                mRewardedAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                "BaseAdmob - onAdFailedToShowFullScreenContent".debugMessage()
                mRewardedAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                "BaseAdmob - onAdImpression".debugMessage()
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                "BaseAdmob - onAdShowedFullScreenContent".debugMessage()
            }
        }

        return mRewardedAd
    }

    fun loadRewardedVideoAds() {
        if (idAdsRewardVideo.isNotEmpty()) {
            RewardedAd.load(activity, this.idAdsRewardVideo, AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
                override fun onAdLoaded(p0: RewardedAd) {
                    mRewardedAd = p0
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    "onAdFailedToLoad BaseAdmob $p0".debugMessage()
                    mRewardedAd = null
                }
            })
        } else "loadRewardedVideoAds BaseAdmob not yet setupRewardVideoLegacyApi".debugMessage()
    }

    fun showRewardedVideoAds(isCloseApp: Boolean?) {
        if (!mRewardedAd.isNull()) {
            isCloseApps = isCloseApp!!
            mRewardedAd?.show(activity) {
                isRewardVideoComplete = true
                if (isCloseApps) {
                    activity.finish()
                }
            }
        } else {
            "showRewardedVideoAds BaseAdmob The reward video wasn't loaded yet.".debugMessage()
        }
    }
}
