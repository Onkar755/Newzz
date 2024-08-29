package com.example.newzz.screens.explore

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newzz.R
import com.example.newzz.adapter.LoaderAdapter
import com.example.newzz.adapter.NewsAdapter
import com.example.newzz.adapter.OnItemClickListener
import com.example.newzz.adapter.ViewPagerParentNavigator
import com.example.newzz.api.NewsAPI
import com.example.newzz.databinding.FragmentEntertainmentBinding
import com.example.newzz.db.ArticleDatabase
import com.example.newzz.model.Article
import com.example.newzz.repository.NewsRepository
import com.example.newzz.ui.CustomDividerItemDecoration
import com.example.newzz.util.NetworkChecker
import com.example.newzz.viewmodel.NewsViewModel
import com.example.newzz.viewmodel.NewsViewModelFactory

class EntertainmentFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentEntertainmentBinding
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var parentNavigator: ViewPagerParentNavigator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEntertainmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        parentNavigator = parentFragment as ViewPagerParentNavigator

        val api = NewsAPI()
        val articleDAO = ArticleDatabase.invoke(requireContext()).getArticleDao()
        val networkChecker = NetworkChecker(requireContext())
        val repository = NewsRepository(api, articleDAO, networkChecker)
        val newsViewModelFactory = NewsViewModelFactory(repository)
        newsViewModel =
            ViewModelProvider(requireActivity(), newsViewModelFactory)[NewsViewModel::class.java]
        binding.entertainmentsNews = newsViewModel

        newsViewModel.getNews("Films, Series, Entertainment", "entertainment")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsAdapter = NewsAdapter(this)
        binding.rvEntertainment.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter.withLoadStateFooter(
                footer = LoaderAdapter()
            )
            setHasFixedSize(true)
            val marginStart = resources.getDimensionPixelSize(R.dimen.divider_margin_start)
            val marginEnd = resources.getDimensionPixelSize(R.dimen.divider_margin_end)
            addItemDecoration(CustomDividerItemDecoration(requireContext(), marginStart, marginEnd))
        }

        newsViewModel.entertainmentNews.observe(viewLifecycleOwner, Observer { articles ->
            Log.d("SearchNewsFragment", "Articles received: $articles")
            articles?.let {
                Log.d("SearchNewsFragment", "Articles receiveddddd: $it")
                newsAdapter.submitData(lifecycle, it)
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
        parentNavigator.navigateFromPager(article)
    }

    override fun onSaveStateClick(article: Article) {
        newsViewModel.saveStateChange(article)
    }
}