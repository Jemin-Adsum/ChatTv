package com.techo.chattv.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.techo.chattv.model.Channel

class InterstitialAds(var activity: Activity) {

    var fbInterstitialAd: InterstitialAd? = null
    var admobInterstitialAd: com.google.android.gms.ads.InterstitialAd? = null
    lateinit var activityIntent: Intent

    var isAdsShow = false
    var isFacebookAds = false
    var isAdmob = false
    var fb_interstitial_ad = ""
    var interstitial_ad = ""

    var ads_show_count = 0
    var clickCount = 0

    private var fbAdsListener: InterstitialAdListener = object : InterstitialAdListener {
        override fun onError(p0: Ad?, p1: AdError?) {
            Log.e(Constants.INTERSTITIAL_TAG, Constants.FB_ADS_ON_ERROR + p1!!.errorMessage)
        }

        override fun onAdLoaded(p0: Ad?) {
            Log.d(Constants.INTERSTITIAL_TAG, Constants.FB_ADS_LOADED)
        }

        override fun onAdClicked(p0: Ad?) {
            Log.d(Constants.INTERSTITIAL_TAG, Constants.FB_ADS_CLICKED)
        }

        override fun onLoggingImpression(p0: Ad?) {
            Log.d(Constants.INTERSTITIAL_TAG, Constants.FB_ADS_IMPRESSION)
        }

        override fun onInterstitialDisplayed(p0: Ad?) {
            Log.e(Constants.INTERSTITIAL_TAG, Constants.FB_ADS_DISPLAYED)
            if (fbInterstitialAd != null && !fbInterstitialAd!!.isAdLoaded)
                fbInterstitialAd!!.loadAd()
        }

        override fun onInterstitialDismissed(p0: Ad?) {
            Log.e(Constants.INTERSTITIAL_TAG, Constants.FB_ADS_DISMISS)
            if (fbInterstitialAd != null && !fbInterstitialAd!!.isAdLoaded) {
                fbInterstitialAd!!.loadAd()
            }
            if (activityIntent != null)
                activity.startActivity(activityIntent)
        }
    }

    private var adListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            Log.e(Constants.INTERSTITIAL_TAG, Constants.ADMOB_ADS_LOADED)
        }

        override fun onAdClosed() {
            super.onAdClosed()
            Log.e(Constants.INTERSTITIAL_TAG, Constants.ADMOB_ADS_CLOSSED)
            if (admobInterstitialAd != null && !admobInterstitialAd!!.isLoaded) {
                val adRequest = AdRequest.Builder().build()
                admobInterstitialAd!!.loadAd(adRequest)
            }
            if (activityIntent != null)
                activity.startActivity(activityIntent)
        }

        override fun onAdFailedToLoad(p0: LoadAdError?) {
            super.onAdFailedToLoad(p0)
            Log.e(Constants.INTERSTITIAL_TAG, Constants.ADMOB_ADS_ON_ERROR)
        }

        override fun onAdOpened() {
            super.onAdOpened()
            if (admobInterstitialAd != null && !admobInterstitialAd!!.isLoaded) {
                val adRequest = AdRequest.Builder().build()
                admobInterstitialAd!!.loadAd(adRequest)
            }
        }
    }

    fun loadInterstitialAds() {
        val pref = SaveSharedPreference(activity).getPreferences(activity)
        isAdsShow = pref.getBoolean(Constants.IS_ADS_SHOW, false)
        isFacebookAds = pref.getBoolean(Constants.IS_FACEBOOK_ADS, false)
        fb_interstitial_ad = pref.getString(Constants.FB_INTERSTITIAL_AD, "").toString()
        fbInterstitialAd = InterstitialAd(
            activity,
            fb_interstitial_ad
        )
        if (fbInterstitialAd != null && fbInterstitialAd?.isAdLoaded == false)
            fbInterstitialAd!!.loadAd(
                fbInterstitialAd!!.buildLoadAdConfig()
                    .withAdListener(fbAdsListener)
                    .build()
            )
    }

    fun loadAdmobAds() {
        val pref = SaveSharedPreference(activity).getPreferences(activity)
        isAdmob = pref.getBoolean(Constants.IS_ADMOB, false)
        interstitial_ad = pref.getString(Constants.INTERSTITIAL_AD, "").toString()
        val adRequest = AdRequest.Builder().build()
        admobInterstitialAd = com.google.android.gms.ads.InterstitialAd(activity)
        if (admobInterstitialAd != null && !admobInterstitialAd?.isLoaded!!) {
            admobInterstitialAd!!.adUnitId = interstitial_ad
            admobInterstitialAd!!.loadAd(adRequest)
            admobInterstitialAd!!.adListener = adListener
        }
    }

    fun onStartOtherActivity(intent: Intent) {
        ads_show_count = SaveSharedPreference(activity).getPreferences(activity)
            .getInt(Constants.ADS_SHOW_COUNT, 2)
        Log.e("COUNT","ads_show_count = $ads_show_count // clickCount = $clickCount")
        if (clickCount == ads_show_count)
            clickCount = 0
        activityIntent = intent
        if (isAdsShow) {
            if (isFacebookAds && fbInterstitialAd != null && fbInterstitialAd!!.isAdLoaded && clickCount == 0) {
                fbInterstitialAd!!.show()
            } else if (isAdmob && admobInterstitialAd != null && admobInterstitialAd!!.isLoaded && clickCount == 0) {
                admobInterstitialAd!!.show()
            } else {
                if (activityIntent != null)
                    activity.startActivity(activityIntent)
            }
        } else {
            if (activityIntent != null)
                activity.startActivity(activityIntent)
        }
        clickCount += 1
    }
}