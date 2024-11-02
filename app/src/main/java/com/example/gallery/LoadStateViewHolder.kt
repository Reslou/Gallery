package com.example.gallery

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.gallery.databinding.GalleryFooterBinding

class LoadStateViewHolder(itemView: View, var retry: () -> Unit) :
    RecyclerView.ViewHolder(itemView) {
    private val binding = GalleryFooterBinding.bind(itemView)
    private val progressBar = binding.progressBar
    private val textView = binding.textView

    companion object {
        fun newInstance(parent: ViewGroup, retry: () -> Unit): LoadStateViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_footer, parent, false)
            (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            return LoadStateViewHolder(view, retry)
        }
    }

    fun bind(loadState: LoadState) {
        when (loadState) {
            is LoadState.Loading -> {
                textView.text = "正在加载"
                progressBar.visibility = View.VISIBLE
                itemView.isClickable = false
            }

            is LoadState.Error -> {
                textView.text = "点击重试"
                progressBar.visibility = View.GONE
                itemView.isClickable = true
                itemView.setOnClickListener { retry() }
            }

            is LoadState.NotLoading -> {
                Log.d("loadState", "bind: ${loadState.endOfPaginationReached}")
                if (loadState.endOfPaginationReached) {
                    textView.text = "加载完毕"
                    progressBar.visibility = View.GONE
                    itemView.isClickable = false
                }
            }
        }
    }
}
