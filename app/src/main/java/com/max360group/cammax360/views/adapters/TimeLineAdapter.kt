package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.TimeLine
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.JobsLDetailListener
import com.max360group.cammax360.views.utils.JobsConstants
import kotlinx.android.synthetic.main.load_timeline_layout.view.*


class TimeLineAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = mutableListOf<TimeLine>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_timeline_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(list: List<TimeLine>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            //Set date grouping
            setDate(itemView.viewDate, itemView.tvDate, absoluteAdapterPosition)

            when (mList[absoluteAdapterPosition].event) {
                "NoteCreate" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_left_note)).also { itemView.tvType.text = it }
                    itemView.tvMessage.text = mList[absoluteAdapterPosition].reqData.message
                    itemView.tvMessage.visibility = View.VISIBLE
                    itemView.sdvMedia.visibility = View.GONE
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.GONE
                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_notes)
                }

                "ConversationCreate" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_conversation_left)).also {
                        itemView.tvType.text = it
                    }
                    itemView.tvMessage.text = mList[absoluteAdapterPosition].reqData.message
                    itemView.tvMessage.visibility = View.VISIBLE
                    itemView.sdvMedia.visibility = View.GONE
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.GONE
                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_message)
                }

                "JobCreate" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_create_job)).also { itemView.tvType.text = it }
                    itemView.tvMessage.text =
                        mFragment.requireContext().getString(R.string.st_job_info_edited)
                    itemView.tvMessage.visibility = View.VISIBLE
                    itemView.sdvMedia.visibility = View.GONE
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.GONE
                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_notes)
                }

                "PhotoCreate" -> {
                    if (mList[absoluteAdapterPosition].media.subKind == "simple") {
                        (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                            .getString(R.string.st_added_simple_photo)).also {
                            itemView.tvType.text = it
                        }
                        itemView.tvMessage.visibility = View.GONE
                        itemView.sdvMedia.visibility = View.VISIBLE
                        itemView.sdvMedia1.visibility = View.GONE
                        itemView.sdvUserPhoto.visibility = View.GONE
                        itemView.sdvMedia.setImageURI(mList[absoluteAdapterPosition].media.medias!![0].mediaURL)
                        itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                            mList[absoluteAdapterPosition].createdAt,
                            Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                        )
                        itemView.ivIcon.setImageResource(R.drawable.ic_timeline_video)
                    } else if (mList[absoluteAdapterPosition].media.subKind == "dual") {
                        (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                            .getString(R.string.st_added_dual_photo)).also {
                            itemView.tvType.text = it
                        }
                        itemView.tvMessage.visibility = View.GONE
                        itemView.sdvUserPhoto.visibility = View.GONE
                        itemView.sdvMedia.visibility = View.VISIBLE
                        itemView.sdvMedia1.visibility = View.VISIBLE
                        itemView.sdvMedia.setImageURI(mList[absoluteAdapterPosition].media.medias!![0].mediaURL)
                        itemView.sdvMedia1.setImageURI(mList[absoluteAdapterPosition].media.medias!![1].mediaURL)
                        itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                            mList[absoluteAdapterPosition].createdAt,
                            Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                        )
                        itemView.ivIcon.setImageResource(R.drawable.ic_timeline_video)
                    }

                    //Click listener
                    itemView.clSdvView.setOnClickListener {
                        (mFragment as JobsLDetailListener).onTimelineClick(mList[absoluteAdapterPosition].media)
                    }

                }

                "VideoCreate" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_added_video)).also {
                        itemView.tvType.text = it
                    }
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvMedia.visibility = View.VISIBLE
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.GONE
                    itemView.sdvMedia.setImageURI(mList[absoluteAdapterPosition].media.medias!![0].thumbnailURL)
                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_video)

                    //Click listener
                    itemView.clSdvView.setOnClickListener {
                        (mFragment as JobsLDetailListener).onTimelineClick(mList[absoluteAdapterPosition].media)
                    }
                }

                "CommentCreate" -> {
                    when (mList[absoluteAdapterPosition].media.kind) {
                        JobsConstants.JOB_KIND_VIDEO -> {
                            itemView.sdvMedia.setImageURI(mList[absoluteAdapterPosition].media.medias!![0].thumbnailURL)
                        }
                        JobsConstants.JOB_KIND_PHOTO -> {
                            itemView.sdvMedia.setImageURI(mList[absoluteAdapterPosition].media.medias!![0].mediaURL)
                        }
                        else -> {
                            itemView.sdvMedia.setImageResource(R.drawable.ic_document)
                        }
                    }

                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_left_new_entry_in_comment)).also {
                        itemView.tvType.text = it
                    }

                    itemView.tvMessage.text = mList[absoluteAdapterPosition].reqData.message
                    itemView.sdvMedia.visibility = View.VISIBLE
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.GONE
                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_comment)

                    //Click listener
                    itemView.clSdvView.setOnClickListener {
                        (mFragment as JobsLDetailListener).onTimelineClick(mList[absoluteAdapterPosition].media)
                    }
                }

                "DocCreate" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_added_document)).also {
                        itemView.tvType.text = it
                    }
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvMedia.setImageResource(R.drawable.ic_document)
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.GONE
                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_document)

                    //Click listener
                    itemView.clSdvView.setOnClickListener {
                        (mFragment as JobsLDetailListener).onTimelineClick(mList[absoluteAdapterPosition].media)
                    } }

                "JobDetailEdit" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_edited_the_job_detail)).also {
                        itemView.tvType.text = it }
                    itemView.tvMessage.visibility = View.VISIBLE
                    itemView.sdvMedia.visibility = View.GONE
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY)
                    itemView.ivIcon.setImageResource(R.drawable.ic_time_line_edit)
                }

                "PhotoSubMediaInfoEdit" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_edited_the_photo_detail)).also {
                        itemView.tvType.text = it
                    }
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.GONE
                    itemView.sdvMedia.setImageURI(mList[absoluteAdapterPosition].media.medias!![0].mediaURL)

                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_video)

                    //Click listener
                    itemView.clSdvView.setOnClickListener {
                        (mFragment as JobsLDetailListener).onTimelineClick(mList[absoluteAdapterPosition].media)
                    }
                }

                "VideoSubMediaInfoEdit" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_edited_the_video_detail)).also {
                        itemView.tvType.text = it
                    }
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.GONE
                    itemView.sdvMedia.setImageURI(mList[absoluteAdapterPosition].media.medias!![0].thumbnailURL)

                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_video)

                    //Click listener
                    itemView.clSdvView.setOnClickListener {
                        (mFragment as JobsLDetailListener).onTimelineClick(mList[absoluteAdapterPosition].media)
                    }

                }
                "DocSubMediaInfoEdit" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_edited_the_document_detail)).also {
                        itemView.tvType.text = it
                    }
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.GONE
                    itemView.sdvMedia.setImageResource(R.drawable.ic_document)

                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_document)

                    //Click listener
                    itemView.clSdvView.setOnClickListener {
                        (mFragment as JobsLDetailListener).onTimelineClick(mList[absoluteAdapterPosition].media)
                    }
                }
                "JobMemberEdit" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_edited_permissions_of_member) + mList[absoluteAdapterPosition].member.firstName).also {
                        itemView.tvType.text = it
                    }
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.sdvMedia.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.VISIBLE
                    itemView.sdvUserPhoto.setImageURI(mList[absoluteAdapterPosition].member.picURL)

                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_time_line_edit)
                }

                "PhotoDelete" -> {
                    if (mList[absoluteAdapterPosition].media.subKind == "simple") {
                        (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                            .getString(R.string.st_deleted_photo)).also {
                            itemView.tvType.text = it
                        }
                        itemView.sdvMedia1.visibility = View.GONE
                        itemView.sdvMedia.visibility = View.VISIBLE
                        itemView.sdvMedia.setImageURI(mList[absoluteAdapterPosition].media.medias!![0].mediaURL)

                    }

                    else if (mList[absoluteAdapterPosition].media.subKind == "dual") {
                        (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                            .getString(R.string.st_deleted_dual_photo)).also {
                            itemView.tvType.text = it
                        }
                        itemView.sdvMedia1.visibility = View.VISIBLE
                        itemView.sdvMedia.visibility = View.VISIBLE
                        itemView.sdvMedia.setImageURI(mList[absoluteAdapterPosition].media.medias!![0].mediaURL)
                        itemView.sdvMedia1.setImageURI(mList[absoluteAdapterPosition].media.medias!![1].mediaURL)
                    }
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.GONE

                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_video)

                    //Click listener
                    itemView.clSdvView.setOnClickListener {
                        (mFragment as JobsLDetailListener).onTimelineClick(mList[absoluteAdapterPosition].media)
                    }
                }

                "VideoDelete" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_deleted_video)).also {
                        itemView.tvType.text = it
                    }
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.GONE
                    itemView.sdvMedia.visibility = View.VISIBLE
                    itemView.sdvMedia.setImageURI(mList[absoluteAdapterPosition].media.medias!![0].thumbnailURL)

                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_video)

                    //Click listener
                    itemView.clSdvView.setOnClickListener {
                        (mFragment as JobsLDetailListener).onTimelineClick(mList[absoluteAdapterPosition].media)
                    }
                }
                "DocDelete" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_deleted_document)).also {
                        itemView.tvType.text = it
                    }
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.GONE
                    itemView.sdvMedia.visibility = View.VISIBLE
                    itemView.sdvMedia.setImageResource(R.drawable.ic_document)

                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_document)

                    //Click listener
                    itemView.clSdvView.setOnClickListener {
                        (mFragment as JobsLDetailListener).onTimelineClick(mList[absoluteAdapterPosition].media)
                    }
                }

                "JobMemberAdd" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_added_member) + mList[absoluteAdapterPosition].member.firstName).also {
                        itemView.tvType.text = it
                    }
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.VISIBLE
                    itemView.sdvMedia.visibility = View.GONE
                    itemView.sdvUserPhoto.setImageURI(mList[absoluteAdapterPosition].member.picURL)

                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_time_line_add)
                }

                "JobMemberDelete" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_remove_member) + mList[absoluteAdapterPosition].member.firstName).also {
                        itemView.tvType.text = it
                    }

                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvMedia.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.VISIBLE
                    itemView.sdvUserPhoto.setImageURI(mList[absoluteAdapterPosition].member.picURL)


                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_delete)
                }

                "InviteAccepted" -> {
                    (mList[absoluteAdapterPosition].member.firstName + mFragment.requireContext()
                        .getString(R.string.st_accept_invitation)).also {
                        itemView.tvType.text = it
                    }
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.VISIBLE
                    itemView.sdvMedia.visibility = View.GONE
                    itemView.sdvUserPhoto.setImageURI(mList[absoluteAdapterPosition].member.picURL)

                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_permission)
                }
                "JobMemberInvite" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_invite) + mList[absoluteAdapterPosition].member.firstName).also {
                        itemView.tvType.text = it
                    }
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.VISIBLE
                    itemView.sdvMedia.visibility = View.GONE
                    itemView.sdvUserPhoto.setImageURI(mList[absoluteAdapterPosition].member.picURL)

                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_permission)
                }

                "PhotoSubMediaPermEdit" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_edit_photo_permission)).also {
                        itemView.tvType.text = it
                    }
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.GONE
                    itemView.sdvMedia.visibility = View.VISIBLE
                    itemView.sdvMedia.setImageURI(mList[absoluteAdapterPosition].media.medias!![0].mediaURL)

                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_permission)

                    //Click listener
                    itemView.clSdvView.setOnClickListener {
                        (mFragment as JobsLDetailListener).onTimelineClick(mList[absoluteAdapterPosition].media)
                    }
                }
                "VideoSubMediaPermEdit" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_edit_video_permission)).also {
                        itemView.tvType.text = it
                    }
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.GONE
                    itemView.sdvMedia.visibility = View.VISIBLE
                    itemView.sdvMedia.setImageURI(mList[absoluteAdapterPosition].media.medias!![0].thumbnailURL)

                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_permission)

                    //Click listener
                    itemView.clSdvView.setOnClickListener {
                        (mFragment as JobsLDetailListener).onTimelineClick(mList[absoluteAdapterPosition].media)
                    }
                }
                "DocSubMediaPermEdit" -> {
                    (mList[absoluteAdapterPosition].user.firstName + mFragment.requireContext()
                        .getString(R.string.st_edit_doc_permission)).also {
                        itemView.tvType.text = it
                    }
                    itemView.sdvMedia1.visibility = View.GONE
                    itemView.tvMessage.visibility = View.GONE
                    itemView.sdvUserPhoto.visibility = View.GONE
                    itemView.sdvMedia.visibility = View.VISIBLE
                    itemView.sdvMedia.setImageResource(R.drawable.ic_document)

                    itemView.tvTime.text = GeneralFunctions.changeUtcToLocal(
                        mList[absoluteAdapterPosition].createdAt,
                        Constants.DATE_FORMAT_SERVER_ISO, Constants.TIME_FORMAT_DISPLAY
                    )
                    itemView.ivIcon.setImageResource(R.drawable.ic_timeline_permission)

                    //Click listener
                    itemView.clSdvView.setOnClickListener {
                        (mFragment as JobsLDetailListener).onTimelineClick(mList[absoluteAdapterPosition].media)
                    }
                }
            }
        }
    }

    private fun setDate(view: View, tvDate: TextView, position: Int) {
        if (0 < position) {
            if (GeneralFunctions.changeDateFormat(
                    mList[position].createdAt,
                    Constants.DATE_FORMAT_SERVER_ISO,
                    Constants.DATE_FORMAT_DISPLAY
                )
                    .equals(
                        GeneralFunctions.changeDateFormat(
                            mList[position - 1].createdAt,
                            Constants.DATE_FORMAT_SERVER_ISO,
                            Constants.DATE_FORMAT_DISPLAY
                        ), true
                    )
            ) {
                tvDate.visibility = View.GONE
                view.visibility = View.GONE
            } else {
                tvDate.visibility = View.VISIBLE
                view.visibility = View.VISIBLE
                tvDate.text = GeneralFunctions.changeDateFormat(
                    mList[position].createdAt,
                    Constants.DATE_FORMAT_SERVER_ISO, Constants.DATE_FORMAT_DISPLAY
                )
            }
        } else {
            tvDate.visibility = View.VISIBLE
            view.visibility = View.VISIBLE
            tvDate.text = GeneralFunctions.changeDateFormat(
                mList[position].createdAt,
                Constants.DATE_FORMAT_SERVER_ISO, Constants.DATE_FORMAT_DISPLAY
            )
        }
    }

}