package com.khaled.apitester.util

import android.content.Context
import android.net.ConnectivityManager

/**
 * @Author: Khaled Ahmed
 * @Date: 2/22/2023
 */
object ContextUtils {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null // Capabilities (TRANSPORT_CELLULAR, TRANSPORT_WIFI, TRANSPORT_ETHERNET) will be null if there is no network
    }

}