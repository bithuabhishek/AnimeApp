package com.abhishek.animeapp

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abhishek.animeapp.data.api.RetrofitClient
import com.abhishek.animeapp.data.repository.AnimeDetailsRepository
import com.abhishek.animeapp.data.util.NetworkUtils
import com.abhishek.animeapp.data.util.UiEvent
import com.abhishek.animeapp.viewmodel.AnimeDetailViewModel
import com.abhishek.animeapp.viewmodel.factory.AnimeDetailViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AnimeDetailActivity : AppCompatActivity() {

    private val viewModel: AnimeDetailViewModel by viewModels {
        provideViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_detail)

        val animeId = intent.getIntExtra("anime_id", -1)
        if (animeId == -1) {
            finish()
            return
        }

        setupToolbar()
        setupWebView()
        observeUiEvents()
        observeAnimeDetails()

        viewModel.loadAnimeDetails(
            animeId,
            NetworkUtils.isInternetAvailable(this)
        )
    }

    // ---------------- TOOLBAR ----------------

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title =
            intent.getStringExtra("anime_name") ?: "Anime Details"

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    // ---------------- WEBVIEW ----------------

    private fun setupWebView() {
        val webView = findViewById<WebView>(R.id.webTrailer)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        webView.webViewClient = WebViewClient()

        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.overScrollMode = View.OVER_SCROLL_NEVER

        webView.setOnTouchListener { _, event ->
            event.action == MotionEvent.ACTION_MOVE
        }
    }

    // ---------------- UI EVENTS ----------------

    private fun observeUiEvents() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collectLatest { event ->
                    when (event) {
                        is UiEvent.Loading -> {
                            progressBar.visibility =
                                if (event.isLoading)
                                    View.VISIBLE
                                else
                                    View.GONE
                        }

                        is UiEvent.ShowToast -> {
                            Toast.makeText(
                                this@AnimeDetailActivity,
                                event.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    // ---------------- STATE ----------------

    private fun observeAnimeDetails() {
        val title = findViewById<TextView>(R.id.txtTitle)
        val rating = findViewById<TextView>(R.id.txtRating)
        val episodes = findViewById<TextView>(R.id.txtEpisodes)
        val genres = findViewById<TextView>(R.id.txtGenres)
        val synopsis = findViewById<TextView>(R.id.txtSynopsis)
        val trailerLabel = findViewById<TextView>(R.id.txtTrailer)
        val webView = findViewById<WebView>(R.id.webTrailer)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.animeDetails.collectLatest { anime ->
                    if (anime == null) return@collectLatest

                    title.text = anime.title
                    rating.text = "Rating: ${anime.score ?: "N/A"}"
                    episodes.text = "Episodes: ${anime.episodes ?: "N/A"}"
                    genres.text = "Genres: ${anime.genres}"
                    synopsis.text =
                        anime.synopsis ?: "No synopsis available"

                    renderTrailer(
                        anime.trailerYoutubeId,
                        webView,
                        trailerLabel
                    )
                }
            }
        }
    }

    // ---------------- TRAILER ----------------

    private fun renderTrailer(
        trailerId: String?,
        webView: WebView,
        trailerLabel: TextView
    ) {
        if (
            NetworkUtils.isInternetAvailable(this)
            && !trailerId.isNullOrBlank()
        ) {
            trailerLabel.visibility = View.VISIBLE
            webView.visibility = View.VISIBLE

            val html = """
                <html>
                    <body style="margin:0;padding:0;">
                        <iframe
                            width="100%"
                            height="100%"
                            src="https://www.youtube-nocookie.com/embed/$trailerId"
                            frameborder="0"
                            allowfullscreen>
                        </iframe>
                    </body>
                </html>
            """.trimIndent()

            webView.loadData(html, "text/html", "utf-8")
        } else {
            webView.visibility = View.GONE
            trailerLabel.visibility = View.VISIBLE
            trailerLabel.text = "Trailer not available"
        }
    }

    // ---------------- FACTORY ----------------

    private fun provideViewModelFactory(): AnimeDetailViewModelFactory {
        val app = application as MyApplication

        return AnimeDetailViewModelFactory(
            repository = AnimeDetailsRepository(
                apiService = RetrofitClient.apiService,
                animeDetailsDao = app.database.animeDetailsDao(),
                cache = app.animeDetailsCache,
                logger = app.logger
            ),
            logger = app.logger
        )
    }

    override fun onDestroy() {
        findViewById<WebView>(R.id.webTrailer)?.destroy()
        super.onDestroy()
    }
}
