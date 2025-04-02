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
import java.lang.ref.WeakReference

/**
 */
class BaseAdmob(activity: Activity) {

    /**
     * A weak reference to the activity instance to prevent memory leaks.
     * This is used to hold a reference to an activity without preventing it from being garbage collected when no longer needed.
     *
     * Example:
     * ```kotlin
     * private val activityRef = WeakReference(activity)
     * ```
     *
     * Note:
     * Using WeakReference ensures that the activity can be garbage collected if the reference
     * is no longer in active use, which helps in avoiding memory leaks in cases like background tasks or long-lived objects.
     */
    private val activityRef = WeakReference(activity) // Agar menghindari memory leak
    private var idRewardAd = ""

     var mInterstitialAd: InterstitialAd? = null
     var mRewardedAd: RewardedAd? = null
    var onRewardAdLoading: (isLoading: Boolean) -> Unit = {}
    var onRewardAdReady: (isReady: Boolean) -> Unit = {}
    var onRewardAdFinish: () -> Unit = {}

    /**
     * Sets up a banner advertisement in the provided AdView.
     * The method initializes the Mobile Ads SDK, loads an ad request, and assigns behavior for success
     * and failure scenarios during ad loading. On successful loading, the banner ad is made visible.
     * On failure, an error message is logged.
     *
     * @param adView The AdView where the banner ad will be displayed. If null, an error message is logged, and no setup occurs.
     *
     * Example:
     * ```kotlin
     * val bannerAdView: AdView = findViewById(R.id.bannerAdView)
     * setupBannerAds(bannerAdView)
     * ```
     */
    fun setupBannerAds(adView: AdView?) {
        adView?.let { bannerAd ->
            activityRef.get()?.let { activity ->
                MobileAds.initialize(activity)
                val adRequest = AdRequest.Builder().build()
                bannerAd.loadAd(adRequest)
                bannerAd.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        bannerAd.visible()
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        "BannerAd failed to load: $error".debugMessageError()
                    }
                }
            }
        } ?: run {
            "setupBannerAds: Invalid AdView.".debugMessageError()
        }
    }

    /**
     * Initializes and loads an interstitial ad using the provided ad unit ID.
     * If the ad unit ID is invalid or blank, the method logs an error message and returns without performing any action.
     * The method handles success and failure callbacks for loading the ad.
     * On successful loading, the interstitial ad object is populated and a debug message is logged.
     * On failure, logs an error message and clears the interstitial ad reference.
     *
     * This method ensures the activity is valid before attempting to load the ad, using a weak reference to avoid memory leaks.
     * Use this method to set up interstitial ads before displaying them in your application flow.
     *
     * @param idInterstitialAd The Ad Unit ID of the interstitial advertisement to load.
     *                         If null or blank, an error is logged and the method exits.
     *
     * Example:
     * ```kotlin
     * setupInterstitialAds("ca-app-pub-3940256099942544/1033173712") // Replace with your Ad Unit ID
     * ```
     */
    fun setupInterstitialAds(idInterstitialAd: String) {
        if (idInterstitialAd.isBlank()) {
            "Invalid Interstitial Ad Unit ID.".debugMessageError()
            return
        }

        activityRef.get()?.let { activity ->
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                activity,
                idInterstitialAd,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                        interstitialAd.debugMessageDebug("InterstitialAd Loaded")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        mInterstitialAd = null
                        "InterstitialAd failed to load: $error".debugMessageError()
                    }
                }
            )
        } ?: run {
            "Activity invalid or destroyed.".debugMessageError()
        }
    }

    /**
     * Displays an interstitial advertisement if it is ready. If the ad is not ready, logs an error message
     * indicating that the interstitial ad is unavailable. Upon successfully displaying the ad, a setup method
     * is called to reinitialize the interstitial ad with its respective ad unit ID.
     *
     * This method references a weak reference to the activity to prevent memory leaks and ensures that the activity exists
     * and is valid before attempting to show the ad. If successful, the interstitial ad is displayed in the current activity.
     *
     * Use this method to display interstitial ads during appropriate moments in the user flow, such as transitions or pauses.
     *
     * Requirements:
     * - Ensure `setupInterstitialAds` is called before invoking this method to load the interstitial ad.
     * - Handle cases where the ad might not load due to network errors or other issues.
     *
     * Example:
     * ```kotlin
     * // Check and display an interstitial ad
     * showInterstitialAds()
     * ```
     */
    fun showInterstitialAds() {
        activityRef.get()?.let { activity ->
            mInterstitialAd?.apply {
                show(activity)
                setupInterstitialAds(adUnitId)
            } ?: run {
                "showInterstitialAds: Interstitial Ad is not ready.".debugMessageError()
            }
        }
    }

    /**
     * Initializes and sets up a rewarded video ad using the provided ad unit ID.
     * Handles the loading of the ad, along with callbacks for events such as success, failure, and user interaction.
     *
     * @param idAdsRewardVideo The Ad Unit ID for the rewarded video ad. If blank, an error is logged and the method returns without further action.
     *
     * Usage:
     * ```kotlin
     * setupRewardAd("ca-app-pub-3940256099942544/5224354917") // Replace with your Ad Unit ID
     * ```
     */
    fun setupRewardAd(idAdsRewardVideo: String) {
        if (idAdsRewardVideo.isBlank()) {
            "Invalid Reward Video Ad Unit ID.".debugMessageError()
            return
        }

        onRewardAdLoading.invoke(true)
        idRewardAd = idAdsRewardVideo

        activityRef.get()?.let { activity ->
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(activity, idAdsRewardVideo, adRequest, object : RewardedAdLoadCallback() {
                /**
                 * Callback invoked when a Rewarded Ad fails to load.
                 *
                 * @param error The `LoadAdError` object containing details of why the ad loading failed.
                 *
                 * This method sets the `mRewardedAd` instance to null, invokes the `onRewardAdLoading` callback with `false` to
                 * indicate the failure of the loading process, and invokes the `onRewardAdReady` callback with `false` to signify
                 * that the ad is not ready to be shown. Additionally, it logs an error message with the details of the failure.
                 */
                override fun onAdFailedToLoad(error: LoadAdError) {
                    mRewardedAd = null
                    onRewardAdLoading.invoke(false)
                    onRewardAdReady.invoke(false)
                    "RewardAd failed to load: $error".debugMessageError()
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    mRewardedAd = rewardedAd
                    onRewardAdLoading.invoke(false)
                    onRewardAdReady.invoke(true)
                    rewardedAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        /**
                         *
                         */
                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            mRewardedAd = null
                            onRewardAdReady.invoke(false)
                            "RewardAd failed to show: $error".debugMessageError()
                        }

                        override fun onAdShowedFullScreenContent() {
                            "RewardAd showing.".debugMessageDebug()
                        }

                        override fun onAdDismissedFullScreenContent() {
                            mRewardedAd = null
                            onRewardAdReady.invoke(false)
                            onRewardAdFinish.invoke()
                            "RewardAd dismissed.".debugMessageDebug()
                        }
                    }
                }
            })
        }
    }

    /**
     * Displays a rewarded advertisement to the user. If the ad is successfully shown, the associated reward is processed.
     * If no ad is available, an attempt to load a new ad is triggered via the retry callback.
     *
     * @param isFinishClose A Boolean value indicating whether the activity should finish after the ad is closed.
     *                      If true, the activity is finished upon closing the rewarded ad.
     * @param onRetryShowRewardAd A callback function invoked if there is no rewarded ad available to show.
     *                            Default is an empty function.
     *
     * Example:
     * ```kotlin
     * showRewardAd(isFinishClose = true) {
     *     // Retry logic to load and show the rewarded ad again
     *     println("Retrying to load Reward Ad.")
     * }
     * ```
     */
    fun showRewardAd(isFinishClose: Boolean, onRetryShowRewardAd: () -> Unit = {}) {
        activityRef.get()?.let { activity ->
            mRewardedAd?.let { rewardedAd ->
                rewardedAd.show(activity) { rewardItem ->
                    rewardItem.type.debugMessageDebug("Reward type: ${rewardItem.type}")
                    rewardItem.amount.debugMessageDebug("Reward amount: ${rewardItem.amount}")
                    if (isFinishClose && !activity.isFinishing) {
                        activity.finish()
                    }
                }
            } ?: run {
                idRewardAd.takeIf { it.isNotBlank() }?.let {
                    onRetryShowRewardAd.invoke()
                    setupRewardAd(it)
                } ?: "No Reward Ad to show.".debugMessageError()
            }
        }
    }

    fun isRewardIdValid(): Boolean {
        return mRewardedAd != null
    }
}