package com.max360group.cammax360.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.AccountList
import com.max360group.cammax360.repository.models.Jobs
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.inflate
import kotlinx.android.synthetic.main.item_add_member_header.view.*
import kotlinx.android.synthetic.main.item_add_members_jobs.view.*
import kotlinx.android.synthetic.main.item_add_members_jobs.view.ivEdit
import kotlinx.android.synthetic.main.item_add_members_jobs.view.tvName
import kotlinx.android.synthetic.main.item_add_members_jobs.view.view2

class AddMembersAdapter(var mFragment: androidx.fragment.app.Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private var EXTRA_LIST_ITEM = 1
        private var EXTRA_HEADER = 2
        const val LIMIT = 10
    }

    private var isUpdateMember = false

    protected val mUserPrefsManager: UserPrefsManager by lazy { UserPrefsManager(mFragment.requireContext()) }
    private var mList = mutableListOf<AccountList>()
    private var mSelectedList = ArrayList<AccountList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == EXTRA_LIST_ITEM) {
            ListViewHolder(parent.inflate(R.layout.item_add_members_jobs))
        } else {
            ListHeaderView(parent.inflate(R.layout.item_add_member_header))
        }

    }

    override fun getItemCount(): Int {
        return mList.size + 1
    }

    fun updateData(list: List<AccountList>, isUpdate: Boolean = false) {
        mList.clear()
        mList.addAll(list)
        isUpdateMember = isUpdate
        notifyDataSetChanged()
    }

    fun getSelectedMembers(): ArrayList<AccountList> {
        mSelectedList.clear()
        for (i in mList.indices) {
            if (mList[i].isChecked) {
                mSelectedList.add(mList[i])
            }
        }
        return mSelectedList
    }


    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> EXTRA_HEADER
            else -> EXTRA_LIST_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, possition: Int) {
        when (holder) {
            is ListHeaderView -> {
                holder.bindListAddView(possition)
            }

            is ListViewHolder -> {
                holder.bindListView(possition - 1)
            }
        }

    }

    private inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListView(possition: Int) {
            itemView.cbTermsPrivacy.setOnClickListener {
                if (mList[possition].isChecked) {
                    itemView.cbTermsPrivacy.isChecked = false
                    mList[possition].isChecked = false

                } else {
                    itemView.cbTermsPrivacy.isChecked = true
                    mList[possition].isChecked = true
                }
            }

            if (isUpdateMember) {
                //Update job member
                itemView.ivEdit.visibility = View.GONE
                itemView.view2.visibility = View.GONE
            } else {
                //Create job member
                itemView.ivEdit.visibility = View.VISIBLE
                itemView.view2.visibility = View.VISIBLE
                itemView.ivEdit.setOnClickListener {
                    (mFragment as AddMembersListener).onPermission(
                        mList[possition].accounts!![0].permissions.jobs!!,
                        possition, mList[possition].firstName + " " + mList[possition].lastName
                    )
                }
            }

            (mList[possition].firstName + mList[possition].lastName).also {
                itemView.tvName.text = it
            }
            itemView.tvEmail.text = mList[possition].email

            //Set click listener
            itemView.tvEmail.setOnClickListener {
                GeneralFunctions.email(mList[possition].email,mFragment.requireContext())
            }

        }
    }

    inner class ListHeaderView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindListAddView(position: Int) {
            if (isUpdateMember) {
                //Update job member
                itemView.tvPermission.visibility = View.GONE
                itemView.view2.visibility = View.GONE
            } else {
                //Create job member
                itemView.tvPermission.visibility = View.VISIBLE
                itemView.view2.visibility = View.VISIBLE

            }
        }
    }

    interface AddMembersListener {
        fun onPermission(jobs: Jobs, possition: Int, s: String)
    }

}
