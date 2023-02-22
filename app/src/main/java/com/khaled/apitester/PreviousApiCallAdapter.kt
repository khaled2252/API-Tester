package com.khaled.apitester

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.khaled.apitester.databinding.ViewHolderPreviousApiCallBinding

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

    inner class ViewHolderPreviousApiCallViewHolder(private val binding: ViewHolderPreviousApiCallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: ApiCallModel) {
            binding.urlValue.text = model.requestUrl
            binding.dateValue.text = model.dateInMillis.toString()
            itemView.setOnClickListener { onItemClicked(model) }
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



