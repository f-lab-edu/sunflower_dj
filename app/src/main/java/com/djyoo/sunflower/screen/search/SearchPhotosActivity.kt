package com.djyoo.sunflower.screen.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.djyoo.sunflower.R
import com.djyoo.sunflower.common.base.BaseActivity
import com.djyoo.sunflower.common.widget.GridSpacingItemDecoration
import com.djyoo.sunflower.databinding.ActivitySearchPhotosBinding
import com.djyoo.sunflower.screen.search.vm.SearchPhotosUiState
import com.djyoo.sunflower.screen.search.vm.SearchPhotosViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SearchPhotosActivity : BaseActivity<ActivitySearchPhotosBinding>(R.layout.activity_search_photos) {

    private val viewModel: SearchPhotosViewModel by viewModels()

    private val adapter = SearchPhotosAdapter { photo ->
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(photo.user.attributionUrl)))
    }

    private var savedRecyclerState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.searchPhotosToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        binding.searchPhotosToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()
        setupSwipeRefresh()
        observeSearchResult()
        observeLoadingMore()

        val query = intent.getStringExtra(EXTRA_QUERY).orEmpty()
        if (query.isNotBlank() && viewModel.searchResult.value is SearchPhotosUiState.Idle) {
            viewModel.search(query)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.searchPhotosRecyclerView.layoutManager?.onSaveInstanceState()?.let { state ->
            outState.putParcelable(KEY_RECYCLER_STATE, state)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedRecyclerState = savedInstanceState.getParcelable(KEY_RECYCLER_STATE)
    }

    private fun setupRecyclerView() {
        binding.searchPhotosRecyclerView.adapter = adapter
        binding.searchPhotosRecyclerView.layoutManager = GridLayoutManager(this, GRID_SPAN_COUNT)
        binding.searchPhotosRecyclerView.setHasFixedSize(true)
        binding.searchPhotosRecyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = GRID_SPAN_COUNT,
                spacingDp = GRID_SPACING_DP,
                includeBottom = false,
            ),
        )

        binding.searchPhotosRecyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as? GridLayoutManager ?: return
                val totalItemCount = layoutManager.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                if (totalItemCount > 0 && lastVisible >= totalItemCount - PAGINATION_TRIGGER_OFFSET) {
                    viewModel.loadMore()
                }
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.searchPhotosSwipeRefresh.setColorSchemeResources(R.color.search_photos_swipe_progress_scheme)
        binding.searchPhotosSwipeRefresh.setProgressBackgroundColorSchemeResource(R.color.search_photos_swipe_progress_bg)
        binding.searchPhotosSwipeRefresh.setOnRefreshListener {
            val currentQuery = viewModel.query.value
            if (currentQuery.isNotBlank()) {
                viewModel.search(currentQuery)
            } else {
                binding.searchPhotosSwipeRefresh.isRefreshing = false
            }
        }
    }

    private fun observeSearchResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchResult.collect { state ->
                    when (state) {
                        is SearchPhotosUiState.Idle -> {
                            binding.searchPhotosProgress.isGone = true
                            binding.searchPhotosRecyclerView.isVisible = true
                        }

                        is SearchPhotosUiState.Loading -> {
                            binding.searchPhotosProgress.isVisible = true
                            binding.searchPhotosRecyclerView.isVisible = adapter.itemCount > 0
                        }

                        is SearchPhotosUiState.Success -> {
                            binding.searchPhotosProgress.isGone = true
                            binding.searchPhotosRecyclerView.isVisible = true
                            binding.searchPhotosSwipeRefresh.isRefreshing = false
                            adapter.submitList(state.response.results) {
                                savedRecyclerState?.let { parcelable ->
                                    binding.searchPhotosRecyclerView.layoutManager?.onRestoreInstanceState(parcelable)
                                    savedRecyclerState = null
                                }
                            }
                        }

                        is SearchPhotosUiState.Error -> {
                            binding.searchPhotosProgress.isGone = true
                            binding.searchPhotosRecyclerView.isVisible = adapter.itemCount > 0
                            binding.searchPhotosSwipeRefresh.isRefreshing = false
                            Snackbar.make(
                                binding.root,
                                state.message ?: getString(R.string.error_search_photos),
                                Snackbar.LENGTH_SHORT,
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun observeLoadingMore() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoadingMore.collect { isLoadingMore ->
                    binding.searchPhotosProgressMore.isGone = !isLoadingMore
                }
            }
        }
    }

    companion object {
        const val EXTRA_QUERY = "extra_search_query"

        private const val KEY_RECYCLER_STATE = "search_photos_recycler_state"
        private const val GRID_SPAN_COUNT = 2
        private const val PAGINATION_TRIGGER_OFFSET = 4
        private const val GRID_SPACING_DP = 20
    }
}
