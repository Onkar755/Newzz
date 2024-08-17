package com.example.newzz.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.newzz.model.Article

@Dao
interface ArticleDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<Article>)

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()

    @Query("SELECT * FROM articles WHERE isSaved = 1")
    fun getSavedArticles(): LiveData<List<Article>>

    @Query("SELECT * FROM articles WHERE category = :category")
    suspend fun getSavedArticlesByCategorySync(category: String): List<Article>

    @Update
    suspend fun updateArticle(article: Article)

    @Query("SELECT * FROM articles WHERE category = 'top' AND category = 'today_popular'")
    fun getTopArticles(): LiveData<List<Article>>

    @Query("SELECT * FROM articles WHERE category = 'searched'")
    fun getSearchedArticles(): LiveData<List<Article>>

    @Query("SELECT * FROM articles WHERE category = 'today_popular'")
    fun getTodayPopularArticles(): LiveData<List<Article>>

    @Query("DELETE FROM articles WHERE isSaved = 0 AND category = :category")
    suspend fun deleteArticlesByCategory(category: String)
}