package com.techo.chattv.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techo.chattv.utils.IPTvRealm

@RequiresApi(Build.VERSION_CODES.N)
class CategoriesViewModel : ViewModel() {
    val categoriesLiveData: MutableLiveData<List<String>> = MutableLiveData()

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setCategoriesLiveData() {
        val ipTvRealm = IPTvRealm()
        val channelList:ArrayList<String> = ArrayList()
        if (ipTvRealm.getCategories() != null && ipTvRealm.getCategories()!!.isNotEmpty()) {
            ipTvRealm.getCategories()!!.forEach {
                if (!it.contains("user-agent")){
                    channelList.add(it)
                }
            }
            categoriesLiveData.value = channelList
            Log.e("CATEGORY", channelList.toString())
        }
    }

    init {
        setCategoriesLiveData()
    }
}