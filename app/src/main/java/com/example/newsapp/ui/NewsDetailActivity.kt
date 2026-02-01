package com.example.newsapp.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.newsapp.R

class NewsDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val imageView = findViewById<ImageView>(R.id.detailImage)
        val titleView = findViewById<TextView>(R.id.detailTitle)
        val descView = findViewById<TextView>(R.id.detailDescription)

        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val imageUrl = intent.getStringExtra("imageUrl")

        titleView.text = title
        descView.text = description

        Glide.with(this)
            .load(imageUrl)
            .placeholder(android.R.color.darker_gray)
            .into(imageView)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}