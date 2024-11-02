package com.example.gallery

import android.os.Bundle
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

class PhotoAdapter(diffCallback: DiffUtil.ItemCallback<PhotoItem>) :
    PagingDataAdapter<PhotoItem, PhotoViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val holder = PhotoViewHolder.newInstance(parent)
        holder.itemView.setOnClickListener {
            Bundle().apply {
                putParcelableArrayList("PHOTO_LIST", ArrayList(snapshot()))
                putInt("PHOTO_POSITION", holder.bindingAdapterPosition)
                holder.itemView.findNavController().navigate(R.id.galleryToPhoto, this)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoItem = getItem(position) ?: return
        holder.bind(photoItem)
    }


    object PhotoComparator : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }
    }
}