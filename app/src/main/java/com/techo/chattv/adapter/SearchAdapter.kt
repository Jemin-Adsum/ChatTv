package com.techo.chattv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.techo.chattv.R
import com.techo.chattv.databinding.ItemSearchBinding
import com.techo.chattv.model.Channel

class SearchAdapter(private val context: Context, val channelList: List<Channel>?) :
    RecyclerView.Adapter<SearchAdapter.SearchVHolder?>() {

    var onChannelClick: (channel: Channel) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVHolder {
        return SearchVHolder(ItemSearchBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: SearchVHolder, position: Int) {
        val channel: Channel = channelList!![position]
        if (channel != null) {
            if (channel.channelImg != null && !channel.channelImg.equals("")) {
                Glide.with(context).load(channel.channelImg).override(200, 200)
                    .placeholder(R.drawable.ic_image).into(holder.binding.searchRowImage)
            }
            holder.binding.searchRowName.text = channel.channelName
            holder.binding.channelLay.setOnClickListener {
                onChannelClick.invoke(channel)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (channelList != null && channelList.isNotEmpty()) {
            channelList.size
        } else {
            0
        }
    }

    inner class SearchVHolder(itemView: ItemSearchBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding: ItemSearchBinding = itemView
    }
}