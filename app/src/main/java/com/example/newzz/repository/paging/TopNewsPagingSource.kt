package com.example.newzz.repository.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newzz.api.NewsAPI
import com.example.newzz.db.ArticleDAO
import com.example.newzz.model.Article

class TopNewsPagingSource(
    private val api: NewsAPI,
    private val articleDAO: ArticleDAO
) : PagingSource<Int, Article>() {
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        return try {
            if (page == 1) {
                articleDAO.deleteArticlesByCategory("top")
            }
            val response = api.getTopNews(page)
            val articles = response.body()?.articles?.filterNotNull() ?: emptyList()
            Log.d("TopNewsPagingSource", "Paged $page !! Articles received: ${articles.size}")

            val categorizedArticles = articles.map { it.copy(category = "top") }
            articleDAO.insertArticles(categorizedArticles)

            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}