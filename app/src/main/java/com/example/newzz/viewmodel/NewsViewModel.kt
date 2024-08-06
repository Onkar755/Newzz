package com.example.newzz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newzz.model.Article
import com.example.newzz.repository.NewsRepository
import com.example.newzz.util.Resource
import kotlinx.coroutines.launch

class NewsViewModel(
    private val repository: NewsRepository,
) : ViewModel() {

    // LiveData for top news
    private val _topNews = MutableLiveData<Resource<List<Article>>>()
    val topNews: LiveData<Resource<List<Article>>> = _topNews

    // LiveData for searched news
    private val _searchedNews = MutableLiveData<Resource<List<Article>>>()
    val searchedNews: LiveData<Resource<List<Article>>> = _searchedNews

    init {
        refreshTopNews()
    }

    val savedArticles: LiveData<List<Article>> = repository.getSavedArticles()
    val topArticles: LiveData<List<Article>> = repository.getTopArticles()
    val searchedArticles: LiveData<List<Article>> = repository.getSearchedArticles()

    fun refreshTopNews() {
        _topNews.value = Resource.Loading()
        viewModelScope.launch {
            val response = repository.refreshTopNews()
            response.data?.let { insertArticles(it, "top") }
            _topNews.value = response
        }
    }

    fun getSearches(query: String) {
        _searchedNews.value = Resource.Loading()
        viewModelScope.launch {
            val response = repository.getSearches(query)
            response.data?.let { insertArticles(it, "searched") }
            _searchedNews.value = response
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