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
    private val _trendingNews = MutableLiveData<PagingData<Article>>()
    val trendingNews: LiveData<PagingData<Article>> = _trendingNews.cachedIn(viewModelScope)

    // LiveData for searched news
    private val _searchedNews = MutableLiveData<PagingData<Article>>()
    val searchedNews: LiveData<PagingData<Article>> = _searchedNews.cachedIn(viewModelScope)

    // LiveData for sports news
    private val _sportsNews = MutableLiveData<PagingData<Article>>()
    val sportsNews: LiveData<PagingData<Article>> = _sportsNews.cachedIn(viewModelScope)

    // LiveData for politics news
    private val _politicsNews = MutableLiveData<PagingData<Article>>()
    val politicsNews: LiveData<PagingData<Article>> = _politicsNews.cachedIn(viewModelScope)

    // LiveData for entertainment news
    private val _entertainmentNews = MutableLiveData<PagingData<Article>>()
    val entertainmentNews: LiveData<PagingData<Article>> =
        _entertainmentNews.cachedIn(viewModelScope)

    // LiveData for technology news
    private val _techNews = MutableLiveData<PagingData<Article>>()
    val techNews: LiveData<PagingData<Article>> = _techNews.cachedIn(viewModelScope)

    // LiveData for searched news
    private val _popularNews = MutableLiveData<List<Article>>()
    val popularNews: LiveData<List<Article>> = _popularNews

    init {
        getPopularNews()
    }

    val savedArticles: LiveData<List<Article>> = repository.getSavedArticles()

    fun getNews(query: String, category: String) {
        viewModelScope.launch {
            val response = repository.getSearches(query, category)
            when (category) {
                "search" -> response.collect { pagingData ->
                    _searchedNews.postValue(pagingData)
                }

                "sports" -> response.collect { pagingData ->
                    _sportsNews.postValue(pagingData)
                }

                "tech" -> response.collect { pagingData ->
                    _techNews.postValue(pagingData)
                }

                "entertainment" -> response.collect { pagingData ->
                    _entertainmentNews.postValue(pagingData)
                }

                "politics" -> response.collect { pagingData ->
                    _politicsNews.postValue(pagingData)
                }

                "trending" -> response.collect { pagingData ->
                    _trendingNews.postValue(pagingData)
                }
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
}