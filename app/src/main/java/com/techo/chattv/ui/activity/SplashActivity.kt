package com.techo.chattv.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.techo.chattv.databinding.ActivitySplashBinding
import com.techo.chattv.utils.CommonFunction
import com.techo.chattv.utils.IPTvRealm

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        CommonFunction().setAdsData(this)
        val ipTvRealm = IPTvRealm()
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.BLACK
        Handler(Looper.getMainLooper()).postDelayed({
            if (ipTvRealm.allChannelCount() > 0) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, CountrySelectActivity::class.java))
            }
        },3000)
    }
}