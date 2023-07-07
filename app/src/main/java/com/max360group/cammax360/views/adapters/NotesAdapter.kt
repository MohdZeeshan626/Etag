package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.ConversationList
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.JobsLDetailListener
import com.max360group.cammax360.views.utils.JobsConstants
import kotlinx.android.synthetic.main.load_conversation_layout.view.tvBio
import kotlinx.android.synthetic.main.load_conversation_layout.view.tvDate
import kotlinx.android.synthetic.main.load_data_layout.view.tvName
import kotlinx.android.synthetic.main.load_notes_layout.view.*


class NotesAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList= mutableListOf<ConversationList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_notes_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateFunction(list:List<ConversationList>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            (mList[absoluteAdapterPosition].creatorId.firstName+mList[absoluteAdapterPosition].creatorId.lastName).also { itemView.tvName.text = it }
            itemView.tvBio.text=mList[absoluteAdapterPosition].message
            itemView.tvDate.text= GeneralFunctions.changeDateFormat(mList[absoluteAdapterPosition].createdAt,
                Constants.DATE_FORMAT_SERVER_ISO, Constants.DATE_FORMAT_DISPLAY1
            )

            itemView.sdvUserImage.setImageURI(mList[absoluteAdapterPosition].creatorId.picURL)

            when (mList[absoluteAdapterPosition].mediaId.kind) {
                JobsConstants.JOB_KIND_PHOTO -> {
                    itemView.ivProductImage.setImageURI(mList[absoluteAdapterPosition].mediaId.medias!![0].mediaURL)
                    itemView.ivPlay.visibility=View.GONE
                }

                JobsConstants.JOB_KIND_VIDEO -> {
                    itemView.ivProductImage.setImageURI(mList[absoluteAdapterPosition].mediaId.medias!![0].thumbnailURL)
                    itemView.ivPlay.visibility=View.VISIBLE
                }

                else -> {
                    itemView.ivProductImage.setImageResource(R.drawable.ic_document)
                    itemView.ivPlay.visibility=View.GONE
                }
            }

            itemView.ivProductImage.setOnClickListener {
                (mFragment as JobsLDetailListener).onCommentsMediaClick(mList[absoluteAdapterPosition])
            }
        }
    }
}