package com.example.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.News
import com.example.newsapp.R
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.model.NewsResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val apiKey = "a0c586ca2fea78a4ca8535e98c55f4af"
    private lateinit var recyclerView: RecyclerView

    companion object {
        private const val TAG = "NEWS_APP"
    }

    // SEARCH MENU
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

            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        return true
    }

    // SEARCH NEWS
    private fun searchNews(query: String) {

        RetrofitInstance.api.searchNews(query, "en", 10, apiKey)
            .enqueue(object : Callback<NewsResponse> {

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

                        recyclerView.adapter = NewsAdapter(newsList) {
                            openDetail(it)
                        }
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    // OPEN DETAIL SCREEN
    private fun openDetail(news: News) {
        val intent = Intent(this, NewsDetailActivity::class.java)
        intent.putExtra("title", news.title)
        intent.putExtra("description", news.description)
        intent.putExtra("imageUrl", news.imageUrl)
        startActivity(intent)
    }

    // LOAD DEFAULT NEWS (HOME)
    private fun loadTopHeadlines() {

        RetrofitInstance.api.getTopHeadlines("in", "en", 10, apiKey)
            .enqueue(object : Callback<NewsResponse> {

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

                        recyclerView.adapter = NewsAdapter(newsList) {
                            openDetail(it)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TOOLBAR
        val toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "News"

        // RECYCLER VIEW
        recyclerView = findViewById(R.id.newsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // OPTIONAL: BOTTOM NAVIGATION (only if exists in layout)
        val bottomNav = findViewById<BottomNavigationView?>(R.id.bottomNav)

        bottomNav?.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.home -> {
                    loadTopHeadlines()
                    true
                }

                R.id.bookmark -> {
                    // will implement later
                    true
                }

                else -> false
            }
        }

        // LOAD DEFAULT NEWS
        loadTopHeadlines()
    }
}
