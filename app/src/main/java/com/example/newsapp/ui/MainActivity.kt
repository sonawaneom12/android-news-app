package com.example.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.News
import com.example.newsapp.ui.NewsDetailActivity
import com.example.newsapp.R
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.model.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "NEWS_APP"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.newsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val apiKey = "a0c586ca2fea78a4ca8535e98c55f4af"

        RetrofitInstance.api.getTopHeadlines("in","en", 10, apiKey)
            .enqueue(object : Callback<NewsResponse> {

                override fun onResponse(
                    call: Call<NewsResponse>,
                    response: Response<NewsResponse>
                ) {
                    Log.d(TAG, "Response code: ${response.code()}")

                    if (response.isSuccessful) {
                        val articles = response.body()?.articles ?: emptyList()
                        Log.d(TAG, "Articles count: ${articles.size}")

                        val newsList = articles.map { article ->
                            News(
                                article.title ?: "No Title",
                                article.description ?: "No Description",
                                article.image
                            )
                        }

                        recyclerView.adapter = NewsAdapter(newsList) { selectedNews ->

                            val intent = Intent(this@MainActivity, NewsDetailActivity::class.java)
                            intent.putExtra("title", selectedNews.title)
                            intent.putExtra("description", selectedNews.description)
                            intent.putExtra("imageUrl", selectedNews.imageUrl)

                            startActivity(intent)
                        }
                    } else {
                        Log.e(TAG, "API error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }
}