package com.techo.chattv.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.techo.chattv.databinding.ActivityPrivacyPolicyBinding
import com.techo.chattv.utils.Constants
import com.techo.chattv.utils.NativeAds
import com.techo.chattv.utils.SaveSharedPreference


class PrivacyPolicy : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding
    var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        url =
            SaveSharedPreference(this).getPreferences(this).getString(Constants.PRIVACY_POLICY, "")
                .toString()
        NativeAds().loadNativeBannerFBAd(this, binding.relativeBannerAdsLay)
        binding.back.setOnClickListener {
            this.finish()
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                binding.progressBar.visibility = View.GONE
            }

        }
        // this will load the url of the website
        binding.webView.loadUrl(url)
        // this will enable the javascript settings, it can also allow xss vulnerabilities
        binding.webView.settings.javaScriptEnabled = true
        // if you want to enable zoom feature
        binding.webView.settings.setSupportZoom(true)
    }

    override fun onBackPressed() {
        // if your webview can go back it will go back
        if (binding.webView.canGoBack())
            binding.webView.goBack()
        // if your webview cannot go back
        // it will exit the application
        else
            super.onBackPressed()
    }
}