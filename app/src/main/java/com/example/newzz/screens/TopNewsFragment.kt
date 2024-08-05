package com.example.newzz.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newzz.adapter.NewsAdapter
import com.example.newzz.api.NewsAPI
import com.example.newzz.databinding.FragmentTopNewsBinding
import com.example.newzz.db.ArticleDatabase
import com.example.newzz.repository.NewsRepository
import com.example.newzz.util.Resource
import com.example.newzz.viewmodel.NewsViewModel
import com.example.newzz.viewmodel.NewsViewModelFactory

class TopNewsFragment : Fragment() {

    private lateinit var binding: FragmentTopNewsBinding
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        newsAdapter = NewsAdapter()
        binding.rvTopNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
            setHasFixedSize(true)
        }

        newsViewModel.topArticles.observe(viewLifecycleOwner, Observer { articles ->
            Log.d("TopNewsFragment", "Articles received: ${articles.size}")
            newsAdapter.submitList(articles)
        })

        newsViewModel.topNews.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is Resource.Loading -> {
                    Log.d("TopNewsFragment", "Loading data...")
                }

                is Resource.Success -> {
                    binding.top.visibility = View.INVISIBLE
                    Log.d("TopNewsFragment", "News loaded successfully")
                }

                is Resource.Error -> {
                    Toast.makeText(activity, "Error Occurred!!", Toast.LENGTH_SHORT).show()
                    Log.e("TopNewsFragment", "Error: ${state.message}")
                }
            }
        })
    }
}