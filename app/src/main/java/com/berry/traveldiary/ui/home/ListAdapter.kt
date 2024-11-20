package com.berry.traveldiary.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.berry.traveldiary.R
import com.berry.traveldiary.model.DiaryEntries
import com.google.android.material.imageview.ShapeableImageView


class ListAdapter(
    private val mList: MutableList<DiaryEntries>, onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {


    private var monItemClickListener: OnItemClickListener = onItemClickListener
    private var userList = mList
    private val userListCopy: MutableList<DiaryEntries> = ArrayList()

    init {
        userListCopy.addAll(userList)
    }


    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvLoc: TextView = itemView.findViewById(R.id.tvLoc)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val imageView: ShapeableImageView = itemView.findViewById(R.id.imageView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false)

        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return userList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(text: String) {
        var text1 = text
        userList.clear()
        if (text1.isEmpty()) {
            mList.addAll(userListCopy)
        } else {
            text1 = text1.lowercase()
            for (item in userListCopy) {
                if (item.title.lowercase().contains(text1) || item.location.lowercase()
                        .contains(text1)
                ) {
                    userList.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = userList[position]

        if (currentItem.coverImg.isNotEmpty()) {
            holder.imageView.setImageURI(currentItem.coverImg.toUri())
        }

        holder.tvTitle.text = currentItem.title
        holder.tvLoc.text = currentItem.location
        holder.tvDate.text = currentItem.date

        holder.itemView.setOnClickListener {
            monItemClickListener.monItemClickListener(position, userList[position].entryId)
        }
    }

    interface OnItemClickListener {
        fun monItemClickListener(position: Int, entryId: Int)
    }
}