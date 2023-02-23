package com.khaled.apitester.screen.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.khaled.apitester.model.ApiCallModel
import com.khaled.apitester.util.BackgroundTaskUtils.doInBackground
import com.khaled.apitester.util.SharedPrefsUtils

// Using AndroidViewModel to access application context to access SharedPreferences
class MainViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _selectedSort = MutableLiveData(SortOption.DATE)
    internal val selectedSort: LiveData<SortOption> = _selectedSort

    fun getData() = MutableLiveData<List<ApiCallModel>>().apply {
        var newList: List<ApiCallModel> = emptyList()
        doInBackground(
            task = {
                newList = SharedPrefsUtils(app.applicationContext).getPreviousApiCalls()
            },
            onDone = {
                value = newList
            })
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