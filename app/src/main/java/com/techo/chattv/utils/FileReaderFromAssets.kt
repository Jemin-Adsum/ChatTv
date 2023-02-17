package com.techo.chattv.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.techo.chattv.R
import com.techo.chattv.ui.activity.MainActivity
import com.techo.chattv.model.Channel
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class FileReaderFromAssets(private val activity: Activity, private val fileName: String, val interstitialAds: InterstitialAds) {
    private val EXT_INF_SP = "#EXTINF:-1"
    private val EXT_INF_SP_ = "#EXTINF:0"
    private val KOD_IP_DROP_TYPE = "#KODIPROP:inputstream.adaptive.license_type="
    private val KOD_IP_DROP_KEY = "#KODIPROP:inputstream.adaptive.license_key="
    private val TVG_NAME = "tvg-name="
    private val TVG_LOGO = "tvg-logo="
    private val GROUP_TITLE = "group-title="
    private val COMMA = ","
    private val HTTP = "http://"
    private val HTTPS = "https://"
    private var channelList: MutableList<Channel> = ArrayList()
    fun readFile() {
        try {
            val open = activity.assets.open("$fileName.m3u")
            val bufferedReader: BufferedReader? = BufferedReader(InputStreamReader(open))
            var currentLine: String?
            var channelDrmType = ""
            var channelDrmKey = ""
            var channelName = ""
            var channelGroup = ""
            var channelImg = ""
            var channelUrl = ""
            while (bufferedReader?.readLine().also {
                    currentLine = it
                } != null) {
                try {
                    currentLine = currentLine?.replace("\"".toRegex(), "")
                    if (currentLine?.startsWith(KOD_IP_DROP_TYPE) == true) {
                        channelDrmType =
                            currentLine?.split(KOD_IP_DROP_TYPE)?.toTypedArray()?.get(1)?.trim { it <= ' ' }
                                ?:
                        continue
                    }
                    if (currentLine?.startsWith(KOD_IP_DROP_KEY) == true) {
                        channelDrmKey =
                            currentLine?.split(KOD_IP_DROP_KEY)?.toTypedArray()?.get(1)?.trim { it <= ' ' }
                                ?:
                        continue
                    }
                    if (currentLine?.startsWith(EXT_INF_SP) == true || currentLine?.startsWith(EXT_INF_SP_) == true) {
                        channelName =
                            if (currentLine?.split(TVG_NAME)?.toTypedArray()!!.size > 1) currentLine?.split(TVG_NAME)!!
                                .toTypedArray()[1].split(TVG_LOGO)
                                .toTypedArray()[0] else currentLine?.split(COMMA)!!.toTypedArray()[1]
                        channelGroup =
                            currentLine?.split(GROUP_TITLE)!!.toTypedArray()[1].split(COMMA)
                                .toTypedArray()[0]

                        channelImg =
                            if (currentLine?.split(TVG_LOGO)!!
                                    .toTypedArray().size > 1
                            ) currentLine?.split(
                                TVG_LOGO
                            )!!.toTypedArray()[1].split(GROUP_TITLE).toTypedArray()[0] else ""

                        continue
                    }
                    if (currentLine?.startsWith(HTTP) == true || currentLine?.startsWith(HTTPS) == true) {
                        channelUrl = currentLine as String
                        val channel = Channel(
                            channelName,
                            channelUrl,
                            channelImg,
                            channelGroup,
                            channelDrmKey,
                            channelDrmType
                        )
                        channelList.add(channel)
                    }
                } catch (e: Exception) {
                    Log.e("e", e.message.toString())
                }
            }
        } catch (e: IOException) {
            Timber.e(e)
            CommonFunction().showToast(activity, e.localizedMessage)
            CommonFunction().showToast(activity, e.localizedMessage)
        } finally {
            if (channelList.size > 0) {
                if (IPTvRealm().channelListSave(channelList)) {
                    activity.runOnUiThread {
                        val loc = Locale("", fileName.toUpperCase(Locale.ROOT))
                        SaveSharedPreference(activity).setCountry(activity, loc.displayCountry)
                        interstitialAds.onStartOtherActivity(Intent(activity, MainActivity::class.java))
                    }
                } else {
                    CommonFunction().showToast(
                        activity,
                        activity.getString(R.string.error_save_list)
                    )
                }
//                CommonFunction().showToast(activity, activity.getString(R.string.success_file_read))
            } else {
                CommonFunction().showToast(activity, activity.getString(R.string.error_file_read_exp))
            }
        }
    }

}