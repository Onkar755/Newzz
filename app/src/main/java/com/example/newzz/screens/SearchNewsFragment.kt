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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.newzz.R
import com.example.newzz.adapter.ExplorePagerAdapter
import com.example.newzz.adapter.LoaderAdapter
import com.example.newzz.adapter.NewsAdapter
import com.example.newzz.adapter.OnItemClickListener
import com.example.newzz.adapter.ViewPagerParentNavigator
import com.example.newzz.api.NewsAPI
import com.example.newzz.databinding.FragmentSearchNewsBinding
import com.example.newzz.db.ArticleDatabase
import com.example.newzz.model.Article
import com.example.newzz.repository.NewsRepository
import com.example.newzz.ui.CustomDividerItemDecoration
import com.example.newzz.viewmodel.NewsViewModel
import com.example.newzz.viewmodel.NewsViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(), OnItemClickListener, ViewPagerParentNavigator {

    private lateinit var binding: FragmentSearchNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var explorePagerAdapter: ExplorePagerAdapter

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
            adapter = newsAdapter.withLoadStateFooter(
                footer = LoaderAdapter()
            )
            setHasFixedSize(true)

            val marginStart = resources.getDimensionPixelSize(R.dimen.divider_margin_start)
            val marginEnd = resources.getDimensionPixelSize(R.dimen.divider_margin_end)

            addItemDecoration(CustomDividerItemDecoration(requireContext(), marginStart, marginEnd))
        }

        explorePagerAdapter = ExplorePagerAdapter(this)
        binding.explorer.viewPager.adapter = explorePagerAdapter

        TabLayoutMediator(binding.explorer.tabLayout, binding.explorer.viewPager) { tab, position ->
            Log.d("TopNewsFragment", "Mediator $position")
            tab.text = when (position) {
                0 -> "Trending"
                1 -> "Technology"
                2 -> "Sports"
                3 -> "Entertainment"
                4 -> "Politics"
                else -> null
            }
        }.attach()

        newsViewModel.searchedNews.observe(viewLifecycleOwner, Observer { articles ->
            Log.d("SearchNewsFragment", "Articles received: $articles")
            articles?.let {
                Log.d("SearchNewsFragment", "Articles received: $it")
                newsAdapter.submitData(lifecycle, it)
            }
            binding.rvSearchNews.visibility = View.VISIBLE
            binding.explorer.viewPager.visibility = View.GONE
            binding.explorer.tabLayout.visibility = View.GONE
        })

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    newsViewModel.getNews(query, "search")
                    binding.rvSearchNews.visibility = View.VISIBLE
                    binding.explorer.viewPager.visibility = View.GONE
                    binding.explorer.tabLayout.visibility = View.GONE
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(debouncePeriod)
                    newText?.let { query ->
                        if (query.isNotEmpty()) {
                            newsViewModel.getNews(query, "search")
                        } else {
                            // Show explorer if the query is empty
                            binding.rvSearchNews.visibility = View.GONE
                            binding.explorer.viewPager.visibility = View.VISIBLE
                            binding.explorer.tabLayout.visibility = View.VISIBLE
                            binding.searchView.clearFocus()
                        }
                    }
                }
                return true
            }
        })

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus && binding.searchView.query.isNullOrEmpty()) {
                binding.rvSearchNews.visibility = View.GONE
                binding.explorer.viewPager.visibility = View.VISIBLE
                binding.explorer.tabLayout.visibility = View.VISIBLE
                binding.searchView.clearFocus()
            }
        }

        binding.explorer.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.searchView.clearFocus() // Clear focus on page change
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

    override fun navigateFromPager(article: Article) {
        val action =
            SearchNewsFragmentDirections.actionSearchNewsFragmentToNewsArticleFragment(article)
        findNavController().navigate(action)
    }
}