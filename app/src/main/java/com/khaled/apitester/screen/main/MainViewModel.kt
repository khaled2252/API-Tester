package com.khaled.apitester.screen.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.khaled.apitester.model.ApiCallModel
import com.khaled.apitester.util.BackgroundTaskUtils.doInBackground
import com.khaled.apitester.util.HttpUtils
import com.khaled.apitester.util.SharedPrefsUtils

// Using AndroidViewModel to access application context to access SharedPreferences
class MainViewModel(private val app: Application) : AndroidViewModel(app) {

    internal var selectedSort: SortOption = SortOption.DATE

    private val _dataListLiveData = MutableLiveData<List<ApiCallModel>>()
    internal val dataListLiveData: LiveData<List<ApiCallModel>> = _dataListLiveData

    fun getPreviousApiCalls() {
        if (_dataListLiveData.value == null) {
            var newList: List<ApiCallModel> = emptyList()
            doInBackground(
                task = {
                    newList = SharedPrefsUtils(app.applicationContext).getPreviousApiCalls()
                },
                onDone = {
                    _dataListLiveData.value = newList
                })
        }
    }

    fun applyNewSort(newSortOption: SortOption) {
        selectedSort = newSortOption
        _dataListLiveData.value = when(newSortOption){
            SortOption.DATE ->  _dataListLiveData.value?.sortedByDescending { it.dateInMillis }
            SortOption.EXECUTION_TIME_ASCENDING -> _dataListLiveData.value?.sortedBy { it.executionTime }
            SortOption.EXECUTION_TIME_DESCENDING -> _dataListLiveData.value?.sortedByDescending { it.executionTime }
            SortOption.GET_REQUESTS -> _dataListLiveData.value?.sortedByDescending { it.requestMethod == HttpUtils.HttpMethod.GET }
            SortOption.POST_REQUESTS -> _dataListLiveData.value?.sortedByDescending { it.requestMethod == HttpUtils.HttpMethod.POST }
            SortOption.SUCCESS_REQUESTS -> _dataListLiveData.value?.sortedByDescending { (it.responseCode?.div(100) ?: 200) == 2 }
            SortOption.FAILURE_REQUESTS -> _dataListLiveData.value?.sortedByDescending { (it.responseCode?.div(100) ?: 200) != 2 }
        }
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