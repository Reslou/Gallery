package com.example.gallery


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.supercharge.shimmerlayout.ShimmerLayout


class GalleryAdapter(private val galleryViewModel: GalleryViewModel) :
    ListAdapter<PhotoItem, MyViewHolder>(DIFFCALLBACK) {
    var footerViewStatus = DATA_STATUS_CAN_LOAD_MORE

    companion object {
        const val NORMAL_VIEW_TYPE = 0
        const val FOOTER_VIEW_TYPE = 1
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) FOOTER_VIEW_TYPE else NORMAL_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder: MyViewHolder
        if (viewType == NORMAL_VIEW_TYPE) {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell, parent, false)
            holder = MyViewHolder(itemView)
            with(holder) {
                itemView.setOnClickListener {
                    Bundle().apply {
                        putParcelableArrayList("PHOTO_LIST", ArrayList(currentList))
                        putInt("PHOTO_POSITION", adapterPosition)
                        itemView.findNavController().navigate(R.id.galleryToPhoto, this)
                    }
                }
            }
        } else {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_footer, parent, false)
            holder = MyViewHolder(itemView)
            with(holder.itemView) {
                (layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
                setOnClickListener {
                    galleryViewModel.fetchData()
                    it.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                    it.findViewById<TextView>(R.id.textView).text = "正在加载"
                }
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemView = holder.itemView
        if (position == itemCount - 1) {
            with(itemView) {
                val progressBar: ProgressBar = findViewById(R.id.progressBar)
                val textView: TextView = findViewById(R.id.textView)
                when (footerViewStatus) {
                    DATA_STATUS_CAN_LOAD_MORE -> {
                        progressBar.visibility = View.VISIBLE
                        textView.text = "正在加载"
                        isClickable = false
                    }

                    DATA_STATUS_NO_MORE -> {
                        progressBar.visibility = View.GONE
                        textView.text = "全部加载完毕"
                        isClickable = false
                    }

                    DATA_STATUS_NETWORK_ERROR -> {
                        progressBar.visibility = View.GONE
                        textView.text = "网络故障，点击重试"
                        isClickable = true
                    }
                }
            }
            return
        }
        // 绑定视图
        val cell: ShimmerLayout
        val previewPhoto: ImageView
        val user: TextView
        val likes: TextView
        val collections: TextView
        with(itemView) {
            cell = findViewById(R.id.shimmerLayoutCell)
            previewPhoto = findViewById(R.id.imageViewPreviewPhoto)
            user = findViewById(R.id.textViewUser)
            likes = findViewById(R.id.textViewLikes)
            collections = findViewById(R.id.textViewCollections)
        }
        // 初始化视图
        cell.apply {
            setShimmerColor(0x55FFFFFF)
            setShimmerAngle(0)
            startShimmerAnimation()
        }
        val photoItem = getItem(position)
        previewPhoto.layoutParams.height = photoItem.photoHeight
        user.text = photoItem.photoUser
        likes.text = photoItem.photoLikes.toString()
        collections.text = photoItem.photoCollections.toString()
        Glide.with(itemView).load(photoItem.previewUrl).placeholder(R.drawable.photo_placeholder)
            .listener(object : RequestListener<Drawable> {
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
            }).into(previewPhoto)
    }

    object DIFFCALLBACK : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }
    }
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)