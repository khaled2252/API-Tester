package com.khaled.apitester.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

/**
 * @Author: Khaled Ahmed
 * @Date: 2/23/2023
 */
object BackgroundTaskUtils {

    fun doInBackground(task: () -> Unit, onDone: () -> Unit) {
        // Blocking call in a separate thread
        Executors.newSingleThreadExecutor().execute {
            task()
            Handler(Looper.getMainLooper()).post {
                // Update UI in main thread when done
                onDone()
            }
        }
    }
}