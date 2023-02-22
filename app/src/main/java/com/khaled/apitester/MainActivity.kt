package com.khaled.apitester

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.khaled.apitester.databinding.ActivityMainBinding


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

        binding.rvPreviousCalls.adapter = adapter
        binding.btnNewApiCall.setOnClickListener {
            viewModel.addData().observe(this) { newList ->
                if (newList == null)
                    Toast.makeText(
                        this,
                        "Please check your internet connection and try again",
                        Toast.LENGTH_LONG
                    ).show()
                else if (newList.isNotEmpty()) {
                    adapter.submitList(newList)
                    binding.labelNoPreviousCalls.visibility = android.view.View.GONE
                } else
                    binding.labelNoPreviousCalls.visibility = android.view.View.VISIBLE
            }
        }

        viewModel.getData().observe(this) { currentList ->
            if (currentList.isNotEmpty()) {
                adapter.submitList(currentList)
                binding.labelNoPreviousCalls.visibility = android.view.View.GONE
            } else
                binding.labelNoPreviousCalls.visibility = android.view.View.VISIBLE
        }
    }

    private fun onPreviousApiCallItemClicked(apiCallModel: ApiCallModel) {
        // Todo: Navigate to Api call details screen
    }
}