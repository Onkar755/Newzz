package com.example.newzz.screens

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.newzz.R
import com.example.newzz.adapter.ExplorePagerAdapter
import com.example.newzz.adapter.NewsSliderAdapter
import com.example.newzz.adapter.OnItemClickListener
import com.example.newzz.adapter.ViewPagerParentNavigator
import com.example.newzz.api.NewsAPI
import com.example.newzz.databinding.FragmentHomeNewsBinding
import com.example.newzz.db.ArticleDatabase
import com.example.newzz.model.Article
import com.example.newzz.repository.NewsRepository
import com.example.newzz.util.NetworkChecker
import com.example.newzz.viewmodel.NewsViewModel
import com.example.newzz.viewmodel.NewsViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.properties.Delegates

class HomeNewsFragment : Fragment(), OnItemClickListener, ViewPagerParentNavigator {

    private lateinit var binding: FragmentHomeNewsBinding
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var sliderAdapter: NewsSliderAdapter
    private lateinit var explorePagerAdapter: ExplorePagerAdapter

    private var isConnected by Delegates.notNull<Boolean>()
    private val scrollDelay = 5000L
    private var handler: Handler? = null
    private var autoScrollRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeNewsBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val api = NewsAPI()
        val articleDAO = ArticleDatabase.invoke(requireContext()).getArticleDao()
        val networkChecker = NetworkChecker(requireContext())
        val repository = NewsRepository(api, articleDAO, networkChecker)
        val newsViewModelFactory = NewsViewModelFactory(repository)
        newsViewModel =
            ViewModelProvider(requireActivity(), newsViewModelFactory)[NewsViewModel::class.java]
        binding.topNews = newsViewModel
        isConnected = networkChecker.isNetworkAvailable()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsViewModel.popularNews.observe(viewLifecycleOwner, Observer { articles ->
            Log.d("TopNewsFragment", "Articles received 2: $articles")
            articles?.let {
                sliderAdapter = NewsSliderAdapter(it, this)
                binding.vpPopularToday.adapter = sliderAdapter
                binding.vpPopularToday.currentItem = 0
                stopAutoScroll()
                startAutoScroll()
                binding.srlTop.isRefreshing = false
            }
        })

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

        binding.srlTop.setOnRefreshListener {
            newsViewModel.getPopularNews()

            binding.vpPopularToday.currentItem = 0
            stopAutoScroll()
            startAutoScroll()
        }
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    override fun onResume() {
        super.onResume()
        startAutoScroll()
    }

    private fun startAutoScroll() {
        handler = Handler(Looper.getMainLooper())
        autoScrollRunnable = object : Runnable {
            override fun run() {
                val itemCount = sliderAdapter.itemCount
                if (itemCount > 0) {
                    val nextItem = (binding.vpPopularToday.currentItem + 1) % itemCount
                    if (binding.vpPopularToday.currentItem == itemCount - 1) {
                        binding.vpPopularToday.setCurrentItem(0, true)
                    } else {
                        binding.vpPopularToday.setCurrentItem(nextItem, true)
                    }
                    handler?.postDelayed(this, scrollDelay)
                }
            }
        }
        handler?.postDelayed(autoScrollRunnable!!, scrollDelay)
    }

    private fun stopAutoScroll() {
        handler?.removeCallbacks(autoScrollRunnable!!)
        handler = null
    }

    override fun onSaveStateClick(article: Article) {
        Log.d("TopNewsFragment", "Called -> saveArticle")
        newsViewModel.saveStateChange(article)
    }

    override fun onItemClick(article: Article) {
        if (isConnected) {
            val source = article.source
            if (source != null) {
                if (source.id.isNullOrEmpty()) {
                    Log.e("Error", "Article source ID is null or empty. Setting a default value.")
                    article.source = source.copy(id = "default_id")
                }
            }
            val action =
                HomeNewsFragmentDirections.actionHomeNewsFragmentToNewsArticleFragment(article)
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), "Can't Open! No Internet", Toast.LENGTH_SHORT).show()
        }
    }

    override fun navigateFromPager(article: Article) {
        val action =
            HomeNewsFragmentDirections.actionHomeNewsFragmentToNewsArticleFragment(article)
        findNavController().navigate(action)
    }
}