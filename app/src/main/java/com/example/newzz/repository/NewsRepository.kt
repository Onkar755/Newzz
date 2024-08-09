package com.example.newzz.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.newzz.api.NewsAPI
import com.example.newzz.db.ArticleDAO
import com.example.newzz.model.Article
import com.example.newzz.repository.paging.SearchNewsPagingSource
import com.example.newzz.repository.paging.TopNewsPagingSource
import kotlinx.coroutines.flow.Flow

class NewsRepository(
    private val api: NewsAPI,
    private val articleDAO: ArticleDAO
) {
    fun getTopNews(): Flow<PagingData<Article>> = Pager(
        config = PagingConfig(
            pageSize = 50,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { TopNewsPagingSource(api, articleDAO) }
    ).flow

    fun getSearches(query: String): Flow<PagingData<Article>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { SearchNewsPagingSource(query, api, articleDAO) }
    ).flow

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