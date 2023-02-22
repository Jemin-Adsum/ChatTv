package com.techo.chattv.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.techo.chattv.databinding.FragmentAboutAppBinding
import com.techo.chattv.utils.CommonFunction
import com.techo.chattv.utils.InterstitialAds
import com.techo.chattv.utils.NativeAds
import java.nio.charset.StandardCharsets

class AboutAppFragment : Fragment() {

    private lateinit var binding: FragmentAboutAppBinding
    private lateinit var interstitialAds : InterstitialAds

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutAppBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init(){
        interstitialAds = InterstitialAds(requireActivity())
        interstitialAds.loadAdmobAds()
        interstitialAds.loadInterstitialAds()
        NativeAds().loadNativeBannerFBAd(requireActivity(),binding.bannerFrameLay)
        NativeAds().loadNativeFBAd(requireActivity(),binding.nativeRelativeLay)
        val htmlText = "<h2>Chat TV</h2> <p>Chat TV app is for watching Live TV.</p> <p>Watch your favorite news, sports, movies and TV shows from various channel sources such as built-in tuner, IP-based tuners, and more and show them instantly on your Android TV.</p> <p>Users can watch Free Many TV Channels on their devices with this app</p> <p>Chat TV lets users access and view TV channels for Free. They can stream shows on their devices directly</p> <p>When you download the Chat TV app, you can enjoy various TV channels categories, such as Sports, Movies, Entertainment, News, Business, Regional, Music, Lifestyle, Kids, and Family. You can watch major matches and events with the 'Chat TV’ service.</p>"
        binding.aboutAppTV.text = HtmlCompat.fromHtml(htmlText, 0);

        binding.sideMenu.setOnClickListener {
            CommonFunction().showSideMenu(it,requireActivity(),interstitialAds)
        }
    }

    companion object {
        fun newInstance(): AboutAppFragment {
            return AboutAppFragment()
        }
    }
}