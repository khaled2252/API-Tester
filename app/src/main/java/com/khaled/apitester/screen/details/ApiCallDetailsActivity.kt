package com.khaled.apitester.screen.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.khaled.apitester.databinding.ActivityApiCallDetailsBinding
import com.khaled.apitester.util.SharedPrefsUtils


class ApiCallDetailsActivity : AppCompatActivity() {

    companion object {
        const val API_CALL_MODEL_DATE_IDENTIFIER = "API_CALL_MODEL_DATE_IDENTIFYFiER"
    }

    private lateinit var binding: ActivityApiCallDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApiCallDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiCallModelDateIdentifier = intent.getLongExtra(API_CALL_MODEL_DATE_IDENTIFIER, 0L)
        val apiCallModel = SharedPrefsUtils(this).getApiCallModel(apiCallModelDateIdentifier)

        with(binding) {
            println(apiCallModel)
        }
    }
}