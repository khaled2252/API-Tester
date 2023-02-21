package com.khaled.apitester

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * @Author: Khaled Ahmed
 * @Date: 2/21/2023
 */


object HttpUtils {

    fun httpCall(method: HttpMethod, url: String, query: String) {
        // Set up request
        val connection: HttpsURLConnection = URL(url).openConnection() as HttpsURLConnection
        connection.requestMethod = method.name
        // To send a post body, output must be true
        connection.doOutput = method == HttpMethod.POST
        // Create the stream
        val outputStream: OutputStream = connection.outputStream
        // Create a writer container to pass the output over the stream
        val outputWriter = OutputStreamWriter(outputStream)
        // Add the string to the writer container
        outputWriter.write(query)
        // Send the data
        outputWriter.flush()

        // Create an input stream to read the response
        val inputStream = BufferedReader(InputStreamReader(connection.inputStream)).use {
            // Container for input stream data
            val response = StringBuffer()
            var inputLine = it.readLine()
            // Add each line to the response container
            while (inputLine != null) {
                response.append(inputLine)
                inputLine = it.readLine()
            }
            it.close()
            // TODO: Add main thread callback to parse response
            println(">>>> Response: $response")
        }
        connection.disconnect()
    }

    enum class HttpMethod {
        GET, POST
    }
}