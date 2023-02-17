package com.techo.chattv

import android.app.Application
import androidx.viewbinding.BuildConfig
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import io.realm.Realm
import timber.log.Timber

class IPTVPlayer : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        AudienceNetworkAds.initialize(this);
        //This will make the ad run on the test device, let's say your Android AVD emulator
        if(com.techo.chattv.BuildConfig.DEBUG) {
            AdSettings.setTestMode(true);
            AdSettings.addTestDevice("3813341c-5bbf-4d12-82d6-10ca1ad1abe1");
        }
        MobileAds.initialize(this)
    }
}