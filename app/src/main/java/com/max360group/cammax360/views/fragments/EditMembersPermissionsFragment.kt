package com.max360group.cammax360.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Jobs
import com.max360group.cammax360.services.CheckInternetService
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.EditJobsDetailViewModel
import com.max360group.cammax360.viewmodels.JobsPermissionsViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.MemberPermissionAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.dialgofragments.CreateRoleDialogFragment
import com.max360group.cammax360.views.fragments.EditMembersFragment.Companion.INTENT_EDIT_MEMBER
import com.max360group.cammax360.views.interfaces.EditMemberPermissionInterface
import kotlinx.android.synthetic.main.fragment_add_members.rvListing
import kotlinx.android.synthetic.main.fragment_edit_members_permission.*
import kotlinx.android.synthetic.main.toolbar.*
import java.lang.Exception

class EditMembersPermissionsFragment : BaseFragment(), View.OnClickListener,
    EditMemberPermissionInterface {

    companion object {
        const val BUNDLE_JOBS_PERMISSIONS = "permissions"
        const val BUNDLE_JOB_ID = "jobId"
        const val BUNDLE_USER_ID = "userId"
        const val BUNDLE_LOCAL_ID = "localId"
        const val BUNDLE_EDIT = 0
        const val BUNDLE_ADD = 1
        const val BUNDLE_FROM = "from"
        const val BUNDLE_USER_NAME = "userName"
        const val INTENT_EDIT_PERMISSION = "editPermission"
        const val INTENT_EDIT_DATA = "permissionData"

        fun newInstance(
            jobs: Jobs, userName: String, from: Int,
            jobId: String = "", userid: String = "",mLocalId:String=""
        ): EditMembersPermissionsFragment {
            val mFragment = EditMembersPermissionsFragment()
            val mBundle = Bundle()
            mBundle.putParcelable(BUNDLE_JOBS_PERMISSIONS, jobs)
            mBundle.putString(BUNDLE_USER_NAME, userName)
            mBundle.putString(BUNDLE_JOB_ID, jobId)
            mBundle.putString(BUNDLE_USER_ID, userid)
            mBundle.putString(BUNDLE_LOCAL_ID, mLocalId)
            mBundle.putInt(BUNDLE_FROM, from)
            mFragment.arguments = mBundle
            return mFragment
        }
    }

    private var mJobs: Jobs? = null
    private var mJobId=""
    private var mUserId=""
    private var mLocalId=""
    private var mFrom = 0

    private val mJobsPermissionsViewModel by lazy {
        ViewModelProvider(this).get(JobsPermissionsViewModel::class.java)
    }

    private val mEditJobsDetailViewModel by lazy {
        ViewModelProvider(this).get(EditJobsDetailViewModel::class.java)
    }

    private val mMemberPermissionAdapter by lazy {
        MemberPermissionAdapter(this)
    }

    override val layoutId: Int
        get() = R.layout.fragment_edit_members_permission

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
        arguments?.let {
            mJobs = it.getParcelable(BUNDLE_JOBS_PERMISSIONS)
            tvUserName.text = it.getString(BUNDLE_USER_NAME)
            mFrom = it.getInt(BUNDLE_FROM)
            mJobId = it.getString(BUNDLE_JOB_ID).toString()
            mUserId = it.getString(BUNDLE_USER_ID).toString()
            mLocalId = it.getString(BUNDLE_LOCAL_ID).toString()
        }

        //Set adapter
        rvListing.adapter = mMemberPermissionAdapter

        //Get data
        mJobsPermissionsViewModel.getMenu(mJobs)

        //Set click listener
        btnSaveChanges.setOnClickListener(this)
        btnSaveAsNewRole.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)
    }

    override val viewModel: BaseViewModel?
        get() = mEditJobsDetailViewModel

    override fun observeProperties() {
        mJobsPermissionsViewModel.onGetMenu().observe(this, Observer {
            mMemberPermissionAdapter.updateData(it)
        })

        mEditJobsDetailViewModel.onUpdateJob().observe(this, Observer {
            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(INTENT_EDIT_MEMBER)
                )
            (requireContext() as BaseAppCompactActivity).onBackPressed()
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSaveChanges -> {
                if (mFrom == BUNDLE_ADD) {
                    (requireContext() as BaseAppCompactActivity).onBackPressed()
                } else {
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mEditJobsDetailViewModel.editPermissions(
                            mJobId,
                            mUserId,
                            mMemberPermissionAdapter.getJobsPermission()!!
                        )
                    }else{
                        mEditJobsDetailViewModel.editMemberPermissionInDataBase(
                            mLocalId,mUserId,mMemberPermissionAdapter.getJobsPermission()
                        )
                    }
                }

            }

            R.id.btnSaveAsNewRole -> {
                CreateRoleDialogFragment.newInstance(mMemberPermissionAdapter.getJobsPermission()!!)
                    .show(
                        childFragmentManager, ""
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

    override fun onUpdateData(position: Int) {
        mMemberPermissionAdapter.notifyDataSetChanged()
    }
}