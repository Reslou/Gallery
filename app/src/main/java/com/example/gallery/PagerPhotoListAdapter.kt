package com.example.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gallery.databinding.PagerPhotoViewBinding

class PagerPhotoListAdapter : ListAdapter<PhotoItem, PagerPhotoViewHolder>(DiffCallback) {
    object DiffCallback : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerPhotoViewHolder {
        val binding =
            PagerPhotoViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagerPhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PagerPhotoViewHolder, position: Int) {
        Glide.with(holder.itemView).load(getItem(position).fullUrl)
            .placeholder(R.drawable.photo_placeholder).into(holder.binding.pagerPhoto)
    }
}

class PagerPhotoViewHolder(val binding: PagerPhotoViewBinding) : RecyclerView.ViewHolder(binding.root)