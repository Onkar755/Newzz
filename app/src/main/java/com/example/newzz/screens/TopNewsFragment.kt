package com.example.newzz.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.newzz.R
import com.example.newzz.api.NewsAPI
import com.example.newzz.databinding.FragmentTopNewsBinding
import com.example.newzz.repository.NewsRepository
import com.example.newzz.util.Resource
import com.example.newzz.viewmodel.NewsViewModel
import com.example.newzz.viewmodel.NewsViewModelFactory

class TopNewsFragment : Fragment() {

    private lateinit var binding: FragmentTopNewsBinding
    private lateinit var newsViewModel: NewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopNewsBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val api = NewsAPI()
        val repository = NewsRepository(api)
        val newsViewModelFactory = NewsViewModelFactory(repository)
        newsViewModel = ViewModelProvider(this, newsViewModelFactory)[NewsViewModel::class.java]

        binding.topNews = newsViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsViewModel.topNews.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is Resource.Loading -> {
                    Log.d("NewsFragment", "Loading data...")
                }

                is Resource.Success -> {
                    binding.top.text = state.data.toString()
                    Log.d("NewsFragment", "News loaded successfully")
                }

                is Resource.Error -> {
                    Toast.makeText(activity, "Error Occurred!!", Toast.LENGTH_LONG).show()
                    Log.e("NewsFragment", "Error: ${state.message}")
                }
            }
        })

        newsViewModel.refreshTopNews()

    }
}