package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.AccountList
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.CreateNewJobListener
import kotlinx.android.synthetic.main.item_new_header.view.tvName
import kotlinx.android.synthetic.main.item_new_jobs.view.*

class NewJobsAdapter(var mFragment: androidx.fragment.app.Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private var EXTRA_LIST_ITEM = 1
        private var EXTRA_HEADER = 2
        const val LIMIT = 10
    }

    private var mList= mutableListOf<AccountList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == EXTRA_LIST_ITEM) {
            ListViewHolder(parent.inflate(R.layout.item_new_jobs))
        } else {
            ListLoaMore(parent.inflate(R.layout.item_new_header))
        }

    }

    override fun getItemCount(): Int {
        return mList.size+1
    }

    fun updateData(list:List<AccountList>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun getUsersList():List<AccountList>{
        return mList
    }


    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> EXTRA_HEADER
            else -> EXTRA_LIST_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListLoaMore -> {
                holder.bindListAddView(position)
            }

            is ListViewHolder -> {
                holder.bindListView(position-1)
            }
        }

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(position: Int) {
            (mList[position].firstName+" "+mList[position].lastName).also { itemView.tvName.text = it }
            itemView.tvEmail.text=mList[position].email

            itemView.ivEdit.setOnClickListener {
                (mFragment as CreateNewJobListener).onEditPermissions(mList[position].accounts!![0].permissions.jobs!!,
                mList[position].firstName+" "+mList[position].lastName,adapterPosition)
            }

            itemView.ivDelete.setOnClickListener {
                mList.removeAt(position)
                ApplicationGlobal.mJobUsersList.removeAt(position)
                notifyDataSetChanged()
            }
        }
    }

    inner class ListLoaMore(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListAddView(position: Int) {

        }
    }

}
