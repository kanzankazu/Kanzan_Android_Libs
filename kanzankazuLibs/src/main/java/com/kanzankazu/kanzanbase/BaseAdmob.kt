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
 * Created by Faisal Bahri on 2020-02-11.
 */
class BaseAdmob(private val activity: Activity) {
    private var reSetupRewardVideoLegacyApi = 0

    private var idAdsRewardVideo = ""
    private var isRewardVideoComplete = false

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

    fun setupInterstitialAds(idInterstitialAd: String) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            /* context = */ activity,
            /* adUnitId = */ idInterstitialAd,
            /* adRequest = */ adRequest,
            /* loadCallback = */ object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    interstitialAd.debugMessageDebug("BaseAdmob - onAdLoaded - setupInterstitialAds")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    "BaseAdmob - onAdFailedToLoad - setupInterstitialAds - $adError".debugMessageDebug()
                    mInterstitialAd = null
                }
            }
        )
    }

    fun showInterstitialAds(mInterstitialAd: InterstitialAd?) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity)
            setupInterstitialAds(
                idInterstitialAd = mInterstitialAd.adUnitId
            )
        } else {
            "showInterstitialAds BaseAdmob The interstitial wasn't loaded yet.".debugMessageDebug()
        }
    }

    fun setupRewardVideoLegacyApi(idAdsRewardVideo: String, listener: (isReadyToLoad: Boolean) -> Unit = {}): RewardedAd? {
        this.idAdsRewardVideo = idAdsRewardVideo

        if (idAdsRewardVideo.isNotEmpty()) {
            RewardedAd.load(
                /* context = */ activity,
                /* adUnitId = */ idAdsRewardVideo,
                /* adRequest = */ AdRequest.Builder().build(),
                /* loadCallback = */ object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        loadAdError.debugMessageDebug("BaseAdmob - onAdFailedToLoad - setupRewardVideoLegacyApi")
                        mRewardedAd = null
                        listener.invoke(false)
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        rewardedAd.debugMessageDebug("BaseAdmob - onAdLoaded - setupRewardVideoLegacyApi")
                        mRewardedAd = rewardedAd
                        mRewardedAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                "BaseAdmob - onAdClicked".debugMessageDebug()
                            }

                            override fun onAdDismissedFullScreenContent() {
                                "BaseAdmob - onAdDismissedFullScreenContent".debugMessageDebug()
                                mRewardedAd = null
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                "BaseAdmob - onAdFailedToShowFullScreenContent".debugMessageDebug()
                                mRewardedAd = null
                            }

                            override fun onAdImpression() {
                                "BaseAdmob - onAdImpression".debugMessageDebug()
                            }

                            override fun onAdShowedFullScreenContent() {
                                "BaseAdmob - onAdShowedFullScreenContent".debugMessageDebug()
                            }
                        }
                        listener.invoke(true)
                    }
                }
            )
        } else {
            "setupRewardVideoLegacyApi BaseAdmob not yet setupRewardVideoLegacyApi".debugMessageError("BaseAdmob - setupRewardVideoLegacyApi")
        }
        return mRewardedAd
    }

    fun     showRewardedVideoAds(isFinishClose: Boolean, onRetryShow: (Int) -> Unit = {}) {
        mRewardedAd?.let { rewardedAd ->
            rewardedAd.debugMessageDebug("BaseAdmob - showRewardedVideoAds - rewardedAd")
            rewardedAd.show(activity) { rewardItem ->
                rewardItem.type.debugMessageDebug("BaseAdmob - showRewardedVideoAds - rewardItem")
                rewardItem.amount.debugMessageDebug("BaseAdmob - showRewardedVideoAds - rewardItem")
                rewardItem.debugMessageDebug("BaseAdmob - showRewardedVideoAds - rewardItem")
                isRewardVideoComplete = true
                if (isFinishClose) activity.finish()
            }
        } ?: run {
            "showRewardedVideoAds BaseAdmob The reward video wasn't loaded yet.".debugMessageError("BaseAdmob - showRewardedVideoAds")
            if (idAdsRewardVideo.isNotEmpty() && reSetupRewardVideoLegacyApi <= 3) {
                reSetupRewardVideoLegacyApi++
                onRetryShow.invoke(reSetupRewardVideoLegacyApi)
                setupRewardVideoLegacyApi(idAdsRewardVideo = idAdsRewardVideo)
            }
        }
    }
}
