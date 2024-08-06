package com.example.newzz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newzz.R
import com.example.newzz.databinding.ItemArticleBinding
import com.example.newzz.model.Article

class NewsAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Article, NewsAdapter.NewsViewHolder>(DiffUtil) {

    inner class NewsViewHolder(
        val binding: ItemArticleBinding
    ) : RecyclerView.ViewHolder(binding.root)

    companion object DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val holder = DataBindingUtil.inflate<ItemArticleBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_article,
            parent,
            false
        )
        return NewsViewHolder(holder)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article: Article? = getItem(position)
        holder.binding.newsItem = article
        holder.binding.clickListener = listener
        holder.binding.executePendingBindings()
    }
}

interface OnItemClickListener {
    fun onItemClick(article: Article)
}
