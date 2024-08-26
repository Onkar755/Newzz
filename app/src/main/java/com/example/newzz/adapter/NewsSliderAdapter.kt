package com.example.newzz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newzz.databinding.ItemSliderPopularBinding
import com.example.newzz.model.Article

class NewsSliderAdapter(
    private val articles: List<Article>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<NewsSliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(
        val binding: ItemSliderPopularBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = ItemSliderPopularBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SliderViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val article = articles[position]
        holder.binding.article = article
        holder.binding.clickListener = listener
    }
}