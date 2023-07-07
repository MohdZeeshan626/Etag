package com.max360group.cammax360.views.adapters

import android.text.SpannableString
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Record
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.utils.CustomTypefaceSpan
import kotlinx.android.synthetic.main.load_notification_layout.view.*
import kotlinx.android.synthetic.main.row_load_more.view.*


class NotificationsAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList = mutableListOf<Record>()
    private var isLoadMore = false

    companion object {
        private const val VIEW_ITEM = 1
        private const val VIEW_MORE = 2
        const val LIMIT = 10
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_ITEM) {
            ListViewHolder(parent.inflate(R.layout.load_notification_layout))
        } else {
            MoreViewHolder(parent.inflate(R.layout.row_load_more))
        }
    }

    fun updateData(list: List<Record>, skip: Int = 0) {
        if (skip == 0) {
            mList.clear()
        }
        mList.addAll(list)
        isLoadMore = list.size >= LIMIT
        notifyDataSetChanged()


    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            mList.size -> VIEW_MORE
            else -> VIEW_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        if (VIEW_MORE == getItemViewType(i)) {
            (holder as MoreViewHolder).bindListView(i)
        } else {
            (holder as ListViewHolder).bindListView(i)
        }
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            //Set date
            itemView.tvNotificationTime.text =
                GeneralFunctions.covertTimeToText(GeneralFunctions.changeUtcToLocal(mList[absoluteAdapterPosition].createdAt))
            itemView.sdvNotificationImage.setImageURI(mList[absoluteAdapterPosition].creatorId.picURL)

            when (mList[absoluteAdapterPosition].kind) {
                "JobInvite" -> {
                    if (mList[absoluteAdapterPosition].jobId.users.isNotEmpty()) {
                        if (mList[absoluteAdapterPosition].jobId.users[0].invite.status == "Pending") {
                            itemView.btnReject.visibility = View.VISIBLE
                            itemView.btnAccept.visibility = View.VISIBLE

                            itemView.btnAccept.setOnClickListener {
                                (mFragment as NotificationListener).onInviteAccept(mList[absoluteAdapterPosition].jobId.users[0].invite.token)
                            }

                            itemView.btnReject.setOnClickListener {
                                (mFragment as NotificationListener).onInviteReject(mList[absoluteAdapterPosition].jobId.users[0].invite.token)
                            }

                        } else {
                            itemView.btnReject.visibility = View.GONE
                            itemView.btnAccept.visibility = View.GONE
                        }
                    } else {
                        itemView.btnReject.visibility = View.GONE
                        itemView.btnAccept.visibility = View.GONE
                    }

                    itemView.ivNotificationBadge.setImageResource(R.drawable.ic_notification_invite)

                    itemView.tvNotificationTitle.text = getSpannableString(
                        "${mList[absoluteAdapterPosition].creatorId.firstName} ${mList[absoluteAdapterPosition].creatorId.lastName}",
                        mFragment.requireContext().getString(R.string.st_invite_you_to_job),
                        mList[absoluteAdapterPosition].jobId.title
                    )
                }

                "JobCreate" -> {
                    if (mList[absoluteAdapterPosition].jobId.users.isNotEmpty()) {
                        if (mList[absoluteAdapterPosition].jobId.users[0].invite.status == "Pending") {
                            itemView.btnReject.visibility = View.VISIBLE
                            itemView.btnAccept.visibility = View.VISIBLE

                            itemView.btnAccept.setOnClickListener {
                                (mFragment as NotificationListener).onInviteAccept(mList[absoluteAdapterPosition].jobId.users[0].invite.token)
                            }

                            itemView.btnReject.setOnClickListener {
                                (mFragment as NotificationListener).onInviteReject(mList[absoluteAdapterPosition].jobId.users[0].invite.token)
                            }

                        } else {
                            itemView.btnReject.visibility = View.GONE
                            itemView.btnAccept.visibility = View.GONE
                        }
                    } else {
                        itemView.btnReject.visibility = View.GONE
                        itemView.btnAccept.visibility = View.GONE
                    }

                    itemView.ivNotificationBadge.setImageResource(R.drawable.ic_notification_invite)
                    itemView.tvNotificationTitle.text = getSpannableString(
                        "${mList[absoluteAdapterPosition].creatorId.firstName} ${mList[absoluteAdapterPosition].creatorId.lastName}",
                        mFragment.requireContext().getString(R.string.st_create_new_job_notification),
                        mList[absoluteAdapterPosition].jobId.title
                    )
                }

                "PhotoCreate" -> {
                    itemView.btnReject.visibility = View.GONE
                    itemView.btnAccept.visibility = View.GONE
                    itemView.ivNotificationBadge.setImageResource(R.drawable.ic_notification_video)
                    if (mList[absoluteAdapterPosition].mediaId.subKind == "simple") {

                        itemView.tvNotificationTitle.text = getSpannableString(
                            "${mList[absoluteAdapterPosition].creatorId.firstName} ${mList[absoluteAdapterPosition].creatorId.lastName}",
                            mFragment.requireContext().getString(R.string.st_added_photo),
                            mList[absoluteAdapterPosition].mediaId.name
                        )

                    } else if (mList[absoluteAdapterPosition].mediaId.kind == "dual") {
                        itemView.tvNotificationTitle.text = getSpannableString(
                            "${mList[absoluteAdapterPosition].creatorId.firstName} ${mList[absoluteAdapterPosition].creatorId.lastName}",
                            mFragment.requireContext()
                                .getString(R.string.st_added_dula_photo_notification),
                            mList[absoluteAdapterPosition].mediaId.name
                        )
                    }
                }

                "VideoCreate" -> {
                    itemView.btnReject.visibility = View.GONE
                    itemView.btnAccept.visibility = View.GONE
                    itemView.ivNotificationBadge.setImageResource(R.drawable.ic_notification_video)

                    itemView.tvNotificationTitle.text = getSpannableString(
                        "${mList[absoluteAdapterPosition].creatorId.firstName} ${mList[absoluteAdapterPosition].creatorId.lastName}",
                        mFragment.requireContext().getString(R.string.st_added_video_notification),
                        mList[absoluteAdapterPosition].mediaId.name
                    )
                }

                "DocCreate" -> {
                    itemView.btnReject.visibility = View.GONE
                    itemView.btnAccept.visibility = View.GONE
                    itemView.ivNotificationBadge.setImageResource(R.drawable.ic_notification_video)
                    itemView.tvNotificationTitle.text = getSpannableString(
                        "${mList[absoluteAdapterPosition].creatorId.firstName} ${mList[absoluteAdapterPosition].creatorId.lastName}",
                        mFragment.requireContext()
                            .getString(R.string.st_added_document_notification),
                        mList[absoluteAdapterPosition].mediaId.name
                    )
                }

                "CommentCreate" -> {
                    itemView.btnReject.visibility = View.GONE
                    itemView.btnAccept.visibility = View.GONE
                    itemView.ivNotificationBadge.setImageResource(R.drawable.ic_notification_comments)

                    itemView.tvNotificationTitle.text = getSpannableString(
                        "${mList[absoluteAdapterPosition].creatorId.firstName} ${mList[absoluteAdapterPosition].creatorId.lastName}",
                        mFragment.requireContext().getString(R.string.st_added_comment),
                        mList[absoluteAdapterPosition].conversationId.message
                    )
                }

                "NoteCreate" -> {
                    itemView.btnReject.visibility = View.GONE
                    itemView.btnAccept.visibility = View.GONE
                    itemView.ivNotificationBadge.setImageResource(R.drawable.ic_notification_comments)

                    itemView.tvNotificationTitle.text = getSpannableString(
                        "${mList[absoluteAdapterPosition].creatorId.firstName} ${mList[absoluteAdapterPosition].creatorId.lastName}",
                        mFragment.requireContext().getString(R.string.st_added_note),  mList[absoluteAdapterPosition].conversationId.message
                    )
                }

                "ConversationCreate" -> {
                    itemView.btnReject.visibility = View.GONE
                    itemView.btnAccept.visibility = View.GONE
                    itemView.ivNotificationBadge.setImageResource(R.drawable.ic_notification_message)

                    itemView.tvNotificationTitle.text = getSpannableString(
                        "${mList[absoluteAdapterPosition].creatorId.firstName} ${mList[absoluteAdapterPosition].creatorId.lastName}",
                        mFragment.requireContext().getString(R.string.st_added_conversation), mList[absoluteAdapterPosition].conversationId.message
                    )
                }

                "JobDetailEdit" -> {
                    itemView.btnReject.visibility = View.GONE
                    itemView.btnAccept.visibility = View.GONE
                    itemView.ivNotificationBadge.setImageResource(R.drawable.ic_notification_invite)
                    itemView.tvNotificationTitle.text = getSpannableString(
                        "${mList[absoluteAdapterPosition].creatorId.firstName} ${mList[absoluteAdapterPosition].creatorId.lastName}",
                        mFragment.requireContext()
                            .getString(R.string.st_edit_job_detail_notification), ""
                    )
                }
                else -> {
                }
            }
        }
    }

    private inner class MoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(position: Int) {
            if (GeneralFunctions.isInternetConnected(mFragment.requireContext())) {
                if (isLoadMore) {
                    itemView.llLoadMore.visibility = View.VISIBLE
                    (mFragment as NotificationListener).onLoadMore()
                } else {
                    itemView.llLoadMore.visibility = View.GONE
                }
            }
        }
    }

    interface NotificationListener {
        fun onLoadMore()
        fun onInviteAccept(token: String)
        fun onInviteReject(token: String)
    }

    fun getSpannableString(firstWord: String, secondWord: String, thirdWord: String): CharSequence {
        //Set spannable string
        val span1 = SpannableString(firstWord)
        span1.setSpan(
            CustomTypefaceSpan(mFragment.requireContext().resources.getFont(R.font.font_santral_bold)),
            0,
            firstWord.length,
            SPAN_INCLUSIVE_INCLUSIVE
        )

        val span2 = SpannableString(secondWord)
        span2.setSpan(
            CustomTypefaceSpan(mFragment.requireContext().resources.getFont(R.font.font_santral_reguler)),
            0,
            secondWord.length,
            SPAN_INCLUSIVE_INCLUSIVE
        )

        return if (thirdWord.isNotBlank()) {
            val span3 = SpannableString(thirdWord)
            span3.setSpan(
                CustomTypefaceSpan(mFragment.requireContext().resources.getFont(R.font.font_santral_bold)),
                0,
                thirdWord.length,
                SPAN_INCLUSIVE_INCLUSIVE
            )
            TextUtils.concat(span1, " ", span2, " ", span3)
        } else {
            TextUtils.concat(span1, " ", span2)
        }
    }

}