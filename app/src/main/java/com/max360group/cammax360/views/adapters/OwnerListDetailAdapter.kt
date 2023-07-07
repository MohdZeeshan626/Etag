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
import kotlinx.android.synthetic.main.layout_owner_list_detail.view.*
import kotlinx.android.synthetic.main.layout_property_list.view.*
import kotlinx.android.synthetic.main.load_jobs_layout.view.*
import kotlinx.android.synthetic.main.load_jobs_layout.view.tvJobName
import kotlinx.android.synthetic.main.row_load_more.view.*

open class OwnerListDetailAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mList= mutableListOf<UserOwner>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.layout_owner_list_detail))
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
            (mList[absoluteAdapterPosition].firstName+" "+mList[absoluteAdapterPosition].lastName).also { itemView.tvJobName.text = it }
            itemView.tvEmail.text=mList[absoluteAdapterPosition].email
            itemView.sdvUserImage.setImageURI(mList[absoluteAdapterPosition].picURL)

            itemView.tvEmail.setOnClickListener {
                GeneralFunctions.email(mList[absoluteAdapterPosition].email!!,mFragment.requireContext())
            }
        }
    }
}