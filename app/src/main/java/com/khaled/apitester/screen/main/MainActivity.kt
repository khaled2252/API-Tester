package com.khaled.apitester.screen.main

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.khaled.apitester.R
import com.khaled.apitester.model.ApiCallModel
import com.khaled.apitester.databinding.ActivityMainBinding
import com.khaled.apitester.databinding.DialogSortingBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val adapter = PreviousApiCallAdapter(::onPreviousApiCallItemClicked)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        )[MainViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            rvPreviousCalls.adapter = adapter
            btnSort.setOnClickListener { showSortDialog() }
            btnNewApiCall.setOnClickListener {
                viewModel.addData().observe(this@MainActivity) { newList ->
                    if (newList == null)
                        Toast.makeText(
                            this@MainActivity,
                            "Please check your internet connection and try again",
                            Toast.LENGTH_LONG
                        ).show()
                    else if (newList.isNotEmpty()) {
                        adapter.submitList(newList)
                        rvPreviousCalls.smoothScrollToPosition(0)
                        labelNoPreviousCalls.visibility = android.view.View.GONE
                    } else
                        labelNoPreviousCalls.visibility = android.view.View.VISIBLE
                }
            }
            viewModel.getData().observe(this@MainActivity) { currentList ->
                if (currentList.isNotEmpty()) {
                    adapter.submitList(currentList)
                    labelNoPreviousCalls.visibility = android.view.View.GONE
                } else
                    labelNoPreviousCalls.visibility = android.view.View.VISIBLE
            }
            viewModel.selectedSort.observe(this@MainActivity){ sortOption ->
                adapter.sortBy(sortOption)
                Handler(Looper.getMainLooper()).postDelayed({ rvPreviousCalls.smoothScrollToPosition(0) }, 300)
            }
        }
    }

    private fun onPreviousApiCallItemClicked(apiCallModel: ApiCallModel) {
        // Todo: Navigate to Api call details screen
    }

    private fun showSortDialog() {
        val dialog = Dialog(this)
        val binding = DialogSortingBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        with(binding){
            btnDismiss.setOnClickListener { dialog.dismiss() }
            fun getSortOptionButtonId(): Int {
                return when (viewModel.selectedSort.value) {
                    MainViewModel.SortOption.DATE -> R.id.radio_button_date
                    MainViewModel.SortOption.EXECUTION_TIME_ASCENDING -> R.id.radio_button_execution_time_asc
                    MainViewModel.SortOption.EXECUTION_TIME_DESCENDING -> R.id.radio_button_execution_time_desc
                    MainViewModel.SortOption.GET_REQUESTS -> R.id.radio_button_get_requests
                    MainViewModel.SortOption.POST_REQUESTS -> R.id.radio_button_post_requests
                    MainViewModel.SortOption.SUCCESS_REQUESTS -> R.id.radio_button_success_requests
                    MainViewModel.SortOption.FAILURE_REQUESTS -> R.id.radio_button_failure_requests
                    else -> return -1
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
                viewModel.setNewSort(newSortOption)
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}