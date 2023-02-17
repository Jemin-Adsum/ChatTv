package com.techo.chattv.ui.activity

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.techo.chattv.adapter.ChannelListAdapter
import com.techo.chattv.databinding.ActivityChannelBinding
import com.techo.chattv.model.ChannelList
import com.techo.chattv.utils.CommonFunction
import com.techo.chattv.utils.Constants
import com.techo.chattv.utils.InterstitialAds
import com.techo.chattv.utils.NativeAds
import com.techo.chattv.viewmodel.ChannelViewFactory
import com.techo.chattv.viewmodel.ChannelViewModel


class ChannelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChannelBinding
    private var channelViewModel: ChannelViewModel? = null
    private lateinit var adapter: ChannelListAdapter
    private var category: String? = null
    lateinit var interstitialAds: InterstitialAds
    private var channelArrayList:ArrayList<ChannelList> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        category = intent.getStringExtra(Constants.CATEGORY)
        if (category != null && category != "") {
            channelViewModel = ViewModelProvider(this, ChannelViewFactory(application, category!!))[ChannelViewModel::class.java]
        }
        initialize()
    }

    private fun initialize() {
        interstitialAds = InterstitialAds(this)
        interstitialAds.loadInterstitialAds()
        interstitialAds.loadAdmobAds()
        NativeAds().loadNativeBannerFBAd(this,binding.bannerFrameLay)

        binding.title.text = category
        CommonFunction().getToolbarStyle(this, binding.channelListToolbar, "")
        setFavoriteData()
    }

    private fun setFavoriteData() {
        adapter = ChannelListAdapter(
            this@ChannelActivity,
            channelArrayList
        )
        binding.channelRecycler.adapter = adapter
        channelViewModel!!.getChannelLiveData().observe(this) { channelList ->
            val size = channelList.size
            channelArrayList.clear()
            channelList.forEachIndexed { index, channel ->
                Log.e("forEachIndexed","INDEX = $index // CHANNEL = ${channel.toString()}")
                when {
                    size == 1 -> {
                        channelArrayList.add(ChannelList(1,channel))
                    }
                    size == 2 -> {
                        channelArrayList.add(ChannelList(1,channel))
                    }
                    size == 3 -> {
                        channelArrayList.add(ChannelList(1,channel))
                    }
                    size == 4 -> {
                        channelArrayList.add(ChannelList(1,channel))
                    }
                    size >= 5 && index == 4 ->{
                        channelArrayList.add(ChannelList(1,channel))
                    }
                    size >= 10 && index == 9 ->{
                        channelArrayList.add(ChannelList(1,channel))
                    }
                    else -> {
                        channelArrayList.add(ChannelList(0,channel))
                    }
                }
            }
            adapter.notifyDataSetChanged()
            adapter.onChannelClick = { channel, position ->
                if(CommonFunction().isOnline(this)) {
                    val intent = Intent(this@ChannelActivity, VideoPlayerActivity::class.java)
                    intent.putExtra("position", position)
                    intent.putExtra("list", channel)
                    interstitialAds.onStartOtherActivity(intent)
                } else {
                    CommonFunction().showToast(this,getString(com.techo.chattv.R.string.network_issue_message))
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.home) {
            super.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}