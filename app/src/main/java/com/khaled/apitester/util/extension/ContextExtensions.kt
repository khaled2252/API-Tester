package com.khaled.apitester.util.extension

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import com.khaled.apitester.BuildConfig
import java.io.File
import java.io.FileOutputStream


/**
 * @Author: Khaled Ahmed
 * @Date: 2/22/2023
 */
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
    val newFile = File(cacheDir, getFileNameFromUri(uri))
    val inputStream = contentResolver.openInputStream(uri)
    val outputStream = FileOutputStream(newFile)
    inputStream?.copyTo(outputStream)
    inputStream?.close()
    return newFile
}

fun Context.openFile(file: File) {
    val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(uri, MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.path)))
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "No application found to open this file", Toast.LENGTH_SHORT).show()
    }
}