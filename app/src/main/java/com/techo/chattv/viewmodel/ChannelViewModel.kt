package com.techo.chattv.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techo.chattv.model.Channel
import com.techo.chattv.utils.IPTvRealm

class ChannelViewModel(private val category: String) : ViewModel() {
    private val channelLiveData: MutableLiveData<List<Channel>> = MutableLiveData<List<Channel>>()
    private fun setChannelLiveData() {
        val channelList: List<Channel> = IPTvRealm().getCategoriesChannel(
            category
        )
        if (channelList != null && channelList.isNotEmpty()) {
            channelLiveData.setValue(IPTvRealm().getCategoriesChannel(category))
        }
    }

    fun getChannelLiveData(): MutableLiveData<List<Channel>> {
        return channelLiveData
    }

    init {
        setChannelLiveData()
    }
}