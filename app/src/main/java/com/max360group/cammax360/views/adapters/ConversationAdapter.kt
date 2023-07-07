package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.ConversationList
import com.max360group.cammax360.repository.models.DashBoardMenuModel
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_DISPLAY1
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.dialog_fragment_camera.view.*
import kotlinx.android.synthetic.main.load_conversation_layout.view.*
import kotlinx.android.synthetic.main.load_conversation_layout.view.tvBio
import kotlinx.android.synthetic.main.load_conversation_layout.view.tvDate
import kotlinx.android.synthetic.main.load_data_layout.view.*
import kotlinx.android.synthetic.main.load_data_layout.view.tvName
import kotlinx.android.synthetic.main.load_notes_layout.view.*
import kotlinx.android.synthetic.main.load_organisation_layout.view.*


class ConversationAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList= mutableListOf<ConversationList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.load_conversation_layout))
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
            itemView.tvDate.text=GeneralFunctions.changeUtcToLocal(mList[absoluteAdapterPosition].createdAt)
            GeneralFunctions.changeUtcToLocal(mList[absoluteAdapterPosition].createdAt)
            itemView.sdvImage.setImageURI(mList[absoluteAdapterPosition].creatorId.picURL)
        }
    }
}