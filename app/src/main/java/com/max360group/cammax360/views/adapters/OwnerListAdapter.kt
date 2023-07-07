package com.max360group.cammax360.views.adapters

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Job
import com.max360group.cammax360.repository.models.Owners
import com.max360group.cammax360.repository.models.UserOwner
import com.max360group.cammax360.repository.models.UserX
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.JobsListener
import kotlinx.android.synthetic.main.layout_owner_list.view.*
import kotlinx.android.synthetic.main.layout_property_list.view.*
import kotlinx.android.synthetic.main.load_jobs_layout.view.*
import kotlinx.android.synthetic.main.load_jobs_layout.view.ivSdvImage
import kotlinx.android.synthetic.main.layout_owner_list.view.ivDelete
import kotlinx.android.synthetic.main.row_load_more.view.*

open class OwnerListAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList= mutableListOf<UserOwner>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.layout_owner_list))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(list:List<UserOwner>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }


    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.ivSdvImage.setImageURI(mList[absoluteAdapterPosition].picURL)
            itemView.tvOwnerName.text=mList[absoluteAdapterPosition].firstName
            itemView.tvEmail.text=mList[absoluteAdapterPosition].email

            itemView.ivDelete.setOnClickListener {
                (mFragment as OwnersListListener).onDeleteClick(absoluteAdapterPosition)
            }
        }
    }

    interface OwnersListListener{
        fun onDeleteClick(position:Int)
    }

}