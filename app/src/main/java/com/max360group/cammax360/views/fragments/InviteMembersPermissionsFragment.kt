package com.max360group.cammax360.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.services.CheckInternetService
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.EditJobsDetailViewModel
import com.max360group.cammax360.viewmodels.JobsPermissionsViewModel
import com.max360group.cammax360.viewmodels.JobsViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.MemberPermissionAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.dialgofragments.CreateRoleDialogFragment
import com.max360group.cammax360.views.fragments.CreateNewJobFragment.Companion.BUNDLE_INVITE
import com.max360group.cammax360.views.interfaces.EditMemberPermissionInterface
import kotlinx.android.synthetic.main.fragment_add_members.rvListing
import kotlinx.android.synthetic.main.fragment_invite_member_permission.*
import kotlinx.android.synthetic.main.toolbar.*
import java.lang.Exception


class InviteMembersPermissionsFragment : BaseFragment(),
    View.OnClickListener, EditMemberPermissionInterface {

    private var ROLE_TYPE = "jobRole"

    companion object {
        const val BUNDLE_LAT = "latitude"
        const val BUNDLE_LONGITUDE = "longitude"
        const val BUNDLE_JOB_ID = "jobId"
        const val BUNDLE_ADD = 0
        const val BUNDLE_UPDATE = 1
        const val BUNDLE_FROM = "from"

        fun newInstance(
            latitude: Double = 0.0,
            longitude: Double = 0.0,
            from: Int = 0,
            jobId: String = ""
        ): InviteMembersPermissionsFragment {
            var mFragment = InviteMembersPermissionsFragment()
            var mBundle = Bundle()
            mBundle.putDouble(BUNDLE_LAT, latitude)
            mBundle.putDouble(BUNDLE_LONGITUDE, longitude)
            mBundle.putInt(BUNDLE_FROM, from)
            mBundle.putString(BUNDLE_JOB_ID, jobId)
            mFragment.arguments = mBundle
            return mFragment
        }
    }

    private val mJobsPermissionsViewModel by lazy {
        ViewModelProvider(this).get(JobsPermissionsViewModel::class.java)
    }

    private val mJobsViewModel by lazy {
        ViewModelProvider(this).get(JobsViewModel::class.java)
    }

    private val mEditJobsDetailViewModel by lazy {
        ViewModelProvider(this).get(EditJobsDetailViewModel::class.java)
    }

    private val mMemberPermissionAdapter by lazy {
        MemberPermissionAdapter(this)
    }

    private var mRolesList = ArrayList<RolesList>()
    private var mJobs: Jobs? = Jobs()
    private var mRolesName = mutableListOf<String>()
    private var mJobUsersList = ArrayList<AccountList>()
    private val accounts = ArrayList<AccountDetail>()
    private var mLatitude = 0.0
    private var mLongitude = 0.0
    private var mFrom = 0
    private var mJobId = ""

    override val layoutId: Int
        get() = R.layout.fragment_invite_member_permission

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

        // Register receiver for sync data
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mUpdateLocalDataInServer,
                IntentFilter(CheckInternetService.INTENT_SYNC_DATA)
            )

        //Get arguments
        arguments?.let { bundle ->
            mLatitude = bundle.getDouble(CreateNewJobFragment.BUNDLE_LAT)
            mLongitude = bundle.getDouble(CreateNewJobFragment.BUNDLE_LONGITUDE)
            mFrom = bundle.getInt(BUNDLE_FROM)
            mJobId = bundle.getString(BUNDLE_JOB_ID).toString()
        }

        //Set adapter
        rvListing.adapter = mMemberPermissionAdapter

        //Get data
        mJobsPermissionsViewModel.getMenu(mJobs)
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mJobsViewModel.getJobRole(ROLE_TYPE)
        } else {
            mJobsViewModel.getAllRolesFromLocal()
        }


        //Set click listener
        btnSaveAsNewRole.setOnClickListener(this)
        btnSaveChanges.setOnClickListener(this)
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

        mJobsViewModel.onGetRollsList().observe(this, Observer {
            mRolesList.clear()
            mRolesName.clear()
            mRolesList.addAll(it)

            mRolesName.add(getString(R.string.st_select_role))
            for (i in mRolesList.indices) {
                mRolesName.add(mRolesList[i].name)
            }

            //initialize spinner
            initSpinner(mRolesName)
        })

        mEditJobsDetailViewModel.onUpdateJob().observe(this, Observer {
            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(EditMembersFragment.INTENT_EDIT_MEMBER)
                )
            (requireContext() as BaseAppCompactActivity).onBackPressed()
        })

    }

    private fun initSpinner(mRolesName: MutableList<String>) {
        val dataAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, mRolesName)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnrRoles.adapter = dataAdapter
        spnrRoles.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (position != 0) {
                    mJobsPermissionsViewModel.getMenu(mRolesList[position - 1].permissions)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }
        }
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
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

            R.id.btnSaveChanges -> {
                when {
                    etMemberName.text.toString().trim().isBlank() -> showMessage(
                        null,
                        getString(R.string.et_empty_member_name)
                    )

                    etMemberEmail.text.toString().trim().isBlank() -> showMessage(
                        null,
                        getString(R.string.et_empty_member_email)
                    )

                    !GeneralFunctions.isValidEmail(
                        etMemberEmail.text.toString()
                            .trim()
                    ) -> showMessage(
                        null,
                        getString(R.string.invalid_email)
                    )

                    mUserPrefsManager.loginedUser!!.email == etMemberEmail.text.toString().trim() ->
                        showMessage(
                            null,
                            getString(R.string.invite_your_self)
                        )

                    else -> {
                        if (mFrom == BUNDLE_ADD) {
                            accounts.clear()
                            mJobUsersList.clear()
                            accounts.add(AccountDetail(permissions = Permissions(jobs = mMemberPermissionAdapter.getJobsPermission())))
                            mJobUsersList.add(
                                AccountList(
                                    firstName = etMemberName.text.toString()
                                        .trim(),
                                    email = etMemberEmail.text.toString().trim(),
                                    accounts = accounts
                                )
                            )

                            //Save the selected users list globally
                            ApplicationGlobal.mJobUsersList.addAll(mJobUsersList)

                            (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                                fragment = CreateNewJobFragment.newInstance(
                                    mLatitude,
                                    mLongitude, BUNDLE_INVITE
                                ),
                                containerViewId = R.id.flFragContainerMain,
                                enterAnimation = R.animator.slide_right_in,
                                popExitAnimation = R.animator.slide_right_out
                            )
                        } else {
                            mEditJobsDetailViewModel.inviteJobMember(
                                mJobId,
                                etMemberName.text.toString().trim(),
                                etMemberEmail.text.toString().trim(),
                                mMemberPermissionAdapter.getJobsPermission()!!
                            )
                        }
                    }
                }

            }
        }
    }

    private val mUpdateLocalDataInServer = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                //Call api
                try {
                    mJobsViewModel.uploadRolesLocalToServer()

                } catch (e: Exception) {
                }
            }
        }
    }


    override fun onUpdateData(position: Int) {
        mMemberPermissionAdapter.notifyDataSetChanged()
    }

}