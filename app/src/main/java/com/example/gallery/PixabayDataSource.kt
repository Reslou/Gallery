package com.example.gallery

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PixabayDataSource(private val context: Context) : PagingSource<Int, PhotoItem>() {
    private val keyWords =
        arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal").random()

    override fun getRefreshKey(state: PagingState<Int, PhotoItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoItem> {
        val nextPageNumber = params.key ?: 1
        val url =
            "https://pixabay.com/api/?key=46728940-47fb27bf6a71a7da83b2d6023&q=$keyWords&per_page=50&page=$nextPageNumber"
        val dataList = suspendCoroutine {
            StringRequest(Request.Method.GET, url, { response ->
                val pixabayList = Gson().fromJson(response, Pixabay::class.java).hits.toList()
                it.resume(pixabayList)
            }, { error ->
                Log.d("hello", error.toString())
            }).also { VolleySingleton.getInstance(context).requestQueue.add(it) }
        }
        return LoadResult.Page(data = dataList, prevKey = null, nextKey = nextPageNumber + 1)
    }
}
