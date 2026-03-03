package com.djyoo.sunflower.common.base

import android.util.Log
import androidx.recyclerview.widget.RecyclerView

/**
 * 공통 RecyclerView.Adapter 기본 구현.
 */
abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    protected val items: MutableList<T> = mutableListOf()

    override fun getItemCount(): Int = items.size

    fun submitItems(newItems: List<T>) {
        Log.e("djLog", "submitItems : $newItems")
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    protected fun getItem(position: Int): T = items[position]
}

