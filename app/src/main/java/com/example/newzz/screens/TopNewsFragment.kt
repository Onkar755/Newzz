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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newzz.R
import com.example.newzz.adapter.LoaderAdapter
import com.example.newzz.adapter.NewsAdapter
import com.example.newzz.adapter.OnItemClickListener
import com.example.newzz.api.NewsAPI
import com.example.newzz.databinding.FragmentTopNewsBinding
import com.example.newzz.db.ArticleDatabase
import com.example.newzz.model.Article
import com.example.newzz.repository.NewsRepository
import com.example.newzz.ui.CustomDividerItemDecoration
import com.example.newzz.viewmodel.NewsViewModel
import com.example.newzz.viewmodel.NewsViewModelFactory

class TopNewsFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentTopNewsBinding
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopNewsBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val api = NewsAPI()
        val articleDAO = ArticleDatabase.invoke(requireContext()).getArticleDao()
        val repository = NewsRepository(api, articleDAO)
        val newsViewModelFactory = NewsViewModelFactory(repository)
        newsViewModel =
            ViewModelProvider(requireActivity(), newsViewModelFactory)[NewsViewModel::class.java]
        binding.topNews = newsViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsAdapter = NewsAdapter(this)
        binding.rvTopNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter.withLoadStateFooter(
                footer = LoaderAdapter()
            )
            setHasFixedSize(true)

            val marginStart = resources.getDimensionPixelSize(R.dimen.divider_margin_start)
            val marginEnd = resources.getDimensionPixelSize(R.dimen.divider_margin_end)

            addItemDecoration(CustomDividerItemDecoration(requireContext(), marginStart, marginEnd))
        }

        newsViewModel.topNews.observe(viewLifecycleOwner, Observer { articles ->
            Log.d("TopNewsFragment", "Articles received: $articles")
            articles?.let {
                binding.srlTop.isRefreshing = false
                newsAdapter.submitData(lifecycle, it)
            }
        })

        binding.srlTop.setOnRefreshListener {
            newsViewModel.refreshTopNews()
        }
    }

    override fun onSaveStateClick(article: Article) {
        Log.d("TopNewsFragment", "Called -> saveArticle")
        newsViewModel.saveStateChange(article)
    }

    override fun onItemClick(article: Article) {
        val source = article.source
        if (source != null) {
            if (source.id.isNullOrEmpty()) {
                Log.e("Error", "Article source ID is null or empty. Setting a default value.")
                article.source = source.copy(id = "default_id")
            }
        }
        val action = TopNewsFragmentDirections.actionTopNewsFragmentToNewsArticleFragment(article)
        findNavController().navigate(action)
    }
}