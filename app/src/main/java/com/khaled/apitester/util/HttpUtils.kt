package com.khaled.apitester.util

import android.util.Log
import com.khaled.apitester.model.ApiCallModel
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection


object HttpUtils {

    fun httpCall(
        method: HttpMethod,
        url: String,
        headers: Map<String?, String>? = null,
        body: String? = null,
        file: File? = null,
        onResponse: (ApiCallModel) -> Unit
    ) {
        val systemTimeBeforeExecution = System.currentTimeMillis()

        try {
            val connection = URL(url).openConnection() as HttpsURLConnection
            connection.requestMethod = method.name
            for (header in headers.orEmpty()) {
                connection.setRequestProperty(header.key, header.value)
            }

            if (method == HttpMethod.POST) {
                connection.doOutput = true

                if (body != null) {
                    connection.setRequestProperty("Content-Type", "application/json; utf-8")
                    // Create the stream
                    val outputStream: OutputStream = connection.outputStream
                    // Create a writer container to pass the output over the stream
                    val outputWriter = OutputStreamWriter(outputStream)
                    // Add the string to the writer container
                    outputWriter.write(body.toString())
                    // Send the data
                    outputWriter.flush()
                }

                if (file != null) {
                    connection.setRequestProperty(
                        "Content-Type",
                        "multipart/form-data;boundary=$boundary"
                    )
                    val request = DataOutputStream(connection.outputStream)
                    request.writeBytes(twoHyphens + boundary + crlf);
                    request.writeBytes(
                        "Content-Disposition: form-data; name=\"" +
                                file.name + "\";filename=\"" +
                                file.name + "\"" + crlf
                    );
                    request.writeBytes(crlf);
                    request.write(file.readBytes())
                    request.writeBytes(crlf);
                    request.writeBytes(
                        twoHyphens + boundary +
                                twoHyphens + crlf
                    );
                    request.flush()
                    request.close()
                }
            }

            try {
                if (connection.responseCode / 100 == 2) // 2xx is success
                    BufferedReader(InputStreamReader(connection.inputStream))
                        .use {
                            // Container for input stream data
                            val response = StringBuffer()
                            var inputLine = it.readLine()
                            // Add each line to the response container
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }
                            it.close()
                            println(">>>> Response: $response")
                            onResponse(
                                ApiCallModel(
                                    dateInMillis = systemTimeBeforeExecution,
                                    requestUrl = url,
                                    requestMethod = method,
                                    requestHeaders = headers,
                                    requestBody = body,
                                    requestFile = file,
                                    requestQueries = URL(url).query?.split("&")?.associate {
                                        val split = it.split("=")
                                        split[0] to split[1]
                                    },
                                    responseCode = connection.responseCode,
                                    responseMessage = connection.responseMessage,
                                    responseHeaders = connection.headerFields,
                                    responseBody = response.toString(),
                                    responseError = null,
                                    executionTime = System.currentTimeMillis() - systemTimeBeforeExecution
                                )
                            )
                        }
                else
                    BufferedReader(InputStreamReader(connection.errorStream))
                        .use {
                            val error = StringBuffer()
                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                error.append(inputLine)
                                inputLine = it.readLine()
                            }
                            it.close()
                            println(">>>> Error: $error")
                            onResponse(
                                ApiCallModel(
                                    dateInMillis = systemTimeBeforeExecution,
                                    requestUrl = url,
                                    requestMethod = method,
                                    requestHeaders = headers,
                                    requestBody = body,
                                    requestFile = file,
                                    requestQueries = URL(url).query?.split("&")?.associate {
                                        val split = it.split("=")
                                        split[0] to split[1]
                                    },
                                    responseCode = connection.responseCode,
                                    responseMessage = connection.responseMessage,
                                    responseHeaders = connection.headerFields,
                                    responseBody = null,
                                    responseError = error.toString(),
                                    executionTime = System.currentTimeMillis() - systemTimeBeforeExecution
                                )
                            )
                        }
            } catch (e: Exception) { // UnknownHostException, SocketTimeoutException, etc..
                Log.e(null, e.stackTraceToString())
                onResponse(
                    ApiCallModel(
                        dateInMillis = systemTimeBeforeExecution,
                        requestUrl = url,
                        requestMethod = method,
                        requestHeaders = headers,
                        requestBody = body,
                        requestFile = file,
                        requestQueries = URL(url).query?.split("&")?.associate {
                            val split = it.split("=")
                            split[0] to split[1]
                        },
                        responseError = e.message,
                        executionTime = System.currentTimeMillis() - systemTimeBeforeExecution
                    )
                )
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) { // MalformedURLException etc..
            Log.e(null, e.stackTraceToString())
            onResponse(
                ApiCallModel(
                    dateInMillis = systemTimeBeforeExecution,
                    requestUrl = url,
                    requestMethod = method,
                    requestHeaders = headers,
                    requestBody = body,
                    requestFile = file,
                    responseError = e.message
                )
            )
            return
        }
    }

    enum class HttpMethod {
        GET, POST
    }

    private const val crlf = "\r\n"
    private const val twoHyphens = "--"
    private const val boundary = "*****"
}