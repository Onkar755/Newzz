package com.example.newzz.model

data class NewsResponse(
    val articles: List<Article?>?,
    val status: String?,
    val totalResults: Int?
)