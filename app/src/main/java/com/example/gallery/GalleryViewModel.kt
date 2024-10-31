package com.example.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

const val DATA_STATUS_CAN_LOAD_MORE = 0
const val DATA_STATUS_NO_MORE = 1
const val DATA_STATUS_NETWORK_ERROR = 2

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()
    val photoListLive: LiveData<List<PhotoItem>>
        get() = _photoListLive
    private val _dataStatusLive = MutableLiveData<Int>()
    val dataStatusLive: LiveData<Int>
        get() = _dataStatusLive
    var needToScrollToTop = true
    private val keyWords =
        arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal")
    private lateinit var currentKey: String
    private val perPage = 100
    private var currentPage = 1
    private var totalPage = 1
    private var isNewQuery = true
    private var isLoading = false

    init {
        resetQuery()
    }

    fun resetQuery() {
        currentPage = 1
        totalPage = 1
        currentKey = keyWords.random()
        isNewQuery = true
        needToScrollToTop = true
        fetchData()
    }

    fun fetchData() {
        if (isLoading) return
        if (currentPage > totalPage) {
            _dataStatusLive.value = DATA_STATUS_NO_MORE
            return
        }
        isLoading = true
        val stringRequest = StringRequest(Request.Method.GET, getUrl(), {
            with(Gson().fromJson(it, Pixabay::class.java)) {
                if (isNewQuery) {
                    _photoListLive.value = hits.toList()
                } else {
                    _photoListLive.value =
                        arrayListOf(_photoListLive.value!!, hits.toList()).flatten()
                }
                totalPage = (totalHits - 1) / perPage + 1
            }
            isLoading = false
            isNewQuery = false
            currentPage++
            _dataStatusLive.value = DATA_STATUS_CAN_LOAD_MORE
        }, {
            _dataStatusLive.value = DATA_STATUS_NETWORK_ERROR
            isLoading = false
        })
        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest)
    }

    private fun getUrl(): String =
        "https://pixabay.com/api/?key=46728940-47fb27bf6a71a7da83b2d6023&q=${keyWords.random()}&per_page=${perPage}&page=${currentPage}"
}