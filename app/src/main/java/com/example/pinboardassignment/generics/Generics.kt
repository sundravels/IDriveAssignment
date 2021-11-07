package com.example.pinboardassignment.generics

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pinboardassignment.utils.LoggerClass


/**
 * @author: SundravelS
 *
 * @param arrayList: ArrayList
 *
 * @desc: Below class (adapter is generic class to loading recycler views adapter
 */


abstract class GenericAdapter<T>(val arrayList: ArrayList<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    abstract fun bindCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    abstract fun bindBindViewHolder(holder: RecyclerView.ViewHolder, model: T)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return bindCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bindBindViewHolder(holder, arrayList.get(position))
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun getItemCount(): Int {
        return arrayList.size
    }

}

/**
 * @author: SundravelS
 *
 * @param itemView: View
 *
 * @desc: Below class is generic class for extending recycler view holder
 */


class GenericItemViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView)