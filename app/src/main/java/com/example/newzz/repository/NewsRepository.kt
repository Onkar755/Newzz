package com.example.newzz.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.newzz.api.NewsAPI
import com.example.newzz.db.ArticleDAO
import com.example.newzz.model.Article
import com.example.newzz.util.Resource

class NewsRepository(
    private val api: NewsAPI,
    private val articleDAO: ArticleDAO
) {

    suspend fun refreshTopNews(): Resource<List<Article>> {
        return try {
            val response = api.getTopNews()
            if (response.isSuccessful) {
                val articles = response.body()?.articles?.filterNotNull()
                if (!articles.isNullOrEmpty()) {
                    articleDAO.deleteArticlesByCategory("top")
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
                    articleDAO.deleteArticlesByCategory("searched")
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

    fun getSavedArticles(): LiveData<List<Article>> = articleDAO.getSavedArticles()

    fun getSearchedArticles(): LiveData<List<Article>> = articleDAO.getSearchedArticles()

    fun getTopArticles(): LiveData<List<Article>> = articleDAO.getTopArticles()

    suspend fun saveArticle(article: Article) {
        val updatedArticle = article.copy(isSaved = true)
        articleDAO.updateArticle(updatedArticle)
    }

    suspend fun unSaveArticle(article: Article) {
        val updatedArticle = article.copy(isSaved = false)
        articleDAO.updateArticle(updatedArticle)
    }

    suspend fun insertArticles(articles: List<Article>, category: String) {
        val categorizedArticles = articles.map { it.copy(category = category) }
        articleDAO.insertArticles(categorizedArticles)
    }

}