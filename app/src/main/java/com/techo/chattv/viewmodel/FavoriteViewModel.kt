package com.techo.chattv.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techo.chattv.model.Channel
import com.techo.chattv.utils.IPTvRealm

class FavoriteViewModel : ViewModel() {
    private val listFavoriteLiveData: MutableLiveData<List<Channel>> = MutableLiveData<List<Channel>>()
    private val ipTvRealm: IPTvRealm = IPTvRealm()
    private fun setLiveData() {
        val channelList: List<Channel> = ipTvRealm.favoriteList as List<Channel>
        if (channelList != null) {
            listFavoriteLiveData.value = channelList
        }
    }

    val favoriteLiveData: MutableLiveData<List<Channel>>
        get() = listFavoriteLiveData

    fun updateFavorite() {
        val channelList: List<Channel> = ipTvRealm.favoriteList as List<Channel>
        if (channelList != null) {
            listFavoriteLiveData.value = channelList
        }
    }

    init {
        setLiveData()
    }
}