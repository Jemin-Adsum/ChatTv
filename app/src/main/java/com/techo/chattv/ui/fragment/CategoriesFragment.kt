package com.techo.chattv.ui.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.techo.chattv.R
import com.techo.chattv.ui.activity.ChannelActivity
import com.techo.chattv.adapter.CategoryAdapter
import com.techo.chattv.adapter.SearchAdapter
import com.techo.chattv.databinding.FragmentCategoriesBinding
import com.techo.chattv.model.Channel
import com.techo.chattv.ui.activity.VideoPlayerActivity
import com.techo.chattv.utils.*
import com.techo.chattv.utils.FileReader
import com.techo.chattv.viewmodel.CategoriesViewModel
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding
    private var mViewModel: CategoriesViewModel? = null
    private lateinit var adapter: CategoryAdapter
    private lateinit var searchAdapter: SearchAdapter
//    var searchCon: ConstraintLayout? = null
    private var ipTvRealm: IPTvRealm? = null
    var fileDownloadStarted = false
    var url = ""
    private lateinit var interstitialAds : InterstitialAds

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        mViewModel = ViewModelProvider(this)[CategoriesViewModel::class.java]
        initialize()
        return binding.root
    }

    private fun initialize() {
        interstitialAds = InterstitialAds(requireActivity())
        interstitialAds.loadAdmobAds()
        interstitialAds.loadInterstitialAds()
        NativeAds().loadNativeBannerFBAd(requireActivity(),binding.bannerFrameLay)
        ipTvRealm = IPTvRealm()
        val pref: SharedPreferences =
            SaveSharedPreference(requireActivity()).getPreferences(requireContext())
        val countryName = pref.getString("Country", "").toString()
        binding.mainChannelSize.text = countryName
        binding.searchUrlBtn.setOnClickListener(View.OnClickListener {
            CommonFunction().exitDialog(requireActivity())
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mViewModel!!.categoriesLiveData.observe(viewLifecycleOwner) { categories ->
                adapter = CategoryAdapter(
                    requireContext(),
                    categories
                )
                binding.recyclerCategories.adapter = adapter
                binding.recyclerCategories.layoutManager = StaggeredGridLayoutManager(
                    3,
                    StaggeredGridLayoutManager.VERTICAL
                )
                adapter.onCategoryClick = {
                    val intent = Intent(context, ChannelActivity::class.java)
                    intent.putExtra(Constants.CATEGORY, it)
                    interstitialAds.onStartOtherActivity(intent)
                }
            }
        }
        val searchView = binding.mainSearchView
        val id =
            searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val searchEditText = searchView.findViewById<View>(id) as TextView
        searchEditText.setTextColor(Color.WHITE)
        searchEditText.setHintTextColor(Color.WHITE)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                if (s.isNotEmpty()) {
                    searchSet(s)
                } else {
                    searchLinearGone()
                }
                return false
            }
        })
    }

    companion object {
        fun newInstance(): CategoriesFragment {
            return CategoriesFragment()
        }
    }

    private fun searchSet(searchKey: String) {
        val channels: List<Channel> = ipTvRealm!!.searchChannel(searchKey)
        if (channels.isNotEmpty()) {
            searchAdapter = SearchAdapter(requireContext(), channels)
            binding.searchRecycler.adapter = searchAdapter
            binding.searchRecycler.layoutManager = LinearLayoutManager(requireContext())
            searchLinearVisible()
            searchAdapter.onChannelClick = {
                val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
                intent.putExtra("list", it)
                interstitialAds.onStartOtherActivity(intent)
//                startActivity(intent)
            }
        }
    }

    private fun searchLinearGone() {
        binding.searchLinear.visibility = View.GONE
    }

    private fun searchLinearVisible() {
        binding.searchLinear.visibility = View.VISIBLE
    }
}