package com.example.newzz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newzz.api.NewsAPI
import com.example.newzz.databinding.ActivityMainBinding
import com.example.newzz.db.ArticleDatabase
import com.example.newzz.repository.NewsRepository
import com.example.newzz.viewmodel.NewsViewModel
import com.example.newzz.viewmodel.NewsViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    lateinit var binding: ActivityMainBinding
    private lateinit var newsViewModel: NewsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val api = NewsAPI()
        val articleDAO = ArticleDatabase.invoke(this).getArticleDao()
        val repository = NewsRepository(api, articleDAO)
        val newsViewModelFactory = NewsViewModelFactory(repository)
        newsViewModel =
            ViewModelProvider(this, newsViewModelFactory)[NewsViewModel::class.java]

        val toolbarDefault: Toolbar = binding.toolbarDefault.toolbarDefault
        val toolbarDetail: Toolbar = binding.toolbarDetail.toolbarDetail

        setSupportActionBar(toolbarDefault)

        bottomNavigationView = binding.bottomNavigation
        navController = findNavController(R.id.fragment_main)
        appBarConfiguration =
            AppBarConfiguration(
                setOf(
                    R.id.topNewsFragment,
                    R.id.searchNewsFragment,
                    R.id.savedNewsFragment
                )
            )

        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.topNewsFragment,
                R.id.searchNewsFragment,
                R.id.savedNewsFragment -> {
                    // Show default toolbar
                    if (toolbarDetail.visibility == View.VISIBLE) {
                        toolbarDetail.visibility = View.GONE
                        toolbarDefault.visibility = View.VISIBLE
                    }
                    bottomNavigationView.visibility = View.VISIBLE

                    when (destination.id) {
                        R.id.topNewsFragment -> toolbarDefault.findViewById<TextView>(R.id.tvTitleTBM).text =
                            "Newzz"

                        R.id.searchNewsFragment -> toolbarDefault.findViewById<TextView>(R.id.tvTitleTBM).text =
                            "Discover"

                        R.id.savedNewsFragment -> toolbarDefault.findViewById<TextView>(R.id.tvTitleTBM).text =
                            "Saved"
                    }
                }

                R.id.newsArticleFragment -> {
                    if (toolbarDefault.visibility == View.VISIBLE) {
                        toolbarDefault.visibility = View.GONE
                        toolbarDetail.visibility = View.VISIBLE
                    }
                    bottomNavigationView.visibility = View.GONE
                }
            }
        }

        bottomNavigationView.setupWithNavController(navController)

        val backButton = toolbarDetail.findViewById<ImageButton>(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}