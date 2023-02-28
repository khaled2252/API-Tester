package com.khaled.apitester.screen.add

import android.app.Application
import android.webkit.URLUtil.isValidUrl
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.khaled.apitester.model.ApiCallModel
import com.khaled.apitester.util.BackgroundTaskUtils.doInBackground
import com.khaled.apitester.util.HttpUtils
import com.khaled.apitester.util.SharedPrefsUtils
import com.khaled.apitester.util.extension.isNetworkAvailable
import com.khaled.apitester.util.extension.toMap
import java.io.File

// Using AndroidViewModel to access application context to check for internet connection and access SharedPreferences
class AddNewApiCallViewModel(private val app: Application) : AndroidViewModel(app) {

    var currentFile: File? = null

    private val _requestLiveData = MutableLiveData<ViewState>()
    internal val requestLiveData: LiveData<ViewState> = _requestLiveData

    fun makeARequest(
        method: HttpUtils.HttpMethod,
        url: String,
        headers: String,
        body: String?,
        isJson: Boolean
    ) {
            if (app.applicationContext.isNetworkAvailable()) {
                if (isValidUrl(url)) {
                    _requestLiveData.value = ViewState.Loading
                    var result: ApiCallModel? = null
                    doInBackground(
                        task = {
                            HttpUtils.httpCall(
                                method,
                                url,
                                headers.toMap(),
                                if (method == HttpUtils.HttpMethod.GET) null else if (isJson) body else null,
                                if (method == HttpUtils.HttpMethod.GET) null else if (isJson) null else currentFile
                            ) { apiCallModel ->
                                result = apiCallModel
                                SharedPrefsUtils(app.applicationContext).addApiCallModel(
                                    apiCallModel
                                )
                            }
                        },
                        onDone = {
                            _requestLiveData.value = result?.let { ViewState.Finished(it) }
                        })
                } else
                    _requestLiveData.value = ViewState.InvalidUrl
            } else
                _requestLiveData.value = ViewState.NoInternet
        }

    sealed class ViewState {
        object Loading : ViewState()
        data class Finished(val apiCallModel: ApiCallModel) : ViewState()
        object NoInternet : ViewState()
        object InvalidUrl : ViewState()
    }
}