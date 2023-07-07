package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.model.VideosModel
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.PictureListener
import com.max360group.cammax360.views.interfaces.VideoListener
import kotlinx.android.synthetic.main.load_images_layout.view.*


class VideosAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = mutableListOf<VideosModel>()

    private var mPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_video_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(videoList: List<VideosModel>, position: Int) {
        mList.clear()
        mList.addAll(videoList)
        mPosition = position
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {

            Glide
                .with(mFragment)
                .load(mList[adapterPosition].video)
                .centerCrop()
                .placeholder(R.color.colorPrimaryText)
                .into(itemView.sdvSimpleImage)

            if (mPosition==adapterPosition){
                itemView.ivSelection.visibility=View.VISIBLE
            }else{
                itemView.ivSelection.visibility=View.GONE
            }

            itemView.ivCancelImage.setOnClickListener {
                (mFragment as VideoListener).onDeleteImage(adapterPosition)
            }

            itemView.setOnClickListener {
                mPosition = adapterPosition
                (mFragment as VideoListener).onVideoClick(adapterPosition)
                notifyDataSetChanged()
            }

        }
    }
}