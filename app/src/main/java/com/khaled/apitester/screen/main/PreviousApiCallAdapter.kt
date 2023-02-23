package com.khaled.apitester.screen.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.khaled.apitester.model.ApiCallModel
import com.khaled.apitester.databinding.ViewHolderPreviousApiCallBinding
import com.khaled.apitester.util.HttpUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author: Khaled Ahmed
 * @Date: 2/22/2023
 */

class PreviousApiCallAdapter (private val onItemClicked: (ApiCallModel) -> Unit):
    ListAdapter<ApiCallModel, PreviousApiCallAdapter.ViewHolderPreviousApiCallViewHolder>(
        MyDataDiffCallback
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderPreviousApiCallViewHolder {
        val binding = ViewHolderPreviousApiCallBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderPreviousApiCallViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderPreviousApiCallViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun sortBy(sortOption: MainViewModel.SortOption) {
        when(sortOption){
            MainViewModel.SortOption.DATE -> submitList(currentList.sortedByDescending { it.dateInMillis })
            MainViewModel.SortOption.EXECUTION_TIME_ASCENDING -> submitList(currentList.sortedBy { it.executionTime })
            MainViewModel.SortOption.EXECUTION_TIME_DESCENDING -> submitList(currentList.sortedByDescending { it.executionTime })
            MainViewModel.SortOption.GET_REQUESTS -> submitList(currentList.sortedByDescending { it.requestMethod == HttpUtils.HttpMethod.GET })
            MainViewModel.SortOption.POST_REQUESTS -> submitList(currentList.sortedByDescending { it.requestMethod == HttpUtils.HttpMethod.POST })
            MainViewModel.SortOption.SUCCESS_REQUESTS -> submitList(currentList.sortedByDescending { (it.responseCode?.div(100) ?: 200) == 2 })
            MainViewModel.SortOption.FAILURE_REQUESTS -> submitList(currentList.sortedByDescending { (it.responseCode?.div(100) ?: 200) != 2 })
        }
    }

    inner class ViewHolderPreviousApiCallViewHolder(private val binding: ViewHolderPreviousApiCallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: ApiCallModel) {
            with(binding){
                tvUrl.text = model.requestUrl.trim()
                val dateFormatted = SimpleDateFormat("M/d/yyyy h:mm:ss a", Locale.ENGLISH).format(Date(model.dateInMillis))
                tvDate.text = dateFormatted
                tvExecutionTime.text = model.executionTime.toString() + " ms"
                tvRequestType.text = model.requestMethod.name
                tvResponseStatus.text = if((model.responseCode?.div(100) ?: 200) == 2) "SUCCESS" else "FAILURE"
                itemView.setOnClickListener { onItemClicked(model) }
            }

        }
    }

    companion object MyDataDiffCallback : DiffUtil.ItemCallback<ApiCallModel>() {
        override fun areItemsTheSame(
            oldItem: ApiCallModel,
            newItem: ApiCallModel
        ): Boolean {
            return oldItem.dateInMillis == newItem.dateInMillis
        }

        override fun areContentsTheSame(
            oldItem: ApiCallModel,
            newItem: ApiCallModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}



