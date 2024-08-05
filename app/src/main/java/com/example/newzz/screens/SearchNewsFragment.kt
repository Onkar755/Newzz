package com.example.newzz.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newzz.adapter.NewsAdapter
import com.example.newzz.api.NewsAPI
import com.example.newzz.databinding.FragmentSearchNewsBinding
import com.example.newzz.db.ArticleDatabase
import com.example.newzz.repository.NewsRepository
import com.example.newzz.util.Resource
import com.example.newzz.viewmodel.NewsViewModel
import com.example.newzz.viewmodel.NewsViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment() {

    private lateinit var binding: FragmentSearchNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsViewModel: NewsViewModel
    private var searchJob: Job? = null
    private val debouncePeriod: Long = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
            setHasFixedSize(true)
        }

        newsViewModel.searchedArticles.observe(viewLifecycleOwner, Observer { articles ->
            Log.d("SearchNewsFragment", "Articles received: ${articles.size}")
            newsAdapter.submitList(articles)
        })

        newsViewModel.searchedNews.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is Resource.Loading -> {
                    Log.d("SearchNewsFragment", "Loading data...")
                }

                is Resource.Success -> {
                    binding.searchResult.visibility = View.INVISIBLE
                    Log.d("SearchNewsFragment", "News loaded successfully")
                }

                is Resource.Error -> {
                    Toast.makeText(activity, "Error Occurred!!", Toast.LENGTH_SHORT).show()
                    Log.e("SearchNewsFragment", "Error: ${state.message}")
                }
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
                return true
            }
        })
    }


}