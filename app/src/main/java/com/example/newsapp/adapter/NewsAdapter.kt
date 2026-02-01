package com.example.newsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.News
import com.example.newsapp.R

class NewsAdapter(
    private val newsList: List<News>,
    private val onItemClick: (News) -> Unit
) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.newsTitle)
        val description: TextView = itemView.findViewById(R.id.newsDescription)
        val image: ImageView = itemView.findViewById(R.id.newsImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]

        holder.title.text = news.title
        holder.description.text = news.description

        Glide.with(holder.itemView.context)
            .load(news.imageUrl)
            .centerCrop()
            .placeholder(android.R.color.darker_gray)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            onItemClick(news)
        }
    }

    override fun getItemCount(): Int = newsList.size
}