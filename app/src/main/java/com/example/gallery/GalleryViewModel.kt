package com.example.gallery

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()
    val photoListLive: LiveData<List<PhotoItem>>
        get() = _photoListLive

    fun fetchData() {
        val stringRequest = StringRequest(Request.Method.GET,
            getUrl(),
            { _photoListLive.value = Gson().fromJson(it, Pixabay::class.java).hits.toList() },
            { Log.d("fetchData", it.toString()) })
        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest)
    }

    private fun getUrl(): String =
        "https://pixabay.com/api/?key=46728940-47fb27bf6a71a7da83b2d6023&q=${keyWords.random()}&per_page=100"

    private val keyWords =
        arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal")
}