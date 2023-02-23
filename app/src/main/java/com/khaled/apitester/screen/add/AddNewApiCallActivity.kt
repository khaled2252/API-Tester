package com.khaled.apitester.screen.add

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.khaled.apitester.R
import com.khaled.apitester.databinding.ActivityAddNewApiCallBinding
import com.khaled.apitester.screen.details.ApiCallDetailsActivity
import com.khaled.apitester.screen.main.MainActivity.Companion.IS_API_CALL_ADDED
import com.khaled.apitester.util.extension.makeFileFromUri


class AddNewApiCallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewApiCallBinding
    private lateinit var viewModel: AddNewApiCallViewModel

    @SuppressLint("SetTextI18n")
    private val getFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val file = makeFileFromUri(uri)
            binding.labelFile.text = "File: ${file.name}"
            viewModel.currentFile = file
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        )[AddNewApiCallViewModel::class.java]
        binding = ActivityAddNewApiCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            radioGroupRequestType.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId == R.id.radio_button_get) {
                    radioGroupBodyType.visibility = View.GONE
                    inputLayoutJsonBody.visibility = View.INVISIBLE
                    labelFile.visibility = View.GONE
                    btnFileUpload.visibility = View.GONE
                }
                else
                {
                    radioGroupBodyType.visibility = View.VISIBLE
                    handlePostRequestInputs(radioGroupBodyType.checkedRadioButtonId)
                }
            }

            radioGroupBodyType.setOnCheckedChangeListener { _, checkedId ->
                handlePostRequestInputs(checkedId)
            }

            btnFileUpload.setOnClickListener {
                if (ContextCompat.checkSelfPermission(this@AddNewApiCallActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@AddNewApiCallActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
                }

                getFileLauncher.launch("*/*") //  "*/*" i.e. Allow all types of files
            }

            btnSubmit.setOnClickListener {
                if (radioGroupRequestType.checkedRadioButtonId == R.id.radio_button_get) {
                    viewModel.makeGetRequest(
                        editTextUrl.text.toString(),
                        editTextHeaders.text.toString()
                    ).observe(this@AddNewApiCallActivity) { state ->
                        handleRequestState(state)
                    }
                } else if (radioGroupRequestType.checkedRadioButtonId == R.id.radio_button_post) {
                    viewModel.makePostRequest(
                        editTextUrl.text.toString(),
                        editTextHeaders.text.toString(),
                        editTextJsonBody.text.toString(),
                        isJson = radioGroupBodyType.checkedRadioButtonId == R.id.radio_button_json,
                    ).observe(this@AddNewApiCallActivity) { state ->
                        handleRequestState(state)
                    }
                }

            }
        }
    }

    private fun handlePostRequestInputs(
        checkedId: Int
    ) {
        with(binding) {
            if (checkedId == R.id.radio_button_json) {
                inputLayoutJsonBody.visibility = View.VISIBLE
                labelFile.visibility = View.GONE
                btnFileUpload.visibility = View.GONE
            } else {
                inputLayoutJsonBody.visibility = View.INVISIBLE
                labelFile.visibility = View.VISIBLE
                btnFileUpload.visibility = View.VISIBLE
            }
        }
    }

    private fun handleRequestState(state: AddNewApiCallViewModel.ViewState) {
        when (state) {
            is AddNewApiCallViewModel.ViewState.Loading -> {
                binding.loading.visibility = View.VISIBLE
            }
            is AddNewApiCallViewModel.ViewState.Finished -> {
                binding.loading.visibility = View.GONE
                Toast.makeText(
                    this@AddNewApiCallActivity,
                    "Api call added successfully",
                    Toast.LENGTH_SHORT
                ).show()
                intent.putExtra(IS_API_CALL_ADDED, true)
                setResult(RESULT_OK, intent)
                finish()
                startActivity(
                    Intent(
                        this@AddNewApiCallActivity,
                        ApiCallDetailsActivity::class.java
                    ).apply {
                        putExtra(
                            ApiCallDetailsActivity.API_CALL_MODEL_DATE_IDENTIFIER,
                            state.apiCallModel.dateInMillis
                        )
                    })
            }
            is AddNewApiCallViewModel.ViewState.NoInternet -> {
                Toast.makeText(
                    this@AddNewApiCallActivity,
                    "Please check your internet connection and try again",
                    Toast.LENGTH_LONG
                ).show()
            }
            is AddNewApiCallViewModel.ViewState.InvalidUrl -> {
                Toast.makeText(
                    this@AddNewApiCallActivity,
                    "Please enter a valid URL",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}