package com.example.newsapp.api

import com.example.newsapp.model.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("top-headlines")
    fun getTopHeadlines(
        @Query("country") country: String,
        @Query("lang") lang: String,
        @Query("max") max: Int,
        @Query("apikey") apiKey: String
    ): Call<NewsResponse>
}
