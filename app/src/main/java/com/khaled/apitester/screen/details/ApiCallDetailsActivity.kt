package com.khaled.apitester.screen.details

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.khaled.apitester.databinding.ActivityApiCallDetailsBinding
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
    private lateinit var viewModel: ApiCallDetailsViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        )[ApiCallDetailsViewModel::class.java]
        binding = ActivityApiCallDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()
        val apiCallModelDateIdentifier = intent.getLongExtra(API_CALL_MODEL_DATE_IDENTIFIER, 0L)
        viewModel.fetchApiCallModel(apiCallModelDateIdentifier)
    }

    private fun observeViewModel() {
        viewModel.apiCallModelLiveData.observe(this) { apiCallModel ->
            with(binding) {
                // region Request
                tvDate.text = SimpleDateFormat(
                    "M/d/yyyy h:mm:ss a",
                    Locale.ENGLISH
                ).format(Date(apiCallModel?.dateInMillis ?: System.currentTimeMillis()))
                tvUrl.text = apiCallModel?.requestUrl
                tvRequestMethodType.text = apiCallModel?.requestMethod?.name
                tvRequestHeaders.text = apiCallModel?.requestHeaders?.toPrettyString() ?: "N/A"
                tvRequestQueries.text = apiCallModel?.requestQueries?.toPrettyString() ?: "N/A"
                tvRequestBody.text = apiCallModel?.requestBody.toNullIfBlank() ?: "N/A"
                if (apiCallModel?.requestFile != null) {
                    tvRequestFileName.text = apiCallModel.requestFile.name
                    btnOpenFile.setOnClickListener {
                        openFile(apiCallModel.requestFile)
                    }
                } else {
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

}