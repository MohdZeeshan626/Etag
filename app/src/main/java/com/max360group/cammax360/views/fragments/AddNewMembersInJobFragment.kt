package com.max360group.cammax360.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.AccountList
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobsViewModel
import com.max360group.cammax360.views.adapters.AddMembersAdapter
import kotlinx.android.synthetic.main.fragment_add_members.*
import kotlinx.android.synthetic.main.toolbar.*
import android.text.Editable

import android.text.TextWatcher
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.max360group.cammax360.repository.models.JobDetail
import com.max360group.cammax360.repository.models.UserX
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.EditJobsDetailViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.calender.MonthlyActivity
import java.util.concurrent.CopyOnWriteArrayList


class AddNewMembersInJobFragment : BaseFragment(),
    View.OnClickListener {

    companion object {
        const val BUNDLE_JOB_DETAIL = "jobDetail"

        fun newInstance(job: JobDetail?): AddNewMembersInJobFragment {
            var mFragment = AddNewMembersInJobFragment()
            var mBundle = Bundle()
            mBundle.putParcelable(BUNDLE_JOB_DETAIL, job)
            mFragment.arguments = mBundle
            return mFragment
        }
    }

    var mList = CopyOnWriteArrayList<AccountList>()
    private var mJobDetail:JobDetail?=null

    private val mAddMembersAdapter by lazy {
        AddMembersAdapter(this)
    }

    private val mJobsViewModel by lazy {
        ViewModelProvider(this).get(JobsViewModel::class.java)
    }

    private val mEditJobMember by lazy {
        ViewModelProvider(this).get(EditJobsDetailViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_add_members

    override fun init(savedInstanceState: Bundle?) {
        //Set toolbar
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)
        ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)
        Glide
            .with(requireContext())
            .load(ApplicationGlobal.organisationLogo)
            .placeholder(R.drawable.ic_mimi_logo)
            .into(ivToolbarLeftIcon)

        //Get arguments
        mJobDetail=arguments?.getParcelable(BUNDLE_JOB_DETAIL)
        btnInviteMembers.visibility=View.GONE
        tvSkip.visibility=View.GONE

        //Set adapter
        rvListing.adapter = mAddMembersAdapter

        //Set on click listener
        btnInviteMembers.setOnClickListener(this)
        btnAddMembers.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

        if (GeneralFunctions.isInternetConnected(requireContext())){
            mJobsViewModel.getModuleUsers("jobs")
        }else{
            mJobsViewModel.getAllMembersFromDatabase()
        }


        //Search text listener
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                // filter your list from your input
                filter(s)

            }
        })

    }

    override val viewModel: BaseViewModel?
        get() = mJobsViewModel

    override fun observeProperties() {
        mJobsViewModel.onGetModuleUsers().observe(this, Observer {
            mList.clear()
            mList.addAll(it)
            //Remove the already added members
            for (data in mList) {
                for (i in mJobDetail!!.job.users!!.indices){
                    if (data.id==mJobDetail!!.job.users!![i].userId.id){
                        mList.remove(data)
                    }
                }
            }
            mAddMembersAdapter.updateData(mList,true)
        })

        mEditJobMember.onUpdateJob().observe(this, Observer {
            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(EditMembersFragment.INTENT_EDIT_MEMBER)
                )
            (requireContext() as BaseAppCompactActivity).onBackPressed()
        })
    }



    fun filter(text: Editable) {
        val filterList: MutableList<AccountList> = ArrayList()
        for (d in mList) {
            if (d.firstName.toLowerCase().contains(text)) {
                filterList.add(d)
            }
        }
        //update recyclerview
        mAddMembersAdapter.updateData(filterList)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnAddMembers -> {
                if (GeneralFunctions.isInternetConnected(requireContext())){
                    mEditJobMember.addJobMember(mJobDetail!!.job.id,
                        mAddMembersAdapter.getSelectedMembers())
                }else{
                    mEditJobMember.addMemberInDataBase(mJobDetail!!.job.jobLocalId.toString(),
                        mAddMembersAdapter.getSelectedMembers())
                }

            }
            R.id.ivToolbarUserImage -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = AccountsFragment.newInstance(AccountsFragment.BUNDLE_NORMAl_VIEWS),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
            R.id.ivToolbarRightIcon -> {
                startActivity(Intent(requireContext(), MonthlyActivity::class.java))
            }
            R.id.ivToolbarRightIconBell -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = NotificationFragment(),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
        }
    }


}