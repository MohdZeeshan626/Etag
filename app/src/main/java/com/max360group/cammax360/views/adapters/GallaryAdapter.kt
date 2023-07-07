package com.max360group.cammax360.views.adapters

import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.JobMediaList
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.JobsLDetailListener
import com.max360group.cammax360.views.utils.JobsConstants
import kotlinx.android.synthetic.main.item_dual_layout.view.*
import kotlinx.android.synthetic.main.item_video_layout.view.*
import kotlinx.android.synthetic.main.load_gallary_layout.view.*
import kotlinx.android.synthetic.main.load_gallary_layout.view.sdvProductImage
import kotlinx.android.synthetic.main.load_jobs_image.view.*
import java.io.File


class GallaryAdapter(
    var mFragment: Fragment,
    var mJobMediaList: ArrayList<JobMediaList>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_SMALL = 3
        const val VIEW_MEDIUM = 2
        const val VIEW_LARGE = 1

        const val VIEW_TYPE_VIDEO = 1
        const val VIEW_TYPE_DUAL = 2
        const val VIEW_TYPE_SIMPLE = 3
        var viewType = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_VIDEO -> {
                VideoViewHolder(parent.inflate(R.layout.item_video_layout))
            }
            VIEW_TYPE_SIMPLE -> {
                SimpleListViewHolder(parent.inflate(R.layout.load_gallary_layout))
            }
            else -> {
                DualListViewHolder(parent.inflate(R.layout.item_dual_layout))
            }
        }


    }

    override fun getItemCount(): Int {
        return mJobMediaList!!.size
    }

    fun updateData(isViewType: Int) {
        viewType = isViewType
        notifyDataSetChanged()

    }

    override fun getItemViewType(position: Int): Int {
        return when (mJobMediaList!![position].kind) {
            "jobPhoto" -> {
                if (mJobMediaList!![position].subKind == "dual") {
                    VIEW_TYPE_DUAL
                } else {
                    VIEW_TYPE_SIMPLE
                }
            }
            else -> VIEW_TYPE_VIDEO
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        when {
            VIEW_TYPE_SIMPLE == getItemViewType(i) -> {
                (holder as SimpleListViewHolder).bindListView(i)
            }
            VIEW_TYPE_DUAL == getItemViewType(i) -> {
                (holder as DualListViewHolder).bindListView(i)
            }
            else -> {
                (holder as VideoViewHolder).bindListView(i)
            }
        }
    }

    private inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            //Get screen Width
            val displayMetrics = DisplayMetrics()
            (mFragment).requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels

            when (viewType) {
                VIEW_LARGE -> {
                    var mWidth = width - 300
                    itemView.sdvVideoImage.layoutParams.height = mWidth

                }
                VIEW_SMALL -> {
                    var mWidth = width / 3 - 54
                    itemView.sdvVideoImage.layoutParams.height = mWidth
                }
                VIEW_MEDIUM -> {
                    var mWidth = width / 2 - 50
                    itemView.sdvVideoImage.layoutParams.height = mWidth
                }

            }

            //Set data
            if (!mJobMediaList!![absoluteAdapterPosition].medias!![0].thumbnailURL.startsWith("https://")) {
                itemView.sdvVideoImage.setImageURI(
                    GeneralFunctions.getLocalImageFile(
                        File(mJobMediaList!![absoluteAdapterPosition].medias!![0].thumbnailURL)
                    )
                )
            } else {
                val imageName =
                    mJobMediaList!![absoluteAdapterPosition].medias!![0].thumbnail.split("${JobsConstants.JOB_KIND_VIDEO}/")
                // Check if local file exists, if it exists set file from local else downloads from server
                val file = GeneralFunctions
                    .getLocalMediaFile(mFragment.requireActivity(), imageName[1])
                if (file.exists()) {
                    itemView.sdvVideoImage.setImageURI(
                        GeneralFunctions.getLocalImageFile(file)
                    )
                } else {
                    itemView.sdvVideoImage.setImageURI(mJobMediaList!![absoluteAdapterPosition].medias!![0].thumbnailURL)
                }
            }

            itemView.tvVideoUserName.text =
                mJobMediaList!![absoluteAdapterPosition].medias!![0].name
            (mJobMediaList!![absoluteAdapterPosition].creatorId.firstName + " " + mJobMediaList!![absoluteAdapterPosition].creatorId.lastName).also {
                itemView.tvVideoName.text = it
            }

            //Set click listener
            itemView.setOnClickListener {
                (mFragment as JobsLDetailListener).onItemClick(
                    mJobMediaList!![absoluteAdapterPosition],
                    0)
            }

            itemView.ivVideoVisibility.setOnClickListener {
                (mFragment as JobsLDetailListener).onVisibilityClick(
                    mJobMediaList!![absoluteAdapterPosition],
                    0)
            }
        }
    }

    private inner class SimpleListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            //Get screen Width
            val displayMetrics = DisplayMetrics()
            (mFragment).requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels

            when (viewType) {
                VIEW_LARGE -> {
                    val mWidth = width - 300
                    itemView.sdvProductImage.layoutParams.height = mWidth
                }
                VIEW_SMALL -> {
                    val mWidth = width / 3 - 54
                    itemView.sdvProductImage.layoutParams.height = mWidth
                }
                VIEW_MEDIUM -> {
                    val mWidth = width / 2 - 50
                    itemView.sdvProductImage.layoutParams.height = mWidth
                }
            }

            //Set data
            if (!mJobMediaList!![absoluteAdapterPosition].medias!![0].mediaURL.startsWith("https://")) {
                itemView.sdvProductImage.setImageURI(
                    GeneralFunctions.getLocalImageFile(
                        File(
                            mJobMediaList!![absoluteAdapterPosition].medias!![0].mediaURL
                        )
                    )
                )
            } else {
                val imageName =
                    mJobMediaList!![absoluteAdapterPosition].medias!![0].media.split("${JobsConstants.JOB_KIND_PHOTO}/")
                // Check if local file exists, if it exists set file from local else downloads from server
                val file = GeneralFunctions
                    .getLocalMediaFile(mFragment.requireActivity(), imageName[1])
                if (file.exists()) {
                    itemView.sdvProductImage.setImageURI(
                        GeneralFunctions.getLocalImageFile(file)
                    )
                } else {
                    itemView.sdvProductImage.setImageURI(mJobMediaList!![absoluteAdapterPosition].medias!![0].mediaURL)
                }
            }


            itemView.tvSimpleImageName.text =
                mJobMediaList!![absoluteAdapterPosition].medias!![0].name
            (mJobMediaList!![absoluteAdapterPosition].creatorId.firstName + " " + mJobMediaList!![absoluteAdapterPosition].creatorId.lastName).also {
                itemView.tvSimpleUseName.text = it
            }

            //Set click listener
            itemView.setOnClickListener {
                (mFragment as JobsLDetailListener).onItemClick(
                    mJobMediaList!![absoluteAdapterPosition],
                    0
                )
            }

            itemView.ivSimpleVisibility.setOnClickListener {
                (mFragment as JobsLDetailListener).onVisibilityClick(
                    mJobMediaList!![absoluteAdapterPosition],
                    0
                )
            }
        }

    }

    private inner class DualListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            //Get screen Width
            val displayMetrics = DisplayMetrics()
            (mFragment).requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels

            when (viewType) {
                VIEW_LARGE -> {
                    var mWidth = width - 300
                    itemView.sdvBeforeImage.layoutParams.height = mWidth
                    itemView.sdvAfterImage.layoutParams.height = mWidth
                }
                VIEW_SMALL -> {
                    var mWidth = width / 3 - 54
                    itemView.sdvBeforeImage.layoutParams.height = mWidth
                    itemView.sdvAfterImage.layoutParams.height = mWidth
                }
                VIEW_MEDIUM -> {
                    var mWidth = width / 2 - 50
                    itemView.sdvBeforeImage.layoutParams.height = mWidth
                    itemView.sdvAfterImage.layoutParams.height = mWidth
                }
            }

            //Set data
            if (!mJobMediaList!![absoluteAdapterPosition].medias!![0].mediaURL.startsWith("https://")) {
                itemView.sdvBeforeImage.setImageURI(
                    GeneralFunctions.getLocalImageFile(
                        File(
                            mJobMediaList!![absoluteAdapterPosition].medias!![0].mediaURL
                        )
                    )
                )
            } else {
                val imageName =
                    mJobMediaList!![absoluteAdapterPosition].medias!![0].media.split("${JobsConstants.JOB_KIND_PHOTO}/")

                // Check if local file exists, if it exists set file from local else downloads from server
                val file = GeneralFunctions
                    .getLocalMediaFile(mFragment.requireActivity(), imageName[1])
                if (file.exists()) {
                    itemView.sdvBeforeImage.setImageURI(
                        GeneralFunctions.getLocalImageFile(file)
                    )
                } else {
                    itemView.sdvBeforeImage.setImageURI(mJobMediaList!![absoluteAdapterPosition].medias!![0].mediaURL)
                }

            }

            if (!mJobMediaList!![absoluteAdapterPosition].medias!![1].mediaURL.startsWith("https://")) {
                itemView.sdvAfterImage.setImageURI(
                    GeneralFunctions.getLocalImageFile(
                        File(
                            mJobMediaList!![absoluteAdapterPosition].medias!![1].mediaURL
                        )
                    )
                )
            } else {
                val imageName =
                    mJobMediaList!![absoluteAdapterPosition].medias!![1].media.split("${JobsConstants.JOB_KIND_PHOTO}/")
                // Check if local file exists, if it exists set file from local else downloads from server
                val file = GeneralFunctions
                    .getLocalMediaFile(mFragment.requireActivity(), imageName[1])
                if (file.exists()) {
                    itemView.sdvAfterImage.setImageURI(
                        GeneralFunctions.getLocalImageFile(file)
                    )
                } else {
                    itemView.sdvAfterImage.setImageURI(mJobMediaList!![absoluteAdapterPosition].medias!![1].mediaURL)
                }
            }

            //If image contain place holder hide the actions
            if (mJobMediaList!![absoluteAdapterPosition].medias!![1].mediaURL.contains("Placeholder_")) {
                itemView.ivShareBefore.visibility = View.GONE
                itemView.ivViewAfter.visibility = View.GONE
            } else {
                itemView.ivShareBefore.visibility = View.VISIBLE
                itemView.ivViewAfter.visibility = View.VISIBLE
            }

            if (mJobMediaList!![absoluteAdapterPosition].medias!![0].mediaURL.contains("Placeholder_")) {
                itemView.ivShareAfter.visibility = View.GONE
                itemView.ivViewBefore.visibility = View.GONE
            } else {
                itemView.ivShareAfter.visibility = View.VISIBLE
                itemView.ivViewBefore.visibility = View.VISIBLE
            }


            itemView.tvDualImageName.text = mJobMediaList!![absoluteAdapterPosition].name
            (mJobMediaList!![absoluteAdapterPosition].creatorId.firstName + " " + mJobMediaList!![absoluteAdapterPosition].creatorId.lastName).also {
                itemView.tvDualUserName.text = it
            }

            itemView.sdvBeforeImage.setOnClickListener {
                (mFragment as JobsLDetailListener).onItemClick(
                    mJobMediaList!![absoluteAdapterPosition],
                    0
                )
            }

            itemView.ivViewAfter.setOnClickListener {
                (mFragment as JobsLDetailListener).onVisibilityClick(
                    mJobMediaList!![absoluteAdapterPosition],
                    1
                )
            }

            itemView.sdvAfterImage.setOnClickListener {
                (mFragment as JobsLDetailListener).onItemClick(
                    mJobMediaList!![absoluteAdapterPosition],
                    1
                )
            }

            itemView.ivViewBefore.setOnClickListener {
                (mFragment as JobsLDetailListener).onVisibilityClick(
                    mJobMediaList!![absoluteAdapterPosition],
                    0
                )
            }

        }
    }
}