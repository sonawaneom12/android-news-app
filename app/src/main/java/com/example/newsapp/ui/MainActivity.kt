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
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import android.view.View


class MainActivity : AppCompatActivity() {

    private val apiKey = "a0c586ca2fea78a4ca8535e98c55f4af"
    private lateinit var recyclerView: RecyclerView
    companion object {
        private const val TAG = "NEWS_APP"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Search news..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchNews(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return true
    }

    private fun searchNews(query: String) {

        RetrofitInstance.api.searchNews(
            query,
            "en",
            10,
            apiKey
        ).enqueue(object : Callback<NewsResponse> {

            override fun onResponse(
                call: Call<NewsResponse>,
                response: Response<NewsResponse>
            ) {
                if (response.isSuccessful) {
                    val articles = response.body()?.articles ?: emptyList()

                    val newsList = articles.map {
                        News(
                            it.title ?: "No Title",
                            it.description ?: "No Description",
                            it.image
                        )
                    }

                    recyclerView.adapter = NewsAdapter(newsList) { selectedNews ->
                        openDetail(selectedNews)
                    }
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun openDetail(news: News) {
        val intent = Intent(this@MainActivity, NewsDetailActivity::class.java)
        intent.putExtra("title", news.title)
        intent.putExtra("description", news.description)
        intent.putExtra("imageUrl", news.imageUrl)
        startActivity(intent)
    }

    fun onCategoryClick(view: View) {
        val keyword = view.tag.toString()
        searchNews(keyword)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.newsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


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