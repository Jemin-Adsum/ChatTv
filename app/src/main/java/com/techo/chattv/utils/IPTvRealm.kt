package com.techo.chattv.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.techo.chattv.model.Channel
import io.realm.Case
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import java.util.stream.Collectors

class IPTvRealm {
    private var realm: Realm? = null
    fun clearList() {
        realm = ipTvListInstanceRealm()
        realm!!.executeTransaction { realm -> realm.deleteAll() }
    }

    private fun ipTvListInstance() {
        val config = RealmConfiguration.Builder()
            .name("iptv_list.realm")
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()
        realm = Realm.getInstance(config)
    }

    private fun favoriteInstance() {
        val config = RealmConfiguration.Builder()
            .name("iptv_favorite.realm")
            .allowWritesOnUiThread(true)
            .allowQueriesOnUiThread(true)
            .build()
        realm = Realm.getInstance(config)
    }

    private fun ipTvListInstanceRealm(): Realm? {
        ipTvListInstance()
        return realm
    }

    private fun ipTvFavoriteInstanceRealm(): Realm? {
        favoriteInstance()
        return realm
    }

    fun channelListSave(channelList: List<Channel?>): Boolean {
        realm = ipTvListInstanceRealm()
        realm!!.beginTransaction()
        for (chl in channelList) {
            val channel: Channel = realm!!.createObject(Channel::class.java)
            channel.channelName = chl!!.channelName
            channel.channelImg = chl.channelImg
            channel.channelUrl = chl.channelUrl
            channel.channelGroup = chl.channelGroup
            channel.channelDrmKey = chl.channelDrmKey
            channel.channelDrmType = chl.channelDrmType
        }
        realm!!.commitTransaction()
        return true
    }

    val allChannelList: List<Any>
        get() {
            realm = ipTvListInstanceRealm()
            realm!!.beginTransaction()
            val channelList: List<Channel> = realm!!.where(Channel::class.java).findAll()
            realm!!.commitTransaction()
            return channelList
        }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getCategories(): List<String>? {
        realm = ipTvListInstanceRealm()
        realm!!.beginTransaction()
        val categoriesQ = realm!!.where(Channel::class.java)
            .distinct("channelGroup").findAll()
        Log.e("categoriesQ", categoriesQ.toString())
        realm!!.commitTransaction()
        return categoriesQ.stream().distinct().map<String>(Channel::channelGroup)
            .collect(Collectors.toList<String>())
    }

    fun getCategoriesChannel(category: String?): List<Channel> {
        realm = ipTvListInstanceRealm()
        realm!!.beginTransaction()
        val channels: RealmResults<Channel> = realm!!.where(Channel::class.java)
            .equalTo("channelGroup", category).findAll()
        realm!!.commitTransaction()
        return channels
    }

    fun searchChannel(searchKey: String?): List<Channel> {
        realm = ipTvListInstanceRealm()
        realm!!.beginTransaction()
        val channels: RealmResults<Channel> = realm!!.where(Channel::class.java)
            .contains("channelName", searchKey, Case.INSENSITIVE)
            .limit(5)
            .findAll()
        realm!!.commitTransaction()
        return channels
    }

    fun allChannelCount(): Long {
        realm = ipTvListInstanceRealm()
        realm!!.beginTransaction()
        val count: Long = realm!!.where(Channel::class.java).count()
        realm!!.commitTransaction()
        return count
    }

    fun isFavorite(channelName: String?): Boolean {
        realm = ipTvFavoriteInstanceRealm()
        realm!!.beginTransaction()
        val result = realm!!.where(Channel::class.java).equalTo("channelName", channelName)
            .findFirst()
        realm!!.commitTransaction()
        return result != null
    }

    fun setFavorite(chl: Channel): Boolean {
        realm = ipTvFavoriteInstanceRealm()
        realm!!.beginTransaction()
        val channel: Channel = realm!!.createObject(Channel::class.java)
        channel.channelName = chl.channelName
        channel.channelImg = chl.channelImg
        channel.channelUrl = chl.channelUrl
        channel.channelGroup = chl.channelGroup
        channel.channelDrmKey = chl.channelDrmKey
        channel.channelDrmType = chl.channelDrmType
        realm!!.commitTransaction()
        return true
    }

    fun deleteFavorite(channel: Channel): Boolean {
        realm = ipTvFavoriteInstanceRealm()
        realm!!.beginTransaction()
        val result: Boolean =
            realm!!.where(Channel::class.java).equalTo("channelName", channel.channelName)
                .findAll().deleteFirstFromRealm()
        realm!!.commitTransaction()
        return result
    }

    val favoriteList: List<Any>
        get() {
            realm = ipTvFavoriteInstanceRealm()
            realm!!.beginTransaction()
            val channel: RealmResults<Channel> = realm!!.where(Channel::class.java).findAll()
            realm!!.commitTransaction()
            return channel
        }
}