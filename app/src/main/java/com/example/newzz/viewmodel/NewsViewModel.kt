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
    private val repository: NewsRepository
) : ViewModel() {

    private val _topNews = MutableLiveData<Resource<List<Article>>>()
    val topNews: LiveData<Resource<List<Article>>>
        get() = _topNews

    private val _searchedNews = MutableLiveData<Resource<List<Article>>>()
    val searchedNews: LiveData<Resource<List<Article>>>
        get() = _searchedNews

    fun refreshTopNews() {
        _topNews.value = Resource.Loading()
        viewModelScope.launch {
            val response = repository.refreshTopNews()
            _topNews.value = response
        }
    }

    fun getSearches(query: String) {
        _searchedNews.value = Resource.Loading()
        viewModelScope.launch {
            val response = repository.getSearches(query)
            _searchedNews.value = response
        }
    }


}