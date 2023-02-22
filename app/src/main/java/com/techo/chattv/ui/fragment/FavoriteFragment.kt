package com.techo.chattv.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.techo.chattv.adapter.ChannelListAdapter
import com.techo.chattv.databinding.FragmentFavoriteBinding
import com.techo.chattv.model.ChannelList
import com.techo.chattv.ui.activity.VideoPlayerActivity
import com.techo.chattv.utils.CommonFunction
import com.techo.chattv.utils.InterstitialAds
import com.techo.chattv.utils.NativeAds
import com.techo.chattv.viewmodel.FavoriteViewModel

class FavoriteFragment : Fragment() {
    private var mViewModel: FavoriteViewModel? = null
    private lateinit var binding: FragmentFavoriteBinding
    private var adapter: ChannelListAdapter? = null
    lateinit var interstitialAds:InterstitialAds
    private var channelArrayList:ArrayList<ChannelList> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        initialize()
        return binding.root
    }

    private fun initialize() {
        interstitialAds = InterstitialAds(requireActivity())
        interstitialAds.loadInterstitialAds()
        interstitialAds.loadAdmobAds()
        NativeAds().loadNativeBannerFBAd(requireActivity(),binding.bannerFrameLay)

        adapter = ChannelListAdapter(requireActivity(), channelArrayList)
        binding.favoriteRecycler.adapter = adapter
        binding.favoriteRecycler.layoutManager = LinearLayoutManager(context)

        mViewModel!!.favoriteLiveData.observe(viewLifecycleOwner) { channelList ->
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
            if (!channelArrayList.isNullOrEmpty()) {
                binding.favPageTv.visibility = View.GONE
                adapter!!.notifyDataSetChanged()
            }
            adapter!!.onChannelClick = { channel, position ->
                val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
                intent.putExtra("position", position)
                intent.putExtra("list", channel)
                interstitialAds.onStartOtherActivity(intent)
            }
        }

        binding.sideMenu.setOnClickListener {
            CommonFunction().showSideMenu(it,requireActivity(),interstitialAds)
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel!!.favoriteLiveData.observe(viewLifecycleOwner) { channelList ->
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
            if (!channelArrayList.isNullOrEmpty()) {
                binding.favPageTv.visibility = View.GONE
                adapter!!.notifyDataSetChanged()
            } else {
                binding.favPageTv.visibility = View.VISIBLE
            }
            adapter!!.onChannelClick = { channel, position ->
                val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
                intent.putExtra("position", position)
                intent.putExtra("list", channel)
                interstitialAds.onStartOtherActivity(intent)
            }
        }
    }

    companion object {
        fun newInstance(): FavoriteFragment {
            return FavoriteFragment()
        }
    }
}