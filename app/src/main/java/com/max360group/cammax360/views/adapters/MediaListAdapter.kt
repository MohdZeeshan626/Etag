package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.MediaData1
import com.max360group.cammax360.repository.models.Medias
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.utils.JobsConstants
import kotlinx.android.synthetic.main.fragment_before_after_detail.*
import kotlinx.android.synthetic.main.load_images_layout.view.*
import kotlinx.android.synthetic.main.load_images_layout.view.ivSelection
import kotlinx.android.synthetic.main.load_images_layout.view.sdvSimpleImage
import kotlinx.android.synthetic.main.load_media_layout.view.*
import java.io.File


class MediaListAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mMediaData1 = MediaData1()

    private var mPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_media_layout))
    }

    override fun getItemCount(): Int {
        return 1
    }

    fun updateData(docsList: MediaData1) {
        mMediaData1 = docsList
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            if (mPosition == adapterPosition) {
                itemView.ivSelection.visibility = View.VISIBLE
            } else {
                itemView.ivSelection.visibility = View.GONE
            }

            (mMediaData1.creatorId.firstName + " " + mMediaData1.creatorId.lastName).also {
                itemView.tvUserName.text = it
            }
            itemView.tvName.text = mMediaData1.medias!![0].name

            when (mMediaData1.kind) {
                JobsConstants.JOB_KIND_PHOTO -> {
                    //Set before image
                    if (mMediaData1.medias!![0].mediaURL.startsWith("https://")) {
                        val imageName =
                            mMediaData1.medias!![0].media.split("${JobsConstants.JOB_KIND_PHOTO}/")

                        // Check if local file exists, if it exists set file from local else downloads from server
                        val file = GeneralFunctions
                            .getLocalMediaFile(mFragment.requireContext(), imageName[1])
                        if (file.exists()) {
                            itemView.sdvSimpleImage.setImageURI(
                                GeneralFunctions.getLocalImageFile(file)
                            )
                        } else {
                            itemView.sdvSimpleImage.setImageURI(mMediaData1.medias!![0].mediaURL)
                        }

                    } else {
                        itemView.sdvSimpleImage.setImageURI(
                            GeneralFunctions.getLocalImageFile(
                                File(
                                    mMediaData1.medias!![0].mediaURL
                                )
                            )
                        )
                    }
                }

                JobsConstants.JOB_KIND_VIDEO -> {
                    itemView.ivPLay.visibility = View.VISIBLE
                    itemView.sdvSimpleImage.setImageURI(mMediaData1.medias!![0].thumbnailURL)
                }
                else -> {
                    itemView.sdvSimpleImage.setImageResource(R.drawable.ic_document)
                }
            }


            itemView.setOnClickListener {
                mPosition = adapterPosition
                (mFragment as DocsListener).onDocsClick(adapterPosition)
                notifyDataSetChanged()
            }

        }
    }

    interface DocsListener {
        fun onDocsClick(position: Int)
        fun onRemove(position: Int)
    }
}