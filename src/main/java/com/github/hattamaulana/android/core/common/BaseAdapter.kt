package com.github.hattamaulana.android.core.common

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

interface DiffUtilCallbackItem {
    fun diff(): String
}

open class BaseObject<T : DiffUtilCallbackItem> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.diff() == newItem.diff()
    }
}


class ViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)

abstract class BaseAdapter<M : DiffUtilCallbackItem> :
    ListAdapter<M, ViewHolder<ViewBinding>>(BaseObject<M>()) {

    protected lateinit var context: Context

    abstract fun binding(holder: ViewBinding, item: M, position: Int)

    abstract fun createBinding(
        viewType: Int,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ViewBinding

    abstract override fun getItemViewType(position: Int): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ViewBinding> {
        context = parent.context

        val inflater = LayoutInflater.from(context)
        val binding = createBinding(viewType, inflater, parent)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<ViewBinding>, position: Int) {
        binding(holder.binding, getItem(position), position)
    }
}
