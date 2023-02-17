package com.techo.chattv.ui.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.techo.chattv.R
import com.techo.chattv.adapter.SearchAdapter
import com.techo.chattv.databinding.ActivityMainBinding
import com.techo.chattv.ui.fragment.AboutAppFragment
import com.techo.chattv.ui.fragment.CategoriesFragment
import com.techo.chattv.ui.fragment.FavoriteFragment
import com.techo.chattv.utils.IPTvRealm
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView


class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private var ipTvRealm: IPTvRealm? = null
    private lateinit var adapter: SearchAdapter
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
    }

    private fun initialize() {
        ipTvRealm = IPTvRealm()
//        val pref: SharedPreferences = CommonFunction().getPreferences(this)
//        val countryName = pref.getString("Country", "").toString()
//        binding.mainChannelSize.text = countryName
        openFragment(CategoriesFragment.newInstance())
        //        binding.mainChannelSize.setText(String.format(getString(R.string.channel_count), ipTvRealm.allChannelCount()));
        bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(this)

//        val searchView = binding.mainSearchView
//        val id = searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
//        val searchEditText = searchView.findViewById<View>(id) as TextView
//        searchEditText.setTextColor(Color.WHITE)
//        searchEditText.setHintTextColor(Color.WHITE)
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(s: String): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(s: String): Boolean {
//                if (s.isNotEmpty()) {
//                    searchSet(s)
//                } else {
//                    searchLinearGone()
//                }
//                return false
//            }
//        })

        supportFragmentManager.addOnBackStackChangedListener {
            when (supportFragmentManager.findFragmentById(R.id.bottomFrameLayout)) {
                is FavoriteFragment -> {
                    bottomNavigationView.selectedItemId = R.id.favoriteMenu
                }
                is AboutAppFragment -> {
                    bottomNavigationView.selectedItemId = R.id.aboutMenu
                }
                else -> {
                    bottomNavigationView.selectedItemId = R.id.mainMenu
                }
            }
        }
    }

//    private fun searchSet(searchKey: String) {
//        val channels: List<Channel> = ipTvRealm!!.searchChannel(searchKey)
//        if (channels.isNotEmpty()) {
//            adapter = SearchAdapter(this@MainActivity, channels)
//            binding.searchRecycler.adapter = adapter
//            binding.searchRecycler.layoutManager = LinearLayoutManager(this)
//            searchLinearVisible()
//            adapter.onChannelClick = {
//                val intent = Intent(this@MainActivity, PlayerActivity::class.java)
//                intent.putExtra(CommonFunction().CHANNEL, it)
//                startActivity(intent)
//            }
//        }
//    }
//
//    private fun searchLinearGone() {
//        binding.searchLinear.visibility = View.GONE
//    }
//
//    private fun searchLinearVisible() {
//        binding.searchLinear.visibility = View.VISIBLE
//    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.bottomFrameLayout.id, fragment)
        transaction.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mainMenu -> {
                openFragment(CategoriesFragment.newInstance())
                true
            }
            R.id.favoriteMenu -> {
                openFragment(FavoriteFragment.newInstance())
                true
            }
            R.id.aboutMenu -> {
                openFragment(AboutAppFragment.newInstance())
                true
            }
            else -> {
                false
            }
        }
    }
}