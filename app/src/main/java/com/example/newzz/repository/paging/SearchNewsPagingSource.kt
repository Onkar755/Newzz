package com.example.newzz.repository.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newzz.api.NewsAPI
import com.example.newzz.db.ArticleDAO
import com.example.newzz.model.Article

class SearchNewsPagingSource(
    private val query: String,
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
               articleDAO.deleteArticlesByCategory("searched")
           }
            val response = api.getSearchedNews(query, page)
            val articles = response.body()?.articles?.filterNotNull() ?: emptyList()
            Log.d("SearchNewsPagingSource", "Paged $page !! Articles received: ${articles.size}")

            val categorizedArticles = articles.map { it.copy(category = "searched") }
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