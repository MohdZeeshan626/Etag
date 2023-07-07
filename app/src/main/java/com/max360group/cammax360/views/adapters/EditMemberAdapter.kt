package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.UserX
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import com.max360group.cammax360.views.interfaces.EditMemberListener
import kotlinx.android.synthetic.main.item_new_header.view.tvName
import kotlinx.android.synthetic.main.item_new_jobs.view.*
import java.util.concurrent.CopyOnWriteArrayList

class EditMemberAdapter(var mFragment: androidx.fragment.app.Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private var EXTRA_LIST_ITEM = 1
        private var EXTRA_HEADER = 2
        const val LIMIT = 10
    }

    protected val mUserPrefsManager: UserPrefsManager by lazy { UserPrefsManager(mFragment.requireContext()) }
    private var mList= mutableListOf<UserX>()

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

    fun updateData(list: ArrayList<UserX>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
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
            (mList[position].userId.firstName+" "+mList[position].userId.lastName).also { itemView.tvName.text = it }
            itemView.tvEmail.text=mList[position].userId.email

            //Unable edit my self
            if (mList[position].userId.id==mUserPrefsManager.loginedUser!!.id){
                itemView.ivEdit.setImageResource(R.color.white)
                itemView.ivEdit.setPadding(0,36,0,36)
                itemView.ivDelete.setImageResource(R.color.white)
            }else{
                itemView.ivEdit.setImageResource(R.drawable.ic_edit_new)
                itemView.ivEdit.setPadding(0,0,0,0)
                itemView.ivDelete.setImageResource(R.drawable.ic_delete)
            }

            itemView.ivEdit.setOnClickListener {
                (mFragment as EditMemberListener).onEdit(mList[position].permissions,
                    mList[position].userId.firstName+" "+mList[position].userId.lastName,
                mList[position].userId.id)
            }

            itemView.ivDelete.setOnClickListener {
                (mFragment as EditMemberListener).onDelete(mList[position].userId.id)
            }

            itemView.tvEmail.setOnClickListener {
                GeneralFunctions.email(mList[position].userId.email,mFragment.requireContext())
            }

        }
    }

    inner class ListLoaMore(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListAddView(position: Int) {

        }
    }



}
