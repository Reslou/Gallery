package com.example.gallery

import android.content.Intent
import android.content.IntentSender
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.gallery.databinding.FragmentGalleryBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.retry
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
        val galleryViewModel: GalleryViewModel by viewModels()
        val galleryAdapter = GalleryAdapter()
        with(binding) {
            with(recyclerView) {
                adapter = galleryAdapter
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }
            swipeRefreshLayout.setOnRefreshListener { galleryAdapter.refresh() }
        }
        lifecycleScope.launch {
            galleryViewModel.flow.collectLatest { galleryAdapter.submitData(it) }
            galleryAdapter.loadStateFlow.collectLatest {
                binding.swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
            }
        }
        // 添加操作菜单
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.swipeIndicator -> {
                        galleryAdapter.refresh()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}