package com.github.hattamaulana.android.core.common

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.github.hattamaulana.android.core.util.FragmentInflater

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

abstract class BaseAdapter<M : DiffUtilCallbackItem, VB: ViewBinding>(
    private val inflater: FragmentInflater<VB>
) : ListAdapter<M, ViewHolder<VB>>(BaseObject<M>()) {

    abstract fun binding(holder: VB, item: M, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<VB> {
        return ViewHolder(inflater.invoke(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder<VB>, position: Int) {
        binding(holder.binding, getItem(position), position)
    }
}
