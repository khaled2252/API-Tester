package com.khaled.apitester.screen.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.khaled.apitester.model.ApiCallModel
import com.khaled.apitester.util.BackgroundTaskUtils.doInBackground
import com.khaled.apitester.util.SharedPrefsUtils

// Using AndroidViewModel to access application context to check for internet connection and access SharedPreferences
class ApiCallDetailsViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _apiCallModelLiveData = MutableLiveData<ApiCallModel>()
    internal val apiCallModelLiveData: LiveData<ApiCallModel> = _apiCallModelLiveData

    fun fetchApiCallModel(apiCallModelDateIdentifier: Long) {
        var apiCallModel: ApiCallModel? = null

        doInBackground(
            task = {
                apiCallModel = SharedPrefsUtils(app.applicationContext).getApiCallModel(apiCallModelDateIdentifier)
            },
            onDone = {
                _apiCallModelLiveData.value = apiCallModel
            }
        )
    }
}