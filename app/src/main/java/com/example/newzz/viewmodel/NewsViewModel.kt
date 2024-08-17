package com.example.newzz.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newzz.model.Article
import com.example.newzz.repository.NewsRepository
import kotlinx.coroutines.launch

class NewsViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    // LiveData for top news
    private val _topNews = MutableLiveData<PagingData<Article>>()
    val topNews: LiveData<PagingData<Article>> = _topNews.cachedIn(viewModelScope)

    // LiveData for searched news
    private val _searchedNews = MutableLiveData<PagingData<Article>>()
    val searchedNews: LiveData<PagingData<Article>> = _searchedNews.cachedIn(viewModelScope)

    // LiveData for searched news
    private val _popularNews = MutableLiveData<List<Article>>()
    val popularNews: LiveData<List<Article>> = _popularNews

    init {
        refreshTopNews()
        getPopularNews()
    }

    val savedArticles: LiveData<List<Article>> = repository.getSavedArticles()
    val topArticles: LiveData<List<Article>> = repository.getTopArticles()
    val searchedArticles: LiveData<List<Article>> = repository.getSearchedArticles()
    val popularArticles: LiveData<List<Article>> = repository.getTodayPopularArticles()

    fun refreshTopNews() {
        viewModelScope.launch {
            repository.getTopNews().collect { pagingData ->
                _topNews.postValue(pagingData)
            }
        }
    }

    fun getSearches(query: String) {
        viewModelScope.launch {
            repository.getSearches(query).collect { pagingData ->
                _searchedNews.postValue(pagingData)
            }
        }
    }

    fun getPopularNews() {
        viewModelScope.launch {
            Log.d("NewsViewModel", "Calling Repo -> getPopularNews")
            val articles = repository.getPopularNews()
            _popularNews.value = articles
            Log.d("NewsViewModel", "Size - ${articles.size}")
        }
    }

    fun saveStateChange(article: Article) {
        viewModelScope.launch {
            Log.d("NewsViewModel", "Called Repo-> saveArticle")
            repository.saveStateChange(article)
        }
    }

    private fun insertArticles(articles: List<Article>, category: String) {
        viewModelScope.launch {
            repository.insertArticles(articles, category)
        }
    }
}