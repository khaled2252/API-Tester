package com.khaled.apitester.screen.details

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.khaled.apitester.databinding.ActivityApiCallDetailsBinding
import com.khaled.apitester.util.SharedPrefsUtils
import com.khaled.apitester.util.extension.openFile
import com.khaled.apitester.util.extension.toNullIfBlank
import com.khaled.apitester.util.extension.toPrettyString
import java.text.SimpleDateFormat
import java.util.*


class ApiCallDetailsActivity : AppCompatActivity() {

    companion object {
        const val API_CALL_MODEL_DATE_IDENTIFIER = "API_CALL_MODEL_DATE_IDENTIFIER"
    }

    private lateinit var binding: ActivityApiCallDetailsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApiCallDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiCallModelDateIdentifier = intent.getLongExtra(API_CALL_MODEL_DATE_IDENTIFIER, 0L)
        val apiCallModel = SharedPrefsUtils(this).getApiCallModel(apiCallModelDateIdentifier)

        with(binding) {
            // region Request
            tvDate.text = SimpleDateFormat("M/d/yyyy h:mm:ss a", Locale.ENGLISH).format(Date(apiCallModel?.dateInMillis ?: System.currentTimeMillis()))
            tvUrl.text = apiCallModel?.requestUrl
            tvRequestMethodType.text = apiCallModel?.requestMethod?.name
            tvRequestHeaders.text = apiCallModel?.requestHeaders?.toPrettyString() ?: "N/A"
            tvRequestQueries.text = apiCallModel?.requestQueries?.toPrettyString() ?: "N/A"
            tvRequestBody.text = apiCallModel?.requestBody.toNullIfBlank() ?: "N/A"
            if (apiCallModel?.requestFile != null)
            {
                tvRequestFileName.text = apiCallModel.requestFile.name
                btnOpenFile.setOnClickListener {
                    openFile(apiCallModel.requestFile)
                }
            }
            else
            {
                tvRequestFileName.text = "N/A"
                btnOpenFile.visibility = android.view.View.GONE
            }
            // endregion

            // region Response
            tvResponseCode.text = apiCallModel?.responseCode?.toString()
            tvResponseHeaders.text = apiCallModel?.responseHeaders?.toPrettyString()
            tvResponseBody.text = apiCallModel?.responseBody.toNullIfBlank() ?: "N/A"
            tvResponseError.text = apiCallModel?.responseError.toNullIfBlank() ?: "N/A"
            tvExecutionTime.text = "${apiCallModel?.executionTime} ms"
            // endregion
        }
    }

}