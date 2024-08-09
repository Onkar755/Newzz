package com.example.newzz.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newzz.adapter.NewsAdapter
import com.example.newzz.adapter.OnItemClickListener
import com.example.newzz.api.NewsAPI
import com.example.newzz.databinding.FragmentSavedNewsBinding
import com.example.newzz.db.ArticleDatabase
import com.example.newzz.model.Article
import com.example.newzz.repository.NewsRepository
import com.example.newzz.viewmodel.NewsViewModel
import com.example.newzz.viewmodel.NewsViewModelFactory

class SavedNewsFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentSavedNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsViewModel: NewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedNewsBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val api = NewsAPI()
        val articleDAO = ArticleDatabase.invoke(requireContext()).getArticleDao()
        val repository = NewsRepository(api, articleDAO)
        val newsViewModelFactory = NewsViewModelFactory(repository)
        newsViewModel =
            ViewModelProvider(requireActivity(), newsViewModelFactory)[NewsViewModel::class.java]
        binding.savedNews = newsViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsAdapter = NewsAdapter(this)
        binding.rvSavedNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
            setHasFixedSize(true)
        }

        newsViewModel.savedArticles.observe(viewLifecycleOwner, Observer { articles ->
            Log.d("SavedNewsFragment", "Articles received: ${articles.size}")
            newsAdapter.submitData(lifecycle, PagingData.from(articles))
        })
    }

    override fun onSaveStateClick(article: Article) {
        Log.d("SavedNewsFragment", "Called -> saveArticle")
        newsViewModel.saveStateChange(article)
    }

    override fun onItemClick(article: Article) {
        val source = article.source
        if (source!!.id.isEmpty()) {
            Log.e("Error", "Article source ID is null or empty. Setting a default value.")
            article.source = source.copy(id = "default_id")
        }
        val action =
            SavedNewsFragmentDirections.actionSavedNewsFragmentToNewsArticleFragment(article)
        findNavController().navigate(action)

    }
}