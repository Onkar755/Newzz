package com.example.newzz.repository

import android.util.Log
import com.example.newzz.api.NewsAPI
import com.example.newzz.model.Article
import com.example.newzz.util.Resource

class NewsRepository(
    private val api: NewsAPI
) {
    suspend fun refreshTopNews(): Resource<List<Article>> {
        return try {
            val response = api.getTopNews()
            if (response.isSuccessful) {
                val articles = response.body()?.articles?.filterNotNull()
                if (!articles.isNullOrEmpty()) {
                    Resource.Success(articles)
                } else {
                    Resource.Error("No data found")
                }
            } else {
                Resource.Error("Error: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
            Resource.Error("Exception occurred: ${e.message}")
        }
    }

    suspend fun getSearches(query: String): Resource<List<Article>> {
        return try {
            val response = api.getSearchedNews(query)
            if (response.isSuccessful) {
                val articles = response.body()?.articles?.filterNotNull()
                if (!articles.isNullOrEmpty()) {
                    Resource.Success(articles)
                } else {
                    Resource.Error("No data found")
                }
            } else {
                Resource.Error("Error: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
            Resource.Error("Exception occurred: ${e.message}")
        }
    }

}