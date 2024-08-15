package com.example.newzz.screens

import android.content.Intent
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.example.newzz.MainActivity
import com.example.newzz.R
import com.example.newzz.adapter.NewsAdapter
import com.example.newzz.api.NewsAPI
import com.example.newzz.databinding.FragmentNewsArticleBinding
import com.example.newzz.databinding.FragmentSavedNewsBinding
import com.example.newzz.db.ArticleDatabase
import com.example.newzz.model.Article
import com.example.newzz.repository.NewsRepository
import com.example.newzz.viewmodel.NewsViewModel
import com.example.newzz.viewmodel.NewsViewModelFactory

class NewsArticleFragment : Fragment() {

    private lateinit var binding: FragmentNewsArticleBinding
    private val args by navArgs<NewsArticleFragmentArgs>()
    private lateinit var newsViewModel: NewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNewsArticleBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val api = NewsAPI()
        val articleDAO = ArticleDatabase.invoke(requireContext()).getArticleDao()
        val repository = NewsRepository(api, articleDAO)
        val newsViewModelFactory = NewsViewModelFactory(repository)
        newsViewModel =
            ViewModelProvider(requireActivity(), newsViewModelFactory)[NewsViewModel::class.java]

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val article: Article = args.article

        val mainActivity = activity as MainActivity
        mainActivity.binding.toolbarDetail.article = article

        val toolbar = mainActivity.binding.toolbarDetail
        val shareButton = toolbar.btnShare
        val saveButton = toolbar.btnSave

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

        saveButton.setOnClickListener {
            newsViewModel.saveStateChange(article)
            saveButton.apply {
                if (article.isSaved) {
                    setImageResource(R.drawable.ic_save_selected)
                } else {
                    setImageResource(R.drawable.ic_save_unselected)
                }
            }
        }

        shareButton.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, article.url.toString())
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share Article"))
        }
    }
}