package com.max360group.cammax360.views.fragments

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Access
import com.max360group.cammax360.repository.models.AccountList
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobsViewModel
import com.max360group.cammax360.views.adapters.*
import kotlinx.android.synthetic.main.layout_owner_access.*
import kotlinx.android.synthetic.main.layout_owner_general_info.*

class AccessDetailFragment : BaseFragment() {
    private val mJobsViewModel by lazy {
        ViewModelProvider(this).get(JobsViewModel::class.java)
    }

    private val mAccessListAdapter by lazy {
        DetailAccessListAdapter(this)
    }

    private var mAccess = Access()
    private var mAccountList = ArrayList<AccountList>()

    override val layoutId: Int
        get() = R.layout.layout_fragment_access_detail

    override fun init(savedInstanceState: Bundle?) {
        //Set adapter
        rvAccess.adapter = mAccessListAdapter
    }

    fun getDetailInfo(access: Access) {
        mAccess = access
        //Get users
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mJobsViewModel.getModuleUsers("jobs", false)
        } else {
            mJobsViewModel.getAllMembersFromDatabase()
        } }

    override val viewModel: BaseViewModel?
        get() = mJobsViewModel

    override fun observeProperties() {
        mJobsViewModel.onGetModuleUsers().observe(this, Observer {
            mAccountList.clear()
            if (mAccess.all) {
                mAccessListAdapter.updateData(it)
            } else {
                for (i in it.indices) {
                    if (mAccess.users!!.contains(it[i].id)) {
                        mAccountList.add(it[i])
                    }
                }
                mAccessListAdapter.updateData(mAccountList)
            }

        })
    }


}