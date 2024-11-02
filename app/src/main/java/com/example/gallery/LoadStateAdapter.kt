package com.example.gallery

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class LoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadStateViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder =
        LoadStateViewHolder.newInstance(parent, retry)

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) =
        holder.bind(loadState)

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean =
        loadState is LoadState.Loading || loadState is LoadState.Error
                || (loadState is LoadState.NotLoading && loadState.endOfPaginationReached)
}