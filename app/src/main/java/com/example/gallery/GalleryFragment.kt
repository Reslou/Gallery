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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.gallery.databinding.FragmentGalleryBinding

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
        val galleryAdapter = GalleryAdapter(galleryViewModel)
        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefreshLayout
        with(galleryViewModel) {
            photoListLive.observe(viewLifecycleOwner) {
                if (galleryViewModel.needToScrollToTop) {
                    binding.recyclerView.scrollToPosition(0)
                    galleryViewModel.needToScrollToTop = false
                }
                galleryAdapter.submitList(it)
                swipeRefreshLayout.isRefreshing = false
            }
            dataStatusLive.observe(viewLifecycleOwner) {
                galleryAdapter.footerViewStatus = it
                galleryAdapter.notifyItemChanged(galleryAdapter.itemCount - 1)
                if (it == DATA_STATUS_NETWORK_ERROR) swipeRefreshLayout.isRefreshing = false
            }
        }
        with(binding) {
            with(recyclerView) {
                adapter = galleryAdapter
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (dy < 0) return
                        val layoutManager = layoutManager as StaggeredGridLayoutManager
                        val intArray = IntArray(2)
                        layoutManager.findLastVisibleItemPositions(intArray)
                        if (intArray[0] == galleryAdapter.itemCount - 1) {
                            galleryViewModel.fetchData()
                        }
                    }
                })
            }
        }
        swipeRefreshLayout.setOnRefreshListener {
            galleryViewModel.resetQuery()
        }
        // 添加操作菜单
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.swipeIndicator -> {
                        swipeRefreshLayout.isRefreshing = true
                        galleryViewModel.resetQuery()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}