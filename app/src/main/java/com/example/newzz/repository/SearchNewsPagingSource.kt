package com.example.newzz.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newzz.api.NewsAPI
import com.example.newzz.db.ArticleDAO
import com.example.newzz.model.Article

class SearchNewsPagingSource(
    private val query: String,
    private val category: String,
    private val api: NewsAPI,
    private val articleDAO: ArticleDAO,
) : PagingSource<Int, Article>() {
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        if (page == 1) {
            articleDAO.deleteArticlesByCategory(category)
        }
        return try {
            val response = api.getSearchedNews(query, page)
            val articles = response.body()?.articles?.filterNotNull() ?: emptyList()

            val urlsToExclude = setOf("https://removed.com")

            val filteredArticles = articles.filter { article ->
                !urlsToExclude.contains(article.url)
            }

            Log.d("TopNewsPagingSource", "Fetched $page articles: ${articles.size}")

            val categorizedArticles = filteredArticles.map { it.copy(category = category) }

            val savedArticles = articleDAO.getSavedArticlesByCategorySync(category)
            val savedArticlesMap = savedArticles.associateBy { it.url }

            val updatedArticles = categorizedArticles.map {
                it.copy(isSaved = savedArticlesMap[it.url]?.isSaved ?: false)
            }

            articleDAO.insertArticles(updatedArticles)
            Log.d("TopNewsPagingSource", "Inserted articles into DB: ${updatedArticles.size}")

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