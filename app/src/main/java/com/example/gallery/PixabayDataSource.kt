package com.example.gallery

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PixabayDataSource(private val context: Context) : PagingSource<Int, PhotoItem>() {
    override fun getRefreshKey(state: PagingState<Int, PhotoItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoItem> {
        val api = "https://pixabay.com/api/"
        val q = arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal")
        val key = "46728940-47fb27bf6a71a7da83b2d6023"
        val page = params.key ?: 1
        val url = "$api?key=$key&q=${q.random()}&per_page=50&page=$page"
        try {
            val dataList = suspendCoroutine {
                StringRequest(Request.Method.GET, url,
                    { response ->
                        val pixabayList =
                            Gson().fromJson(response, Pixabay::class.java).hits.toList()
                        it.resume(pixabayList)
                    },
                    { error -> it.resumeWithException(error) })
                    .also { VolleySingleton.getInstance(context).requestQueue.add(it) }
            }
            return LoadResult.Page(data = dataList, prevKey = null, nextKey = page + 1)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}
