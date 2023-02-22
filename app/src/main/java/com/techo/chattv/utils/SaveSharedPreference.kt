package com.techo.chattv.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SaveSharedPreference(activity:Activity) {
    fun getPreferences(context: Context?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun setCountry(context: Context?, country: String?) {
        val editor = getPreferences(context).edit()
        editor.putString("Country", country)
        editor.apply()
    }

    fun setPrivacyPolicy(context: Context, privacy_policy: String) {
        val editor = getPreferences(context).edit()
        editor.putString(Constants.PRIVACY_POLICY, privacy_policy)
        editor.apply()
    }

    fun setAds(
        context: Context,
        isAdsShow: Boolean,
        isFacebookAds: Boolean,
        isAdmob: Boolean,
        native_ad: String,
        banner_ad: String,
        interstitial_ad: String,
        fb_native_ad: String,
        fb_native_banner_ad: String,
        fb_interstitial_ad: String,
        ads_show_count: Int
    ) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(Constants.IS_ADS_SHOW, isAdsShow)
        editor.putBoolean(Constants.IS_FACEBOOK_ADS, isFacebookAds)
        editor.putBoolean(Constants.IS_ADMOB, isAdmob)
        editor.putString(Constants.NATIVE_AD, native_ad)
        editor.putString(Constants.BANNER_AD, banner_ad)
        editor.putString(Constants.INTERSTITIAL_AD, interstitial_ad)
        editor.putString(Constants.FB_NATIVE_AD, fb_native_ad)
        editor.putString(Constants.FB_NATIVE_BANNER_AD, fb_native_banner_ad)
        editor.putString(Constants.FB_INTERSTITIAL_AD, fb_interstitial_ad)
        editor.putInt(Constants.ADS_SHOW_COUNT, ads_show_count)
        editor.apply()
    }
}