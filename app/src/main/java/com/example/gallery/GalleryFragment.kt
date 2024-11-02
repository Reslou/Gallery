package com.example.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.gallery.databinding.FragmentGalleryBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: GalleryViewModel by viewModels()
        val adapter = PhotoAdapter(PhotoAdapter.PhotoComparator)
        val footerAdapter = adapter.withLoadStateFooter(LoadStateAdapter(adapter::retry))
        with(binding) {
            with(recyclerView) {
                this.adapter = footerAdapter
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }
            with(swipeRefreshLayout) {
                setOnRefreshListener { adapter.refresh() }
                viewLifecycleOwner.lifecycleScope.launch { viewModel.flow.collectLatest { adapter.submitData(it) } }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                binding.swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
            }
        }
        // 添加操作菜单
        requireActivity().addMenuProvider(menuProvider(adapter), viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun menuProvider(adapter: PhotoAdapter) = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.itemRefresh -> {
                    adapter.refresh()
                    true
                }
                R.id.itemRetry -> {
                    adapter.retry()
                    true
                }

                else -> false
            }
        }
    }
}