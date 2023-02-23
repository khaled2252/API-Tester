package com.khaled.apitester.util.extension

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream


/**
 * @Author: Khaled Ahmed
 * @Date: 2/22/2023
 */
object ContextExtensions {

    fun Context.isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null // Capabilities (TRANSPORT_CELLULAR, TRANSPORT_WIFI, TRANSPORT_ETHERNET) will be null if there is no network
    }

    fun Context.getFileNameFromUri(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.moveToFirst()
        val name = cursor?.getString(0)
        cursor?.close()
        return name ?: System.currentTimeMillis().toString()
    }

    fun Context.makeFileFromUri(uri: Uri): File {
        val newFile = File.createTempFile(System.currentTimeMillis().toString(), null)
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(newFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        return newFile
    }

}