package com.example.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    val flow = Pager(PagingConfig(20)) { PixabayDataSource(application) }.flow.cachedIn(viewModelScope)
}