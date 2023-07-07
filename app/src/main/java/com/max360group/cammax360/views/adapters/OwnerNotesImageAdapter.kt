package com.max360group.cammax360.views.adapters

import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Media
import com.max360group.cammax360.repository.models.NotesMedia
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.utils.JobsConstants
import com.max360group.cammax360.views.utils.JobsConstants.JOB_KIND_PHOTO
import kotlinx.android.synthetic.main.item_video_layout.view.*
import kotlinx.android.synthetic.main.load_jobs_image.view.*
import kotlinx.android.synthetic.main.load_note_media_selection.view.*
import java.io.File

class OwnerNotesImageAdapter(var mFragment: Fragment, var mImageList: List<NotesMedia>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_owner_notes_image))
    }

    override fun getItemCount(): Int {
        return mImageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            when (mImageList[absoluteAdapterPosition].kind) {
                "photo" -> {
                    val imageName =
                        mImageList[absoluteAdapterPosition].media.split("${"photo"}/")
                    // Check if local file exists, if it exists set file from local else downloads from server
                    val file = GeneralFunctions
                        .getLocalMediaFile(mFragment.requireActivity(), imageName[1])
                    if (file.exists()) {
                        itemView.sdvImage.setImageURI(
                            GeneralFunctions.getLocalImageFile(file)
                        )
                    } else {
                        itemView.sdvImage.setImageURI(mImageList[absoluteAdapterPosition].mediaURL)
                    }

                }
                "video" -> {
                    val imageName =
                        mImageList[absoluteAdapterPosition].thumbnail.split("${"photo"}/")
                    // Check if local file exists, if it exists set file from local else downloads from server
                    val file = GeneralFunctions
                        .getLocalMediaFile(mFragment.requireActivity(), imageName[1])
                    if (file.exists()) {
                        itemView.sdvImage.setImageURI(
                        GeneralFunctions.getLocalImageFile(file)
                        )
                    } else {
                        itemView.sdvImage.setImageURI(mImageList[absoluteAdapterPosition].thumbnailURL)
                    }

                }
                else -> {
                    itemView.sdvImage.setImageResource(R.drawable.ic_document)
                }
            }
        }
    }
}