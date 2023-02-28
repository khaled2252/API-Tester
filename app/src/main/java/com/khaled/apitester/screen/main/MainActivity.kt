package com.khaled.apitester.screen.main

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.khaled.apitester.R
import com.khaled.apitester.databinding.ActivityMainBinding
import com.khaled.apitester.databinding.DialogSortingBinding
import com.khaled.apitester.model.ApiCallModel
import com.khaled.apitester.screen.add.AddNewApiCallActivity
import com.khaled.apitester.screen.details.ApiCallDetailsActivity
import com.khaled.apitester.screen.details.ApiCallDetailsActivity.Companion.API_CALL_MODEL_DATE_IDENTIFIER


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val adapter = PreviousApiCallAdapter(::onPreviousApiCallItemClicked)
    private val addNewApiCallActivityLauncher = registerForActivityResult(object : ActivityResultContract<Unit, Boolean>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(context, AddNewApiCallActivity::class.java)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
            return intent?.getBooleanExtra(IS_API_CALL_ADDED, false) ?: false
        }

    }) { isApiCallAdded ->
        if (isApiCallAdded)
            viewModel.getPreviousApiCalls()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        )[MainViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()
        setListeners()

        viewModel.getPreviousApiCalls()
    }

    private fun setListeners() {
        with(binding) {
            rvPreviousCalls.adapter = adapter
            btnSort.setOnClickListener { showSortDialog() }
            btnNewApiCall.setOnClickListener {
                addNewApiCallActivityLauncher.launch(Unit)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.dataListLiveData.observe(this) { currentList ->
            if (currentList.isNotEmpty()) {
                adapter.submitList(currentList)
                Handler(Looper.getMainLooper()).postDelayed({ binding.rvPreviousCalls.smoothScrollToPosition(0) }, 300)
                binding.labelNoPreviousCalls.visibility = android.view.View.GONE
            } else
                binding.labelNoPreviousCalls.visibility = android.view.View.VISIBLE
        }
    }

    private fun onPreviousApiCallItemClicked(apiCallModel: ApiCallModel) {
        startActivity(Intent(this, ApiCallDetailsActivity::class.java).apply {
            putExtra(API_CALL_MODEL_DATE_IDENTIFIER, apiCallModel.dateInMillis)
        })
    }

    private fun showSortDialog() {
        val dialog = Dialog(this)
        val binding = DialogSortingBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        with(binding){
            btnDismiss.setOnClickListener { dialog.dismiss() }
            fun getSortOptionButtonId(): Int {
                return when (viewModel.selectedSort) {
                    MainViewModel.SortOption.DATE -> R.id.radio_button_date
                    MainViewModel.SortOption.EXECUTION_TIME_ASCENDING -> R.id.radio_button_execution_time_asc
                    MainViewModel.SortOption.EXECUTION_TIME_DESCENDING -> R.id.radio_button_execution_time_desc
                    MainViewModel.SortOption.GET_REQUESTS -> R.id.radio_button_get_requests
                    MainViewModel.SortOption.POST_REQUESTS -> R.id.radio_button_post_requests
                    MainViewModel.SortOption.SUCCESS_REQUESTS -> R.id.radio_button_success_requests
                    MainViewModel.SortOption.FAILURE_REQUESTS -> R.id.radio_button_failure_requests
                }
            }

            fun getSortOptionFromButtonId(buttonId: Int): MainViewModel.SortOption {
                return when (buttonId) {
                    R.id.radio_button_date -> MainViewModel.SortOption.DATE
                    R.id.radio_button_execution_time_asc -> MainViewModel.SortOption.EXECUTION_TIME_ASCENDING
                    R.id.radio_button_execution_time_desc -> MainViewModel.SortOption.EXECUTION_TIME_DESCENDING
                    R.id.radio_button_get_requests -> MainViewModel.SortOption.GET_REQUESTS
                    R.id.radio_button_post_requests -> MainViewModel.SortOption.POST_REQUESTS
                    R.id.radio_button_success_requests -> MainViewModel.SortOption.SUCCESS_REQUESTS
                    R.id.radio_button_failure_requests -> MainViewModel.SortOption.FAILURE_REQUESTS
                    else -> throw IllegalArgumentException("Unknown button ID: $buttonId")
                }
            }

            radioGroupSortOptions.check(getSortOptionButtonId())

            radioGroupSortOptions.setOnCheckedChangeListener { _, checkedId ->
                val newSortOption = getSortOptionFromButtonId(checkedId)
                viewModel.applyNewSort(newSortOption)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    companion object {
        const val IS_API_CALL_ADDED = "IS_API_CALL_ADDED"
    }
}