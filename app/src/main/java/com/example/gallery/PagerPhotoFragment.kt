package com.example.gallery

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.gallery.databinding.FragmentPagerPhotoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 图片详情分页器的实现类
 */
class PagerPhotoFragment : Fragment() {
    // 绑定视图对象
    private var _binding: FragmentPagerPhotoBinding? = null
    private val binding get() = _binding!!

    // 权限请求的启动器
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // 通过LayoutInflater将XML布局文件转换为视图对象
        _binding = FragmentPagerPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 在视图销毁时释放绑定对象
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 获取传递过来的照片列表
        @Suppress("DEPRECATION") val photoList =
            arguments?.getParcelableArrayList<PhotoItem>("PHOTO_LIST")
        // 初始化适配器并设置到ViewPager2
        PagerPhotoListAdapter().apply {
            binding.viewPager2.adapter = this
            submitList(photoList)
        }
        // 注册权限请求的启动器
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // 权限已授予，执行相关操作
                    lifecycleScope.launch {
                        savePhoto()
                    }
                } else {
                    // 权限被拒绝，处理拒绝的情况
                    toast("没有存储权限")
                }
            }
        // 设置ViewPager2的页面变化监听器和初始位置
        with(binding) {
            with(viewPager2) {
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        binding.photoTag.text =
                            getString(R.string.photo_tag_text, position + 1, photoList?.size)
                    }
                })
                setCurrentItem(arguments?.getInt("PHOTO_POSITION") ?: 0, false)
            }
            // 设置保存按钮的点击监听器
            buttonSave.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // 请求权限
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else {
                    // 已有权限，执行保存操作
                    lifecycleScope.launch {
                        savePhoto()
                    }
                }
            }
        }
    }

    /**
     * 保存当前显示的图片到外部存储
     */
    private suspend fun savePhoto() {
        var result = false
        withContext(Dispatchers.IO) {
            // 获取当前显示的图片并转换为Bitmap
            val holder =
                (binding.viewPager2[0] as RecyclerView).findViewHolderForAdapterPosition(binding.viewPager2.currentItem) as PagerPhotoViewHolder
            val bitmap = holder.pagerPhoto.drawable.toBitmap()
            // 在外部存储中创建新的图片记录
            val saveUri = requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues()
            ) ?: run {
                return@withContext
            }
            // 将Bitmap数据写入到外部存储
            requireContext().contentResolver.openOutputStream(saveUri)?.use {
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)) {
                    result = true
                }
            }
        }
        if (result) {
            toast("存储成功")
        } else {
            toast("存储失败")
        }
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}
