package com.techo.chattv.model

import com.google.gson.annotations.SerializedName

data class Ads(
    @SerializedName("isAdsShow")
    val isAdsShow: Boolean,
    @SerializedName("isFacebookAds")
    var isFacebookAds: Boolean,
    @SerializedName("isAdmob")
    var isAdmob: Boolean,
    @SerializedName("native_ad")
    var native_ad: String,
    @SerializedName("banner_ad")
    var banner_ad: String,
    @SerializedName("interstitial_ad")
    val interstitial_ad: String,
    @SerializedName("fb_native_ad")
    var fb_native_ad: String,
    @SerializedName("fb_native_banner_ad")
    var fb_native_banner_ad: String,
    @SerializedName("fb_interstitial_ad")
    val fb_interstitial_ad: String,
    @SerializedName("ads_show_count")
    val ads_show_count: Int
)
