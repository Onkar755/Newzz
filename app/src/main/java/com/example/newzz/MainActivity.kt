package com.example.newzz

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newzz.api.NewsAPI
import com.example.newzz.databinding.ActivityMainBinding
import com.example.newzz.db.ArticleDatabase
import com.example.newzz.repository.NewsRepository
import com.example.newzz.util.ConnectivityObserver
import com.example.newzz.util.NetworkChecker
import com.example.newzz.util.NetworkConnectivityObserver
import com.example.newzz.viewmodel.NewsViewModel
import com.example.newzz.viewmodel.NewsViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    lateinit var binding: ActivityMainBinding
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var networkConnectivityObserver: ConnectivityObserver
    private lateinit var networkChecker: NetworkChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkChecker = NetworkChecker(this)
        networkConnectivityObserver = NetworkConnectivityObserver(this)

        val api = NewsAPI()
        val articleDAO = ArticleDatabase.invoke(this).getArticleDao()
        val repository = NewsRepository(api, articleDAO, networkChecker)
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
                    R.id.homeNewsFragment,
                    R.id.searchNewsFragment,
                    R.id.savedNewsFragment
                )
            )

        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeNewsFragment,
                R.id.searchNewsFragment,
                R.id.savedNewsFragment -> {
                    // Show default toolbar
                    if (toolbarDetail.visibility == View.VISIBLE) {
                        toolbarDetail.visibility = View.GONE
                        toolbarDefault.visibility = View.VISIBLE
                    }
                    bottomNavigationView.visibility = View.VISIBLE

                    when (destination.id) {
                        R.id.homeNewsFragment -> toolbarDefault.findViewById<TextView>(R.id.tvTitleTBM).text =
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

        checkInitialNetworkStatus()
        observeNetworkConnectivity()
    }

    private fun checkInitialNetworkStatus() {
        // Initial check for network availability
        val isNetworkAvailable = networkChecker.isNetworkAvailable()

        // Only show "Offline" if no internet at app start
        if (!isNetworkAvailable) {
            binding.internetStatus.text = "Offline"
            binding.internetStatus.setBackgroundColor(
                ContextCompat.getColor(this, R.color.colorSecondary)
            )
            binding.internetStatus.visibility = View.VISIBLE
        } else {
            binding.internetStatus.visibility = View.GONE
        }
    }

    private fun observeNetworkConnectivity() {
        lifecycleScope.launch {
            networkConnectivityObserver.observe().collect { status ->
                when (status) {
                    ConnectivityObserver.Status.Available -> {
                        // Only show "Internet Available" when connection is restored
                        if (binding.internetStatus.visibility == View.VISIBLE) {
                            binding.internetStatus.text = "Internet Available"
                            binding.internetStatus.setBackgroundColor(
                                ContextCompat.getColor(this@MainActivity, R.color.green)
                            )
                            binding.internetStatus.visibility = View.VISIBLE

                            // Hide the status after 5 seconds
                            lifecycleScope.launch {
                                delay(5000)
                                binding.internetStatus.visibility = View.GONE
                            }
                        }
                    }

                    ConnectivityObserver.Status.Lost -> {
                        // Show "Lost Internet Connection" immediately
                        binding.internetStatus.text = "Lost Internet Connection"
                        binding.internetStatus.setBackgroundColor(
                            ContextCompat.getColor(this@MainActivity, R.color.red)
                        )
                        binding.internetStatus.visibility = View.VISIBLE

                        // Wait for 5 seconds, then switch to "Offline"
                        lifecycleScope.launch {
                            delay(5000)
                            binding.internetStatus.visibility = View.VISIBLE
                            binding.internetStatus.text = "Offline"
                            binding.internetStatus.setBackgroundColor(
                                ContextCompat.getColor(this@MainActivity, R.color.colorSecondary)
                            )
                        }
                    }

                    ConnectivityObserver.Status.Unavailable -> {
                        // Directly show "Offline" when the app starts without internet
                        binding.internetStatus.text = "Offline"
                        binding.internetStatus.setBackgroundColor(
                            ContextCompat.getColor(this@MainActivity, R.color.colorSecondary)
                        )
                        binding.internetStatus.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}