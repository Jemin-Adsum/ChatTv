package com.techo.chattv.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import com.google.android.exoplayer2.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.techo.chattv.BuildConfig
import com.techo.chattv.R
import com.techo.chattv.model.Ads
import com.techo.chattv.ui.activity.CountrySelectActivity
import com.techo.chattv.ui.activity.PrivacyPolicy
import java.util.*

class CommonFunction {

    fun setAdsData(activity:Activity){
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                val url = mFirebaseRemoteConfig.getString(Constants.PRIVACY_POLICY)
                SaveSharedPreference(activity).setPrivacyPolicy(activity,url)
                val stringJson = mFirebaseRemoteConfig.getString(Constants.CHAT_TV_ADS)
                if (stringJson.isNotEmpty()) {
                    Log.e("SET DATA","DONE")
                    val adsModel = Gson().fromJson(stringJson, Ads::class.java)
                    Log.e("ADS MODEL",adsModel.toString())
                    SaveSharedPreference(activity).setAds(
                        activity,
                        adsModel.isAdsShow,
                        adsModel.isFacebookAds,
                        adsModel.isAdmob,
                        adsModel.native_ad,
                        adsModel.banner_ad,
                        adsModel.interstitial_ad,
                        adsModel.fb_native_ad,
                        adsModel.fb_native_banner_ad,
                        adsModel.fb_interstitial_ad,
                        adsModel.ads_show_count
                    )
                }
            }
        }
    }

    fun showToast(context: Activity?, message: String?) {
        context!!.runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun getToolbarStyle(activity: AppCompatActivity, toolbar: Toolbar?, title: String?) {
        activity.setSupportActionBar(toolbar)
        Objects.requireNonNull(activity.supportActionBar)?.setHomeButtonEnabled(true)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar!!.title = title
        val upArrow =
            ResourcesCompat.getDrawable(activity.resources, R.drawable.ic_arrow_back, null)
        activity.supportActionBar!!.setHomeAsUpIndicator(upArrow)
    }

    fun exitDialog(context: Activity) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        try {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.setContentView(R.layout.dialog_exit)
        val yesButton = dialog.findViewById<TextView>(R.id.yes_btn)
        val noButton = dialog.findViewById<TextView>(R.id.no_btn)

        yesButton.setOnClickListener {
            IPTvRealm().clearList()
            context.startActivity(Intent(context,CountrySelectActivity::class.java))
            context.finish()
            dialog.dismiss()

        }
        noButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
    fun showSideMenu(v: View,activity: Activity,interstitialAds : InterstitialAds) {
        PopupMenu(activity, v).apply {
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.share_settings -> {
                        try {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name))
                            var shareMessage = activity.getString(R.string.share_app_message)
                            val playStoreLink = activity.getString(R.string.play_store_link)
                            shareMessage =
                                """
                                            ${shareMessage}$playStoreLink${BuildConfig.APPLICATION_ID}

                                            """.trimIndent()
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                            activity.startActivity(Intent.createChooser(shareIntent, Constants.CHOOSE_ONE))
                        } catch (e: Exception) {
                            //e.toString();
                        }
                        true
                    }
                    R.id.privacy_settings -> {
                        val intent = Intent(activity, PrivacyPolicy::class.java)
                        interstitialAds.onStartOtherActivity(intent)
                        true
                    }
                    else -> false
                }
            }
            inflate(R.menu.menu_main)
            show()
        }
    }
}