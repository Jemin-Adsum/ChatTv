package com.techo.chattv.utils

import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.facebook.ads.*
import com.facebook.ads.AdError
import com.google.android.gms.ads.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.techo.chattv.R

class NativeAds {

    fun loadNativeFBAd(
        context: Activity /*, NativeAdLayout nativeAdLayout*/,
        layNative: RelativeLayout?
    ) {
        val pref = SaveSharedPreference(context).getPreferences(context)
        val isAdsShow = pref.getBoolean(Constants.IS_ADS_SHOW, false)
        val isFacebookAds = pref.getBoolean(Constants.IS_FACEBOOK_ADS, false)
        val isAdmob = pref.getBoolean(Constants.IS_ADMOB, false)
        val native_ad = pref.getString(Constants.NATIVE_AD, "")
        val fb_native_ad = pref.getString(Constants.FB_NATIVE_AD, "")

        if (isAdsShow) {
            if (isFacebookAds && !fb_native_ad.isNullOrEmpty()) {
                val nativeAd = NativeAd(context, fb_native_ad)
                val nativeAdListener: NativeAdListener = object : NativeAdListener {
                    override fun onMediaDownloaded(ad: Ad) {}
                    override fun onError(ad: Ad, adError: AdError) {
                        if (isAdmob) {
                            native_ad?.let { loadAdMobNativeAd(context, layNative!!, it) }
                        } else {
                            Log.e("isAdmob", isAdmob.toString())
                        }
                    }

                    override fun onAdLoaded(p0: Ad?) {
//                        Log.e("FB-NATIVE-ADS","LOADED")
                        val inflater = context.layoutInflater
                        val view = inflater.inflate(R.layout.native_ad_layout, null)
                        val nativeAdLayout =
                            view.findViewById<NativeAdLayout>(R.id.native_ad_container)
                        layNative!!.removeAllViews()
                        layNative.addView(view)
                        layNative.visibility = View.VISIBLE
                        nativeAdLayout.visibility = View.VISIBLE
                        inflateNativeAd(context, nativeAd, nativeAdLayout)
                        nativeAd.downloadMedia()
                    }

                    override fun onAdClicked(p0: Ad?) {
//                        Log.e("loadNativeFBAd", "onAdClicked")
                    }

                    override fun onLoggingImpression(p0: Ad?) {
//                        Log.e("loadNativeFBAd", "onLoggingImpression")
                    }
                }
                nativeAd.loadAd(
                    nativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
                        .build()
                )
            } else if (isAdmob) {
                native_ad?.let { loadAdMobNativeAd(context, layNative!!, it) }
            } else {
                Log.e("isFacebookAds", isFacebookAds.toString())
            }
        } else {
            Log.e("isAdsShow", isAdsShow.toString())
        }
    }

