package com.berry.traveldiary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.berry.traveldiary.R
import com.berry.traveldiary.model.Photos
import com.berry.traveldiary.uitility.CommonUtils
import com.google.android.material.imageview.ShapeableImageView

class GalleryAdapter(
//    private val images: List<Int>,
    private val photosList: MutableList<Photos>,
    onItemClickListener: OnItemClickListener,
    saveClickListener: SaveClickListener
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    private var monItemClickListener: OnItemClickListener = onItemClickListener
    private var mSaveClickListener: SaveClickListener = saveClickListener


    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ShapeableImageView = itemView.findViewById(R.id.imageView)
        val caption: EditText = itemView.findViewById(R.id.tvCaption)
        val btnUpdate: ImageView = itemView.findViewById(R.id.btnUpdate)
        val btnAddNew: ImageView = itemView.findViewById(R.id.btnAddblank)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        return GalleryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {

        if (photosList[position].imagePath == CommonUtils.NO_IMAGE) {
//            holder.imageView.setImageDrawable(holder.btnUpdate.resources.getDrawable(R.drawable.ic_menu_gallery))
        } else {
            holder.imageView.setImageURI(photosList[position].imagePath.toUri())
        }

        holder.caption.setText(photosList[position].caption)
        holder.imageView.setOnClickListener {
            monItemClickListener.monItemClickListener(
                position,
                photosList[position].photoId,
                holder.imageView,
                false
            )
        }

        holder.btnAddNew.setOnClickListener {
            monItemClickListener.monItemClickListener(
                position,
                photosList[position].photoId,
                holder.imageView,
                true
            )
        }

        holder.btnUpdate.setOnClickListener {
            mSaveClickListener.mSaveClickListener(
                position,
                photosList[position].photoId,
                photosList[position].imagePath,
                holder.caption.text.toString(),
                holder.imageView,
                false
            )
        }

        holder.btnDelete.setOnClickListener {
            mSaveClickListener.mSaveClickListener(
                position,
                photosList[position].photoId,
                photosList[position].imagePath,
                holder.caption.text.toString(),
                holder.imageView,
                true
            )
        }

    }

    override fun getItemCount(): Int {
        return photosList.size
    }

    interface OnItemClickListener {
        fun monItemClickListener(position: Int, imgPos: Int, view: View, forAddBlank: Boolean)
    }

    interface SaveClickListener {
        fun mSaveClickListener(
            position: Int,
            photoId: Int,
            imagepath: String,
            desc: String,
            view: View,
            isforDelete: Boolean
        )
    }


}