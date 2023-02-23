package com.khaled.apitester.model

import com.khaled.apitester.util.HttpUtils
import java.io.File

/**
 * @Author: Khaled Ahmed
 * @Date: 2/22/2023
 */

data class ApiCallModel(
    val dateInMillis: Long,

    // region Request data
    val requestUrl: String,
    val requestMethod: HttpUtils.HttpMethod,
    val requestHeaders: Map<String?, String>? = null,
    val requestBody: String? = null,
    val requestFile: File? = null,
    val requestQueries: Map<String?, String>? = null,
    // endregion

    // region Response data
    val responseCode: Int? = null,
    val responseMessage: String? = null,
    val responseHeaders: Map<String?, List<String>>? = null,
    val responseBody: String? = null,
    val responseError: String? = null,
    val executionTime: Long? = null
    // endregion
)