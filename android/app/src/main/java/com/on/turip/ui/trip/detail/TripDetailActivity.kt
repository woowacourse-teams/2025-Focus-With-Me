package com.on.turip.ui.trip.detail

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.on.turip.R
import com.on.turip.databinding.ActivityTripDetailBinding
import com.on.turip.ui.common.TuripUrlConverter
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.common.loadCircularImage
import com.on.turip.ui.common.model.trip.toDisplayText
import com.on.turip.ui.trip.detail.webview.TuripWebChromeClient
import com.on.turip.ui.trip.detail.webview.TuripWebViewClient
import com.on.turip.ui.trip.detail.webview.WebViewVideoBridge
import com.on.turip.ui.trip.detail.webview.applyVideoSettings

class TripDetailActivity : BaseActivity<ActivityTripDetailBinding>() {
    override val binding: ActivityTripDetailBinding by lazy {
        ActivityTripDetailBinding.inflate(layoutInflater)
    }
    val viewModel: TripDetailViewModel by viewModels {
        TripDetailViewModel.provideFactory(
            intent.getLongExtra(CONTENT_KEY, 0),
            intent.getLongExtra(CREATOR_KEY, 0),
        )
    }
    private val turipWebChromeClient: TuripWebChromeClient by lazy {
        TuripWebChromeClient(
            fullScreenView = binding.flTripDetailVideoFullscreen,
            onEnterFullScreen = ::enableFullscreen,
            onExitFullScreen = ::disableFullscreen,
        )
    }

    private val tripDayAdapter by lazy {
        TripDayAdapter { dayModel ->
            viewModel.updateDay(dayModel)
        }
    }

    private val tripPlaceAdapter by lazy {
        TripPlaceAdapter { placeModel ->
            val intent: Intent = Intent(Intent.ACTION_VIEW, placeModel.placeUri)
            startActivity(intent)
        }
    }

    private fun enableFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(this.window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding.tbTripDetail.visibility = View.GONE
        binding.nsvTripDetail.visibility = View.GONE

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun disableFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(this.window, true)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            show(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding.tbTripDetail.visibility = View.VISIBLE
        binding.nsvTripDetail.visibility = View.VISIBLE

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupOnBackPressedDispatcher()
        setupWebView()
        setupAdapters()
        setupListeners()
        setupObservers()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbTripDetail)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = null
        }
        binding.tbTripDetail.navigationIcon?.setTint(
            ContextCompat.getColor(
                this,
                R.color.gray_300_5b5b5b,
            ),
        )
    }

    private fun setupOnBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (turipWebChromeClient.isFullScreen()) {
                        turipWebChromeClient.onHideCustomView()
                    } else if (binding.wvTripDetailVideo.canGoBack()) {
                        binding.wvTripDetailVideo.goBack()
                    } else {
                        finish()
                    }
                }
            },
        )
    }

    private fun setupWebView() {
        binding.wvTripDetailVideo.apply {
            applyVideoSettings()
            webChromeClient = turipWebChromeClient
            webViewClient = TuripWebViewClient(binding.pbTripDetailVideo)
        }
    }

    private fun showWebViewErrorView() {
        runOnUiThread {
            binding.wvTripDetailVideo.visibility = View.GONE
            binding.tvTripDetailVideoError.visibility = View.VISIBLE
        }
    }

    private fun setupAdapters() {
        binding.rvTripDetailTravelDay.adapter = tripDayAdapter
        binding.rvTripDetailTravelPlace.apply {
            adapter = tripPlaceAdapter
            itemAnimator = null
        }
    }

    private fun setupListeners() {
        binding.tvTripDetailVideoError.setOnClickListener {
            val intent =
                Intent(
                    Intent.ACTION_VIEW,
                    viewModel.tripDetailState.value
                        ?.content
                        ?.videoData
                        ?.url
                        ?.toUri(),
                )
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        viewModel.tripDetailState.observe(this) { tripDetailState ->
            tripDayAdapter.submitList(tripDetailState.days)
            tripPlaceAdapter.submitList(tripDetailState.places)
            binding.ivTripDetailCreatorThumbnail.loadCircularImage(
                tripDetailState.content?.creator?.profileImage ?: "",
            )
            binding.tvTripDetailCreatorName.text = tripDetailState.content?.creator?.channelName
            binding.tvTripDetailContentTitle.text = tripDetailState.content?.videoData?.title
            binding.tvTripDetailUploadDate.text = tripDetailState.content?.videoData?.uploadedDate
            binding.tvTripDetailTotalPlaceCount.text =
                getString(R.string.all_total_place_count, tripDetailState.tripModel.tripPlaceCount)
            binding.tvTripDetailTravelDuration.text =
                tripDetailState.tripModel.tripDurationModel.toDisplayText(this)
            binding.tvTripDetailDayPlaceCount.text =
                getString(
                    R.string.trip_detail_day_place_count,
                    tripDetailState.places.size,
                )
        }
        viewModel.videoUri.observe(this) { url ->
            binding.wvTripDetailVideo.apply {
                addJavascriptInterface(
                    WebViewVideoBridge(
                        TuripUrlConverter.extractVideoId(url),
                    ) { showWebViewErrorView() },
                    BRIDGE_NAME_IN_JS_FILE,
                )
            }

            binding.wvTripDetailVideo.loadUrl(LOAD_URL_FILE_PATH)
        }
    }

    override fun onDestroy() {
        binding.wvTripDetailVideo.destroy()
        super.onDestroy()
    }

    companion object {
        private const val BRIDGE_NAME_IN_JS_FILE = "videoBridge"
        private const val LOAD_URL_FILE_PATH = "file:///android_asset/iframe.html"
        private const val CREATOR_KEY: String = "com.on.turip.CREATOR_KEY"
        private const val CONTENT_KEY: String = "com.on.turip.CONTENT_KEY"

        fun newIntent(
            context: Context,
            contentId: Long,
            creatorId: Long,
        ): Intent =
            Intent(context, TripDetailActivity::class.java)
                .putExtra(CONTENT_KEY, contentId)
                .putExtra(CREATOR_KEY, creatorId)
    }
}
