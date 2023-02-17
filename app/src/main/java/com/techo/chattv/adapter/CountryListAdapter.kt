package com.techo.chattv.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techo.chattv.databinding.ItemCountryListBinding
import com.techo.chattv.model.Country


class CountryListAdapter(private val context: Context, private var countryList: List<Country>?) :
    RecyclerView.Adapter<CountryListAdapter.CategoryVHolder?>() {

    var onCountryClick: (countryCode:String) -> Unit = {}

    inner class CategoryVHolder(itemView: ItemCountryListBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding: ItemCountryListBinding = itemView
    }

    fun updateList(list: List<Country>?) {
        countryList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVHolder {
        return CategoryVHolder(
            ItemCountryListBinding.inflate(
                LayoutInflater.from(
                    context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryVHolder, position: Int) {
        val country = countryList!![position]
        Log.e("Country Name",country.name)
        holder.binding.countryName.text = country.name
        holder.binding.countryImage.text = country.emoji
        holder.binding.countryLay.setOnClickListener {
            onCountryClick.invoke(country.code.lowercase())
        }
    }

    override fun getItemCount(): Int = countryList!!.size
}