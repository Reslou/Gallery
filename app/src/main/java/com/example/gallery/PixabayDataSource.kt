package com.example.gallery

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PixabayDataSource(private val context: Context) : PagingSource<Int, PhotoItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoItem> {
        val api = "https://pixabay.com/api/"
        val q = arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal")
        val key = "46728940-47fb27bf6a71a7da83b2d6023"
        try {
            val page = params.key ?: 1
            val url = "$api?key=$key&q=${q.random()}&per_page=50&page=$page"
            val response = suspendCoroutine { continuation ->
                val request = StringRequest(Request.Method.GET, url,
                    { response -> continuation.resume(response) },
                    { error -> continuation.resumeWithException(error) })
                VolleySingleton.getInstance(context).requestQueue.add(request)
            }
            val data = Gson().fromJson(response, Pixabay::class.java).hits.toList()
            return LoadResult.Page(data = data, prevKey = null, nextKey = page + 1)
        } catch (e: Exception) {
            return if (e.toString() == "com.android.volley.ClientError") {
                LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
            } else {
                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoItem>): Int? {
        return null
    }
}
