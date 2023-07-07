package com.max360group.cammax360.views.adapters

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.AccountList
import com.max360group.cammax360.repository.models.Job
import com.max360group.cammax360.repository.models.UserX
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.JobsListener
import kotlinx.android.synthetic.main.layout_access_list.view.*
import kotlinx.android.synthetic.main.layout_detail_access_list.view.*
import kotlinx.android.synthetic.main.load_jobs_layout.view.*
import kotlinx.android.synthetic.main.row_load_more.view.*

open class DetailAccessListAdapter(var mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        val mList = ArrayList<AccountList>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder(parent.inflate(R.layout.layout_detail_access_list))

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(list: List<AccountList>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        (holder as ListViewHolder).bindListView(i)
    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(i: Int) {
            itemView.sdvImage.setImageURI(mList[absoluteAdapterPosition].profilePic)
            (mList[absoluteAdapterPosition].firstName+" "+ mList[absoluteAdapterPosition].lastName).also { itemView.tvName.text = it }

        }
    }
}