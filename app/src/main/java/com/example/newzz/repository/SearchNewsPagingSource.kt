package com.example.newzz.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newzz.api.NewsAPI
import com.example.newzz.db.ArticleDAO
import com.example.newzz.model.Article
import com.example.newzz.util.NetworkChecker

class SearchNewsPagingSource(
    private val query: String,
    private val category: String,
    private val api: NewsAPI,
    private val articleDAO: ArticleDAO,
    private val networkChecker: NetworkChecker
) : PagingSource<Int, Article>() {
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        val isConnected = networkChecker.isNetworkAvailable()
        Log.d(
            "SearchNewsPagingSource",
            "Connectivity status: ${if (isConnected) "Available" else "Not Available"}"
        )

        return try {

            if (!isConnected) {
                val articles = articleDAO.getSavedArticlesByCategorySync(category)
                Log.d(
                    "SearchNewsPagingSource",
                    "DB Article -> Fetched $page articles: ${articles.size}"
                )
                return LoadResult.Page(
                    data = articles,
                    prevKey = null,
                    nextKey = null
                )
            }

            if (page == 1) {
                articleDAO.deleteArticlesByCategory(category)
            }
            val response = api.getSearchedNews(query, page)
            val articles = response.body()?.articles?.filterNotNull() ?: emptyList()

            val urlsToExclude = setOf("https://removed.com")

            val filteredArticles = articles.filter { article ->
                !urlsToExclude.contains(article.url)
            }

            Log.d("SearchNewsPagingSource", "$category Fetched $page articles: ${articles.size}")

            val categorizedArticles = filteredArticles.map { it.copy(category = category) }

            val savedArticles = articleDAO.getSavedArticlesByCategorySync(category)
            val savedArticlesMap = savedArticles.associateBy { it.url }

            val updatedArticles = categorizedArticles.map {
                it.copy(isSaved = savedArticlesMap[it.url]?.isSaved ?: false)
            }

            articleDAO.insertArticles(updatedArticles)
            Log.d("SearchNewsPagingSource", "Inserted articles into DB: ${updatedArticles.size}")

            LoadResult.Page(
                data = updatedArticles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}