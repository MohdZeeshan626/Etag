package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.ConversationList
import com.max360group.cammax360.repository.models.DashBoardMenuModel
import com.max360group.cammax360.repository.models.UserX
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.utils.JobsConstants
import kotlinx.android.synthetic.main.contributor_list_layout.view.*
import kotlinx.android.synthetic.main.dialog_fragment_camera.view.*
import kotlinx.android.synthetic.main.load_conversation_layout.view.*
import kotlinx.android.synthetic.main.load_conversation_layout.view.tvBio
import kotlinx.android.synthetic.main.load_conversation_layout.view.tvDate
import kotlinx.android.synthetic.main.load_data_layout.view.*
import kotlinx.android.synthetic.main.load_data_layout.view.tvName
import kotlinx.android.synthetic.main.load_notes_layout.view.*
import kotlinx.android.synthetic.main.load_organisation_layout.view.*


class ContributorsListAdapter(var mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList= mutableListOf<UserX>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.contributor_list_layout))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateFunction(list:List<UserX>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            (mList[adapterPosition].userId.firstName+" "+mList[adapterPosition].userId.lastName).also { itemView.tvName.text = it }
            itemView.tvEmail.text=mList[adapterPosition].userId.email
            itemView.tvCount.text=mList[adapterPosition].details.contributionCount.toString()
            itemView.sdvImageUser.setImageURI(mList[adapterPosition].primaryUserId)

            if (adapterPosition==mList.size-1){
                itemView.view.visibility=View.GONE
            }
            itemView.tvEmail.setOnClickListener {
                GeneralFunctions.email(mList[adapterPosition].userId.email,mFragment.requireContext())
            }
        }
    }
}