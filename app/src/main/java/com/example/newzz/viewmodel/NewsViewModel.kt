package com.example.newzz.viewmodel

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

    init {
        refreshTopNews()
    }

    val savedArticles: LiveData<List<Article>> = repository.getSavedArticles()
    val topArticles: LiveData<List<Article>> = repository.getTopArticles()
    val searchedArticles: LiveData<List<Article>> = repository.getSearchedArticles()

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

    fun saveArticle(article: Article) {
        viewModelScope.launch {
            repository.saveArticle(article)
        }
    }

    fun unSaveArticle(article: Article) {
        viewModelScope.launch {
            repository.unSaveArticle(article)
        }
    }

    private fun insertArticles(articles: List<Article>, category: String) {
        viewModelScope.launch {
            repository.insertArticles(articles, category)
        }
    }

}