    private fun inflateNativeAd(
        context: Activity,
        nativeAd: NativeAd,
        nativeAdLayout: NativeAdLayout
    ) {
        try {
            nativeAd.unregisterView()
            val inflater = LayoutInflater.from(context)
            val adView = inflater.inflate(R.layout.native_ad_view, nativeAdLayout, false)
            nativeAdLayout.addView(adView)
            val adChoicesContainer = context.findViewById<RelativeLayout>(R.id.ad_choices_container)
            if (adChoicesContainer != null) {
                val adOptionsView = AdOptionsView(context, nativeAd, nativeAdLayout)
                adChoicesContainer.removeAllViews()
                adChoicesContainer.addView(adOptionsView, 0)
                val nativeAdIcon = adView.findViewById<MediaView>(R.id.native_ad_icon)
                val nativeAdTitle = adView.findViewById<TextView>(R.id.native_ad_title)
                val nativeAdMedia = adView.findViewById<MediaView>(R.id.native_ad_media)
                val nativeAdSocialContext =
                    adView.findViewById<TextView>(R.id.native_ad_social_context)
                val nativeAdBody = adView.findViewById<TextView>(R.id.native_ad_body)
                val sponsoredLabel = adView.findViewById<TextView>(R.id.native_ad_sponsored_label)
                val nativeAdCallToAction =
                    adView.findViewById<Button>(R.id.native_ad_call_to_action)
                nativeAdTitle.text = nativeAd.advertiserName
                nativeAdBody.text = nativeAd.adBodyText
                nativeAdSocialContext.text = nativeAd.adSocialContext
                nativeAdCallToAction.visibility =
                    if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
                nativeAdCallToAction.text = nativeAd.adCallToAction
                sponsoredLabel.text = nativeAd.sponsoredTranslation
                val clickableViews: MutableList<View> = ArrayList()
                clickableViews.add(nativeAdTitle)
                clickableViews.add(nativeAdCallToAction)
                clickableViews.add(nativeAdIcon)
                nativeAd.registerViewForInteraction(
                    adView, nativeAdMedia, nativeAdIcon, clickableViews
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("nativeAd-Exc", e.message.toString())
        }
    }

    private fun loadAdMobNativeAd(activity: Activity, relativeLayout: RelativeLayout, id: String) {
        Log.e("loadAdMobNativeAd", "=OK=")
        val builder = AdLoader.Builder(activity, id)
        builder.forUnifiedNativeAd { unifiedNativeAd ->
            val adView = activity.layoutInflater
                .inflate(R.layout.admob_native_layout, null) as UnifiedNativeAdView
            populateUnifiedNativeAdView(unifiedNativeAd, adView)
            relativeLayout.addView(adView)
        }
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                val error =
                    """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateUnifiedNativeAdView(
        nativeAd: UnifiedNativeAd,
        adView: UnifiedNativeAdView
    ) {
        // Set the media view.
        adView.mediaView =
            adView.findViewById<com.google.android.gms.ads.formats.MediaView>(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
        val destroy = adView.findViewById<Button>(R.id.ad_close)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }
        destroy.setOnClickListener {
            adView.destroy()
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeAd.videoController
        if (vc.hasVideoContent()) {
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    super.onVideoEnd()
                }
            }
        } else {
        }
    }

    // FB Native Banner Ads
    fun loadNativeBannerFBAd(context: Activity, layNative: FrameLayout) {
        val pref = SaveSharedPreference(context).getPreferences(context)
        val isAdsShow = pref.getBoolean(Constants.IS_ADS_SHOW, false)
        val isFacebookAds = pref.getBoolean(Constants.IS_FACEBOOK_ADS, false)
        val isAdmob = pref.getBoolean(Constants.IS_ADMOB, false)
        val banner_ad = pref.getString(Constants.BANNER_AD, "")
        val fb_native_banner_ad = pref.getString(Constants.FB_NATIVE_BANNER_AD, "")

        if (isAdsShow) {
            if (isFacebookAds && !fb_native_banner_ad.isNullOrEmpty()) {
                val nativeAd = NativeBannerAd(context, fb_native_banner_ad)
                val nativeAdListener: NativeAdListener = object : NativeAdListener {
                    override fun onMediaDownloaded(ad: Ad) {}
                    override fun onError(ad: Ad, adError: AdError) {
                        Log.e("FB-NATIVE-BANNER-ADS", "Error = ${adError.errorMessage}")
                        if (isAdmob) {
                            banner_ad?.let { loadAdMobBannerAd(context, layNative, it) }
                        } else {
                            Log.e("isFacebookAds", isFacebookAds.toString())
                        }
                    }

                    override fun onAdLoaded(ad: Ad) {
                        Log.e("FB-NATIVE-BANNER-ADS", "LOADED")
                        val inflater = context.layoutInflater
                        val view = inflater.inflate(R.layout.native_ad_layout, null)
                        val nativeAdLayout =
                            view.findViewById<NativeAdLayout>(R.id.native_ad_container)
                        layNative.removeAllViews()
                        layNative.addView(view, 0)
                        layNative.visibility = View.VISIBLE
                        nativeAdLayout.visibility = View.VISIBLE

                        inflateAdNativeBanner(context, nativeAd, nativeAdLayout)

                        nativeAd.downloadMedia()
                    }

                    override fun onAdClicked(ad: Ad) {}
                    override fun onLoggingImpression(ad: Ad) {}
                }
                nativeAd.loadAd(
                    nativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
                        .build()
                )
            } else if (isAdmob) {
                banner_ad?.let { loadAdMobBannerAd(context, layNative, it) }
            } else {
                Log.e("both", isFacebookAds.toString())
            }
        } else {
            Log.e(Constants.IS_ADS_SHOW, isAdsShow.toString())
        }
    }

    private fun inflateAdNativeBanner(
        context: Activity,
        nativeBannerAd: NativeBannerAd,
        nativeAdLayout: NativeAdLayout
    ) {
        try {
            nativeBannerAd.unregisterView()
            // Add the Ad view into the ad container.
//            nativeAdLayout = context.findViewById(R.id.native_banner_ad_container);
            val inflater = LayoutInflater.from(context)
            // Inflate the Ad view.  The layout referenced is the one you created in the last step.
            val adView = inflater.inflate(R.layout.native_banner_ad_view, nativeAdLayout, false)
            nativeAdLayout.addView(adView)

            // Add the AdChoices icon
            val adChoicesContainer = adView.findViewById<RelativeLayout>(R.id.ad_choices_container)
            if (adChoicesContainer != null) {
                val adOptionsView = AdOptionsView(context, nativeBannerAd, nativeAdLayout)
                adChoicesContainer.removeAllViews()
                adChoicesContainer.addView(adOptionsView, 0)

                // Create native UI using the ad metadata.
                val nativeAdTitle = adView.findViewById<TextView>(R.id.native_ad_title)
                val nativeAdSocialContext =
                    adView.findViewById<TextView>(R.id.native_ad_social_context)
                val sponsoredLabel = adView.findViewById<TextView>(R.id.native_ad_sponsored_label)
                val nativeAdIconView = adView.findViewById<MediaView>(R.id.native_icon_view)
                val nativeAdCallToAction =
                    adView.findViewById<Button>(R.id.native_ad_call_to_action)

                // Set the Text.
                nativeAdCallToAction.text = nativeBannerAd.adCallToAction
                nativeAdCallToAction.visibility =
                    if (nativeBannerAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
                nativeAdTitle.text = nativeBannerAd.advertiserName
                nativeAdSocialContext.text = nativeBannerAd.adSocialContext
                sponsoredLabel.text = nativeBannerAd.sponsoredTranslation

                // Register the Title and CTA button to listen for clicks.
                val clickableViews: MutableList<View> = ArrayList()
                clickableViews.add(nativeAdTitle)
                clickableViews.add(nativeAdCallToAction)
                nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadAdMobBannerAd(activity: Activity, relativeLayout: FrameLayout, id: String) {
        Log.e("loadAdMobBannerAd", "=OK=")
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = outMetrics.density
        var adWidthPixels = relativeLayout.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }
        val adWidth = (adWidthPixels / density).toInt()
        val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            activity,
            adWidth
        )

        val mAdView = AdView(activity.applicationContext)
        mAdView.setAdSize(adSize)
        mAdView.adUnitId = id
        (relativeLayout as FrameLayout).addView(mAdView)
        MobileAds.initialize(activity)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }
}