package com.khaled.apitester.screen.add

import android.app.Application
import android.webkit.URLUtil.isValidUrl
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.khaled.apitester.model.ApiCallModel
import com.khaled.apitester.util.BackgroundTaskUtils.doInBackground
import com.khaled.apitester.util.HttpUtils
import com.khaled.apitester.util.SharedPrefsUtils
import com.khaled.apitester.util.extension.isNetworkAvailable
import java.io.File

// Using AndroidViewModel to access application context to check for internet connection and access SharedPreferences
class AddNewApiCallViewModel(private val app: Application) : AndroidViewModel(app) {

    var currentFile: File? = null

    fun makeGetRequest(url: String, headers: String) =
        MutableLiveData<ViewState>().apply {
            if (app.applicationContext.isNetworkAvailable()) {
                if (isValidUrl(url)) {
                    value = ViewState.Loading
                    var result: ApiCallModel? = null
                    doInBackground(
                        task = {
                            HttpUtils.httpCall(
                                HttpUtils.HttpMethod.GET,
                                url,
                                if (headers.isNotEmpty()) headers.split("\n").associate {
                                    if (it.contains(" ")) {
                                        val (key, value) = it.split(" ", limit = 2)
                                        key to value
                                    } else it to ""
                                } else null,
                                null,
                                null) { apiCallModel ->
                                result = apiCallModel
                                SharedPrefsUtils(app.applicationContext).addApiCallModel(
                                    apiCallModel
                                )
                            }
                        },
                        onDone = {
                            value = result?.let { ViewState.Finished(it) }
                        })
                } else
                    value = ViewState.InvalidUrl
            }
            else
                value = ViewState.NoInternet
        }

    fun makePostRequest(url: String, headers: String, body: String?, isJson: Boolean) =
        MutableLiveData<ViewState>().apply {
            if (app.applicationContext.isNetworkAvailable()) {
                if (isValidUrl(url)) {
                    value = ViewState.Loading
                    var result: ApiCallModel? = null
                    doInBackground(
                        task = {
                            HttpUtils.httpCall(
                                HttpUtils.HttpMethod.POST,
                                url,
                                if (headers.isNotEmpty()) headers.split("\n").associate {
                                    if (it.contains(" ")) {
                                        val (key, value) = it.split(" ", limit = 2)
                                        key to value
                                    } else it to ""
                                } else null,
                                if (isJson) body else null,
                                if (isJson) null else currentFile
                            )
                            { apiCallModel ->
                                result = apiCallModel
                                SharedPrefsUtils(app.applicationContext).addApiCallModel(
                                    apiCallModel
                                )
                            }
                        },
                        onDone = {
                            value = result?.let { ViewState.Finished(it) }
                        })
                } else
                    value = ViewState.InvalidUrl
            }
            else
                value = ViewState.NoInternet
        }

    sealed class ViewState {
        object Loading : ViewState()
        data class Finished(val apiCallModel: ApiCallModel) : ViewState()
        object NoInternet : ViewState()
        object InvalidUrl : ViewState()
    }
}