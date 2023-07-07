package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.model.BeforeAfterImageModel
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.PictureListener
import kotlinx.android.synthetic.main.load_images_layout.view.*


class ImagesAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = mutableListOf<BeforeAfterImageModel>()

    private var mPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_images_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(imageList: List<BeforeAfterImageModel>, position: Int) {
        mList.clear()
        mList.addAll(imageList)
        mPosition = position
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            if (mList[absoluteAdapterPosition].simpleImage.isNotEmpty()) {
                itemView.clImage.visibility = View.VISIBLE
                itemView.clBeforeAfterView.visibility = View.GONE

                Glide
                    .with(mFragment)
                    .load(mList[absoluteAdapterPosition].simpleImage)
                    .centerCrop()
                    .placeholder(R.color.colorPrimaryText)
                    .into(itemView.sdvSimpleImage)

            } else if (mList[absoluteAdapterPosition].beforeImage.isNotEmpty()) {
                itemView.clImage.visibility = View.GONE
                itemView.clBeforeAfterView.visibility = View.VISIBLE

                Glide
                    .with(mFragment)
                    .load(mList[absoluteAdapterPosition].beforeImage)
                    .centerCrop()
                    .placeholder(R.color.colorPrimaryText)
                    .into(itemView.sdvBeforeImage)

                Glide
                    .with(mFragment)
                    .load(mList[absoluteAdapterPosition].afterImage)
                    .centerCrop()
                    .placeholder(R.color.colorPrimaryText)
                    .into(itemView.sdvAfterImage)
            }

            if (absoluteAdapterPosition == mPosition) {
                itemView.ivSelection.visibility = View.VISIBLE
            } else {
                itemView.ivSelection.visibility = View.GONE
            }

            itemView.ivCancelImage.setOnClickListener {
                (mFragment as PictureListener).onDeleteImage(absoluteAdapterPosition)
            }

            itemView.ivCancelBefore.setOnClickListener {
                (mFragment as PictureListener).onDeleteImage(absoluteAdapterPosition)
            }

            itemView.setOnClickListener {
                mPosition = absoluteAdapterPosition
                (mFragment as PictureListener).onImageClick(absoluteAdapterPosition)
                notifyDataSetChanged()
            }

        }
    }
}