package com.max360group.cammax360.views.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.JobDetail
import com.max360group.cammax360.repository.models.Jobs
import com.max360group.cammax360.repository.models.UserX
import com.max360group.cammax360.services.CheckInternetService
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.EditJobsDetailViewModel
import com.max360group.cammax360.viewmodels.JobDetailViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.EditMemberAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.dialgofragments.AlertDialogFragment
import com.max360group.cammax360.views.fragments.EditMembersPermissionsFragment.Companion.BUNDLE_EDIT
import com.max360group.cammax360.views.fragments.InviteMembersPermissionsFragment.Companion.BUNDLE_UPDATE
import com.max360group.cammax360.views.interfaces.EditMemberListener
import kotlinx.android.synthetic.main.fragment_add_members.*
import kotlinx.android.synthetic.main.fragment_edit_members.etSearch
import kotlinx.android.synthetic.main.fragment_edit_members.rvListing
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList


class EditMembersFragment : BaseFragment(), View.OnClickListener, EditMemberListener {

    companion object {
        const val BUNDLE_JOB_DATA = "jobData"
        const val INTENT_EDIT_MEMBER = "editMember"

        fun newInstance(
            mJobDetail: JobDetail?,
        ): EditMembersFragment {
            val mFragment = EditMembersFragment()
            val mBundle = Bundle()
            mBundle.putParcelable(BUNDLE_JOB_DATA, mJobDetail!!)
            mFragment.arguments = mBundle
            return mFragment
        }
    }

    private val mJobDetailViewModel by lazy {
        ViewModelProvider(this).get(JobDetailViewModel::class.java)
    }

    private val mEditJobsDetailViewModel by lazy {
        ViewModelProvider(this).get(EditJobsDetailViewModel::class.java)
    }

    private val mEditMembersAdapter by lazy {
        EditMemberAdapter(this)
    }

    var mList = ArrayList<UserX>()
    private var mJobDetail: JobDetail? = null
    private var mUserId = ""

    override val layoutId: Int
        get() = R.layout.fragment_edit_members

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

        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mUpdateDialogReceiver,
                IntentFilter(INTENT_EDIT_MEMBER)
            )

        // Register receiver for sync data
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mUpdateLocalDataInServer,
                IntentFilter(CheckInternetService.INTENT_SYNC_DATA)
            )

        //Get arguments
        mJobDetail = arguments?.get(BUNDLE_JOB_DATA) as JobDetail?

        //Set adapter
        rvListing.adapter = mEditMembersAdapter

        //Call api
        if (GeneralFunctions.isInternetConnected(requireContext())){
            mJobDetailViewModel.getJobDetail(mJobDetail!!.job.id)
        }else{
            mJobDetailViewModel.getJobDetailFromLocal(mJobDetail!!.job.jobLocalId.toString(),mJobDetail!!.job.id)
        }

        //initSet data
        mEditMembersAdapter.updateData(mJobDetail!!.job.users!!)

        //Set onClick listener
        btnAddMembers.setOnClickListener(this)
        btnInviteMembers.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

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
        get() = mEditJobsDetailViewModel

    override fun observeProperties() {
        mJobDetailViewModel.onGetJobDetail().observe(this, androidx.lifecycle.Observer {
            mList.clear()
            mList.addAll(it.job.users!!)
            mJobDetail = it
            mEditMembersAdapter.updateData(it.job.users!!)
        })

        mEditJobsDetailViewModel.onUpdateJob().observe(this, androidx.lifecycle.Observer {
            //Call api
            if (GeneralFunctions.isInternetConnected(requireContext())){
                mJobDetailViewModel.getJobDetail(mJobDetail!!.job.id)
            }else{
                mJobDetailViewModel.getJobDetailFromLocal(mJobDetail!!.job.jobLocalId.toString(),mJobDetail!!.job.id)
            }
        })


    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnAddMembers -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = AddNewMembersInJobFragment.newInstance(mJobDetail),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }

            R.id.btnInviteMembers -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = InviteMembersPermissionsFragment.newInstance(
                        from = BUNDLE_UPDATE,
                        jobId = mJobDetail!!.job.id
                    ),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
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

    override fun onDelete(userId: String) {
        mUserId = userId
        var mAlertDialogFragment = AlertDialogFragment.newInstance(
            getString(R.string.st_delete_member_message),
            getString(R.string.delete_confirmation)
        )
        mAlertDialogFragment.setTargetFragment(this, 1234)
        mAlertDialogFragment.show(
            parentFragmentManager,
            getString(R.string.dialog)
        )
    }

    override fun onEdit(jobs: Jobs, name: String, userId: String) {
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = EditMembersPermissionsFragment.newInstance(
                jobs,
                name,
                BUNDLE_EDIT,
                mJobDetail!!.job.id,
                userId,
                mJobDetail!!.job.jobLocalId.toString()
            ),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
    }

    fun filter(text: Editable) {
        val filterList = ArrayList<UserX>()
        for (d in mList) {
            if (d.userId.firstName.lowercase().contains(text)) {
                filterList.add(d)
            }
        }
        //update recyclerview
        mEditMembersAdapter.updateData(filterList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Activity.RESULT_OK == resultCode && 1234 == requestCode) {
            when (intent?.getIntExtra(AlertDialogFragment.INTENT_SUBMIT, 0)) {
                AlertDialogFragment.VALUE_TRUE -> {
                    if (GeneralFunctions.isInternetConnected(requireContext())){
                        mEditJobsDetailViewModel.deleteJobMember(mJobDetail!!.job.id, mUserId)
                    }else{
                        mEditJobsDetailViewModel.deleteMembersInDatabase(mJobDetail!!.job.jobLocalId.toString(), mUserId)
                    }

                }
            }
        }
    }

    private val mUpdateDialogReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                //Call api
                //Call api
                if (GeneralFunctions.isInternetConnected(requireContext())){
                    mJobDetailViewModel.getJobDetail(mJobDetail!!.job.id)
                }else{
                    mJobDetailViewModel.getJobDetailFromLocal(mJobDetail!!.job.jobLocalId.toString(),
                        mJobDetail!!.job.id.toString())
                }
            } catch (e: Exception) {

            }
        }
    }

    private val mUpdateLocalDataInServer = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                //Call api
                try {

                } catch (e: java.lang.Exception) {
                }
            }
        }
    }

}