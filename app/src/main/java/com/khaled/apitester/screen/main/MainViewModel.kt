package com.khaled.apitester.screen.main

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.khaled.apitester.model.ApiCallModel
import com.khaled.apitester.util.ContextUtils.isNetworkAvailable
import com.khaled.apitester.util.HttpUtils
import com.khaled.apitester.util.SharedPrefsUtils
import org.json.JSONObject
import java.io.File
import java.util.concurrent.Executors

// Using AndroidViewModel to access application context to check for internet connection
class MainViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _selectedSort = MutableLiveData(SortOption.DATE)
    internal val selectedSort: LiveData<SortOption> = _selectedSort

    fun getData() = MutableLiveData<List<ApiCallModel>>().apply {
        Executors.newSingleThreadExecutor().execute {
            val newList = SharedPrefsUtils(app.applicationContext).getPreviousApiCalls()
            Handler(Looper.getMainLooper()).post {
                value = newList
            }
        }
    }

    fun addData() = MutableLiveData<List<ApiCallModel>>().apply {
        val httpMethod = HttpUtils.HttpMethod.POST
        val url = "https://httpbin.org/get?name=khaled&age=25"
        val headers = mapOf<String?, String>(
            "Key1" to "value1",
            "Key2" to "value2"
        )
        val body = JSONObject().apply {
            put("name", "khaled")
            put("age", 25)
        }
        val file = File.createTempFile("temp", ".txt")

        if (isNetworkAvailable(app.applicationContext))
            Executors.newSingleThreadExecutor().execute {
                // Blocking call in a separate thread
                HttpUtils.httpCall(httpMethod, url, headers, null, null) { apiCallModel ->
                    SharedPrefsUtils(app.applicationContext).saveApiCallModel(apiCallModel)
                    val newList = SharedPrefsUtils(app.applicationContext).getPreviousApiCalls()
                    Handler(Looper.getMainLooper()).post {
                        // Update UI in main thread
                        value = newList
                    }
                }

            }
        else
            value = null
    }

    fun setNewSort(newSortOption: SortOption) {
        _selectedSort.value = newSortOption
    }

    enum class SortOption {
        DATE,
        EXECUTION_TIME_ASCENDING,
        EXECUTION_TIME_DESCENDING,
        GET_REQUESTS,
        POST_REQUESTS,
        SUCCESS_REQUESTS,
        FAILURE_REQUESTS
    }
}