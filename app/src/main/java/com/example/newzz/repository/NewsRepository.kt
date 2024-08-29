package com.example.newzz.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.newzz.api.NewsAPI
import com.example.newzz.db.ArticleDAO
import com.example.newzz.model.Article
import com.example.newzz.util.NetworkChecker
import kotlinx.coroutines.flow.Flow

class NewsRepository(
    private val api: NewsAPI,
    private val articleDAO: ArticleDAO,
    private val networkChecker: NetworkChecker
) {
    private val isConnected = networkChecker.isNetworkAvailable()

    fun getSearches(query: String, category: String): Flow<PagingData<Article>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SearchNewsPagingSource(
                query,
                category,
                api,
                articleDAO,
                networkChecker
            )
        }
    ).flow

    suspend fun getPopularNews(): List<Article> {
        Log.d("NewsRepository", "getPopularNews")
        return try {
            if (isConnected) {
                val response = api.getTodayPopularNews()
                Log.d("NewsRepository", "Response: ${response.message()}")
                if (response.isSuccessful) {
                    val articles = response.body()?.articles?.filterNotNull() ?: emptyList()
                    Log.d("NewsRepository", "Articles: ${articles.size}")
                    val categorizedArticles = articles.map { it.copy(category = "today_popular") }
                    articleDAO.deleteArticlesByCategory("today_popular")
                    articleDAO.insertArticles(categorizedArticles)
                    categorizedArticles
                } else {
                    Log.e("NewsRepository", "Error: ${response.message()}")
                    emptyList()
                }
            } else {
                articleDAO.getTodayPopularArticles()
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Exception: ${e.message}")
            emptyList()
        }
    }

    fun getSavedArticles(): LiveData<List<Article>> = articleDAO.getSavedArticles()

    suspend fun saveStateChange(article: Article) {
        article.isSaved = !article.isSaved
        articleDAO.updateArticle(article)
    }
}