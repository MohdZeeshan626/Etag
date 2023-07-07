package com.max360group.cammax360.views.adapters

import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Media
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.utils.JobsConstants.JOB_KIND_PHOTO
import kotlinx.android.synthetic.main.item_video_layout.view.*
import kotlinx.android.synthetic.main.load_jobs_image.view.*

class JobsImageAdapter(var mFragment: Fragment, var mList: List<Media>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_jobs_image))
    }

    override fun getItemCount(): Int {
        return if (mList.size < 4) {
            mList.size
        } else {
            4
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            //Get screen Width
            val displayMetrics = DisplayMetrics()
            (mFragment).requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels

            val mWidth = width / 4 - 84
            itemView.sdvImage.layoutParams.height = mWidth

            val imageName =
                mList[absoluteAdapterPosition].medias!![0].media.split("$JOB_KIND_PHOTO/")

            // Check if local file exists, if it exists set file from local else downloads from server
            val file = GeneralFunctions
                .getLocalMediaFile(mFragment.requireActivity(), imageName[1])
            if (file.exists()) {
                itemView.sdvImage.setImageURI(
                    GeneralFunctions.getLocalImageFile(file)
                )
            } else {
                itemView.sdvImage.setImageURI(mList[absoluteAdapterPosition].medias!![0].mediaURL)
            }

        }
    }
}