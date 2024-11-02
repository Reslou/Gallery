package com.example.gallery

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.gallery.databinding.GalleryCellBinding

class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = GalleryCellBinding.bind(itemView)
    private val previewPhoto = binding.imageViewPreviewPhoto
    private val cell = binding.shimmerLayoutCell
    private val user = binding.textViewUser
    private val likes = binding.textViewLikes
    private val collections = binding.textViewCollections

    companion object {
        fun newInstance(parent: ViewGroup): PhotoViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell, parent, false)
            return PhotoViewHolder(view)
        }
    }

    fun bind(photoItem: PhotoItem) {

        cell.apply {
            setShimmerColor(0x55FFFFFF)
            setShimmerAngle(0)
            startShimmerAnimation()
        }
        previewPhoto.layoutParams.height = photoItem.photoHeight
        user.text = photoItem.photoUser
        likes.text = photoItem.photoLikes.toString()
        collections.text = photoItem.photoCollections.toString()
        Glide.with(itemView).load(photoItem.previewUrl)
            .placeholder(R.drawable.photo_placeholder)
            .listener(listener()).into(previewPhoto)

    }

    private fun listener() = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: Drawable,
            model: Any,
            target: Target<Drawable>?,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            return false.also { cell.stopShimmerAnimation() }
        }
    }
}