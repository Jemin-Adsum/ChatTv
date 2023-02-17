package com.techo.chattv.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.techo.chattv.R
import com.techo.chattv.model.Channel
import com.techo.chattv.model.ChannelList
import com.techo.chattv.utils.NativeAds


class ChannelListAdapter(private val context: Activity, val channelList: List<ChannelList>?) :
    RecyclerView.Adapter<ChannelListAdapter.MyViewHolder>() {

    var onChannelClick: (channel: Channel, position: Int) -> Unit = { channel: Channel, i: Int -> }
    var ad = 0
    var thiscontext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        return if (viewType == SHOW_ADS) {
//            val view: View =
//                LayoutInflater.from(context).inflate(R.layout.item_native_ad, parent, false)
//            MyViewHolderAd(view)
//        } else {
//            val view: View =
//                LayoutInflater.from(context).inflate(R.layout.item_channel_list, parent, false)
//            MyViewHolder(view)
//        }
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_channel_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {

            Log.e("Adapter", channelList!![position].toString())
            val channel: Channel = channelList[position].channel
            if (channel != null) {
                holder.channelListRowId.text = (position + 1).toString()
                holder.channelListRowName.text = channel.channelName
                if (channel.channelImg != null && !channel.channelImg.equals("")) {
                    Glide.with(context).load(channel.channelImg)
                        .override(60, 60)
                        .placeholder(R.drawable.ic_image)
                        .into(holder.channelListRowImg)
                } else {
                    holder.channelListRowImg
                        .setImageDrawable(
                            ResourcesCompat.getDrawable(
                                context.resources,
                                R.drawable.ic_image, null
                            )
                        )
                }
                holder.channelLay.setOnClickListener {
                    onChannelClick.invoke(channel, position)
                }
            }
            if (channelList[position].ads_show == 1) {
                NativeAds().loadNativeFBAd(context, holder.nativeRelativeLay)
                holder.nativeRelativeLay.visibility = View.VISIBLE
            } else {
                holder.nativeRelativeLay.visibility = View.GONE
            }
        } catch (e:Exception){
            Log.e("Exc",e.message.toString())
        }
    }

    override fun getItemCount(): Int {
        return if (channelList != null && channelList.isNotEmpty()) {
            channelList.size
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
//        var returnValue = 0
        return if (position == 0){
            SHOW_ADS
        } else {
            SHOW_LIST
        }

    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nativeRelativeLay = itemView.findViewById(R.id.native_relative_lay) as RelativeLayout
        val channelListRowImg = itemView.findViewById(R.id.channelListRowImg) as ImageView
        val channelListRowId = itemView.findViewById(R.id.channelListRowId) as TextView
        val channelListRowName = itemView.findViewById(R.id.channelListRowName) as TextView
        val channelLay = itemView.findViewById(R.id.channel_lay) as CardView
    }

    class MyViewHolderAd(view: View) : RecyclerView.ViewHolder(view) {
        val nativeRelativeLay = itemView.findViewById(R.id.native_relative_lay) as RelativeLayout
    }

    private fun adShow(layout: RelativeLayout){
        if (layout.visibility == View.GONE){
            Log.e("adShow","false")
        } else {
            NativeAds().loadNativeFBAd(thiscontext,layout)
        }
    }

    companion object {
        const val SHOW_LIST = 0
        const val SHOW_ADS = 1
    }
}