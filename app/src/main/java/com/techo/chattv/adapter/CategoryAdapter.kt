package com.techo.chattv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techo.chattv.R
import com.techo.chattv.databinding.ItemCategoryBinding

class CategoryAdapter(private val context: Context, private val categories: List<String>?) :
    RecyclerView.Adapter<CategoryAdapter.CategoryVHolder?>() {

    var onCategoryClick: (category: String) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVHolder {
        return CategoryVHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(
                    context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryVHolder, position: Int) {
        val category = categories!![position]
        if (category != null && category != "") {
            holder.binding.categoryRowName.text = category
            when (category) {
                "entertainment", "Entertainment" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_entertainment)
                }
                "music", "Music" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_music)
                }
                "news", "News" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_news)
                }
                "kids", "Kids" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_kids)
                }
                "undefined", "Undefined" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_undefined)
                }
                "general", "General" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_general)
                }
                "lifestyle", "Lifestyle" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_lifestyle)
                }
                "movies", "Movies" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_movies)
                }
                "business", "Business" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_business)
                }
                "documentary", "Documentary" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_documentory)
                }
                "classic", "Classic" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_classic)
                }
                "sports", "Sports" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_sports)
                }
                "religious", "Religious" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_religious)
                }
                "comedy", "Comedy" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_comedy)
                }
                "culture", "Culture" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_culture)
                }
                "science", "Science" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_science)
                }
                "auto", "Auto" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_auto)
                }
                "family", "Family" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_family)
                }
                "weather", "Weather" -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_wheather)
                }
                else -> {
                    holder.binding.categoryImage.setImageResource(R.drawable.cat_undefined)
                }
            }
            holder.binding.categoryLay.setOnClickListener {
                onCategoryClick.invoke(category)
            }
        }

    }

    override fun getItemCount(): Int {
        return if (categories != null && categories.isNotEmpty()) {
            categories.size
        } else {
            0
        }
    }

    inner class CategoryVHolder(itemView: ItemCategoryBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding: ItemCategoryBinding = itemView
    }
}