package com.example.newzz.adapter

import com.example.newzz.model.Article

interface ViewPagerParentNavigator {
    fun navigateFromPager(article: Article)
}