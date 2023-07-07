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
import com.max360group.cammax360.views.adapters.AccessListAdapter
import kotlinx.android.synthetic.main.layout_owner_access.*

class AccessFragment : BaseFragment() {

    private val mJobsViewModel by lazy {
        ViewModelProvider(this).get(JobsViewModel::class.java)
    }

    private val mAccessListAdapter by lazy {
        AccessListAdapter(this)
    }

    private var mAccess = Access()
    private var mAccessAll = ArrayList<AccountList>()
    private var isEdit=false

    override val layoutId: Int
        get() = R.layout.layout_owner_access

    override fun init(savedInstanceState: Bundle?) {
        //Get users
        if (GeneralFunctions.isInternetConnected(requireContext())){
            mJobsViewModel.getModuleUsers("jobs", showLoader = false)
        }else{
            mJobsViewModel.getAllMembersFromDatabase()
        }

        //Set adapter
        isEdit=false
        rvAccess.adapter = mAccessListAdapter
    }

    override val viewModel: BaseViewModel
        get() = mJobsViewModel

    override fun observeProperties() {
        mJobsViewModel.onGetModuleUsers().observe(this, Observer {
            mAccessAll.clear()
            mAccessAll.addAll(it)
            if (!isEdit){
                for (i in mAccessAll.indices){
                    mAccessAll[i].isChecked=true
                }
            }

            //Set editable data if comes for edit detail
            setEditableData()
        })
    }

    fun getEditableDetail(access: Access) {
        mAccess = access
        isEdit=true
        //Get users
        mJobsViewModel.getModuleUsers("jobs", showLoader = false)
    }

    private fun setEditableData() {
        if (mAccess.all) {
            for (i in mAccessAll.indices) {
                mAccessAll[i].isChecked = true

            }
        } else {
            for (i in mAccessAll.indices) {
                if (mAccess.users!!.contains(mAccessAll[i].id)) {
                    mAccessAll[i].isChecked = true
                }
            }
        }
        mAccessListAdapter.updateData(mAccessAll, mAccess.all)
    }

}