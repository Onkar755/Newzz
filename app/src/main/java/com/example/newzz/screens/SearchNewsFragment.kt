package com.example.newzz.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newzz.adapter.NewsAdapter
import com.example.newzz.adapter.OnItemClickListener
import com.example.newzz.api.NewsAPI
import com.example.newzz.databinding.FragmentSearchNewsBinding
import com.example.newzz.db.ArticleDatabase
import com.example.newzz.model.Article
import com.example.newzz.repository.NewsRepository
import com.example.newzz.viewmodel.NewsViewModel
import com.example.newzz.viewmodel.NewsViewModelFactory
import kotlinx.coroutines.Job

class SearchNewsFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentSearchNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsViewModel: NewsViewModel
    private var searchJob: Job? = null
    private val debouncePeriod: Long = 500

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchNewsBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val api = NewsAPI()
        val articleDAO = ArticleDatabase.invoke(requireContext()).getArticleDao()
        val repository = NewsRepository(api, articleDAO)
        val newsViewModelFactory = NewsViewModelFactory(repository)
        newsViewModel =
            ViewModelProvider(requireActivity(), newsViewModelFactory)[NewsViewModel::class.java]
        binding.searchNews = newsViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsAdapter = NewsAdapter(this)
        binding.rvSearchNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
            setHasFixedSize(true)
        }

        newsViewModel.searchedNews.observe(viewLifecycleOwner, Observer { articles ->
            Log.d("SearchNewsFragment", "Articles received: $articles")
            articles?.let {
                Log.d("SearchNewsFragment", "Articles receiveddddd: $it")
                newsAdapter.submitData(lifecycle, it)
            }
        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    Log.d("SearchNewsFragment", "Call....")
                    newsViewModel.getSearches(query)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                searchJob?.cancel()
//                searchJob = lifecycleScope.launch {
//                    newText?.let { query ->
//                        delay(debouncePeriod)
//                        Log.d("SearchNewsFragment", "Call....")
//                        newsViewModel.getSearches(query)
//                    }
//                }
                return false
            }
        })
    }

    override fun onItemClick(article: Article) {
        val source = article.source
        if (source != null) {
            if (source.id.isNullOrEmpty()) {
                Log.e("Error", "Article source ID is null or empty. Setting a default value.")
                article.source = source.copy(id = "default_id")
            }
        }

        val action =
            SearchNewsFragmentDirections.actionSearchNewsFragmentToNewsArticleFragment(article)
        findNavController().navigate(action)
    }

    override fun onSaveStateClick(article: Article) {
        newsViewModel.saveStateChange(article)
    }
}