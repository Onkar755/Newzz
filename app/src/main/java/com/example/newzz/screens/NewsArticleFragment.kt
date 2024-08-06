package com.example.newzz.screens

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.example.newzz.R
import com.example.newzz.adapter.NewsAdapter
import com.example.newzz.databinding.FragmentNewsArticleBinding
import com.example.newzz.databinding.FragmentSavedNewsBinding
import com.example.newzz.viewmodel.NewsViewModel

class NewsArticleFragment : Fragment() {

    private lateinit var binding: FragmentNewsArticleBinding
    private val args by navArgs<NewsArticleFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNewsArticleBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val article = args.article

        binding.wvArticle.apply {
            binding.wvArticle.webViewClient = WebViewClient()
            loadUrl(article.url.toString())
            WebView.setWebContentsDebuggingEnabled(true)
            binding.wvArticle.settings.safeBrowsingEnabled = true
            binding.wvArticle.settings.allowFileAccess = false
            binding.wvArticle.settings.allowContentAccess = false
            binding.wvArticle.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            binding.wvArticle.clearCache(true)
            binding.wvArticle.settings.setSupportZoom(false)
            binding.wvArticle.settings.builtInZoomControls = false
            binding.wvArticle.settings.displayZoomControls = false
        }
    }
}