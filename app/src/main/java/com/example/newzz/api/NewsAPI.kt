package com.example.newzz.api

import com.example.newzz.model.NewsResponse
import com.example.newzz.util.NewsUtil.Companion.API_KEY
import com.example.newzz.util.NewsUtil.Companion.BASE_URL
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("/v2/everything")
    suspend fun getTopNews(
        @Query("page")
        page: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY,
        @Query("q")
        q: String = "news",
        @Query("language")
        language: String = "en",
        @Query("excludeDomains")
        excludeDomains: String = "yahoo.com,removed.com,npr.org"
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun getSearchedNews(
        @Query("q")
        query: String,
        @Query("page")
        page: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY,
        @Query("language")
        language: String = "en",
        @Query("excludeDomains")
        excludeDomains: String = "yahoo.com,removed.com,npr.org"
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun getTodayPopularNews(
        @Query("page")
        page: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY,
        @Query("pageSize")
        pageSize: Int = 5,
        @Query("domains")
        domain: String = "bbc.co.uk, techcrunch.com",
        @Query("language")
        language: String = "en",
        @Query("sortBy")
        sortBy: String = "popularity",
        @Query("excludeDomains")
        excludeDomains: String = "yahoo.com,removed.com,npr.org"
    ): Response<NewsResponse>

    companion object {
        operator fun invoke(): NewsAPI {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewsAPI::class.java)
        }
    }
}