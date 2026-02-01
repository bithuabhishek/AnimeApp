package com.abhishek.animeapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhishek.animeapp.data.api.RetrofitClient
import com.abhishek.animeapp.data.repository.AnimeRepository
import com.abhishek.animeapp.data.util.NetworkUtils
import com.abhishek.animeapp.data.util.UiEvent
import com.abhishek.animeapp.ui.list.AnimeAdapter
import com.abhishek.animeapp.viewmodel.AnimeViewModel
import com.abhishek.animeapp.viewmodel.factory.AnimeViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: AnimeViewModel by viewModels {
        provideAnimeViewModelFactory()
    }

    private lateinit var animeAdapter: AnimeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        setupSearchBar()
        setupRecyclerView()
        observeFlows()

        viewModel.loadNextPage(
            NetworkUtils.isInternetAvailable(this)
        )
    }


    // ---------------- UI ----------------

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.toolbar).title = "Top Animes"
    }

    private fun setupSearchBar() {
        val searchEditText = findViewById<TextView>(R.id.etSearch)
        searchEditText.addTextChangedListener {
            animeAdapter.filter.filter(it.toString())
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        animeAdapter = AnimeAdapter { anime ->
            openAnimeDetailScreen(anime.id, anime.title)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = animeAdapter
            addOnScrollListener(createPaginationScrollListener())
        }
    }

    // ---------------- FLOW OBSERVERS ----------------

    private fun observeFlows() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.animeList.collectLatest { list ->
                        animeAdapter.updateList(list)
                    }
                }

                launch {
                    viewModel.uiEvent.collectLatest { event ->
                        when (event) {

                            is UiEvent.Loading -> {
                                progressBar.visibility =
                                    if (event.isLoading) View.VISIBLE else View.GONE
                            }

                            is UiEvent.ShowToast -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    event.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    // ---------------- PAGINATION ----------------

    private fun createPaginationScrollListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)

                if (animeAdapter.isSearchActive()) return

                val layoutManager =
                    recyclerView.layoutManager as LinearLayoutManager

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItem =
                    layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItem >= totalItemCount - 5) {
                    viewModel.loadNextPage(
                        NetworkUtils.isInternetAvailable(this@MainActivity)
                    )
                }
            }
        }
    }

    // ---------------- NAVIGATION ----------------

    private fun openAnimeDetailScreen(id: Int, title: String) {
        startActivity(
            Intent(this, AnimeDetailActivity::class.java).apply {
                putExtra("anime_id", id)
                putExtra("anime_name", title)
            }
        )
    }

    // ---------------- FACTORY ----------------

    private fun provideAnimeViewModelFactory(): AnimeViewModelFactory {
        val app = application as MyApplication

        return AnimeViewModelFactory(
            repository = AnimeRepository(
                apiService = RetrofitClient.apiService,
                animeDao = app.database.animeDao(),
                logger = app.logger
            ),
            logger = app.logger
        )
    }
}
