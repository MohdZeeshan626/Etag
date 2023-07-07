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
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.AccountList
import com.max360group.cammax360.repository.models.Jobs
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobsViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.AddMembersAdapter
import com.max360group.cammax360.views.fragments.EditMembersPermissionsFragment.Companion.INTENT_EDIT_DATA
import com.max360group.cammax360.views.fragments.EditMembersPermissionsFragment.Companion.INTENT_EDIT_PERMISSION
import kotlinx.android.synthetic.main.fragment_add_members.*
import kotlinx.android.synthetic.main.toolbar.*
import android.text.Editable

import android.text.TextWatcher
import com.bumptech.glide.Glide
import com.max360group.cammax360.repository.models.AccountDetail
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.fragments.CreateNewJobFragment.Companion.BUNDLE_ADD


class AddMembersFragment : BaseFragment(), AddMembersAdapter.AddMembersListener,
    View.OnClickListener {

    companion object {
        const val BUNDLE_LAT = "latitude"
        const val BUNDLE_LONGITUDE = "longitude"

        fun newInstance(latitude: Double, longitude: Double): AddMembersFragment {
            val mFragment = AddMembersFragment()
            val mBundle = Bundle()
            mBundle.putDouble(BUNDLE_LAT, latitude)
            mBundle.putDouble(BUNDLE_LONGITUDE, longitude)
            mFragment.arguments = mBundle
            return mFragment
        }
    }

    private var mLatitude = 0.0
    private var mLongitude = 0.0
    private var mJobs = Jobs()
    private var editPosition = 0
    var mList = ArrayList<AccountList>()

    private val mAddMembersAdapter by lazy {
        AddMembersAdapter(this)
    }

    private val mJobsViewModel by lazy {
        ViewModelProvider(this).get(JobsViewModel::class.java)
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
        arguments?.let { bundle ->
            mLatitude = bundle.getDouble(BUNDLE_LAT)
            mLongitude = bundle.getDouble(BUNDLE_LONGITUDE)
        }

        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mGetUpdateDataBroadcastReceiver,
                IntentFilter(INTENT_EDIT_PERMISSION)
            )

        //Set adapter
        rvListing.adapter = mAddMembersAdapter

        //Set on click listener
        btnInviteMembers.setOnClickListener(this)
        btnAddMembers.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)
        tvSkip.setOnClickListener(this)

        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mJobsViewModel.getModuleUsers("jobs")
        } else {
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

            for (i in mList.indices) {
                if (mList[i].id == mUserPrefsManager.loginedUser!!.id) {
                    mList.removeAt(i)
                    break
                }
            }
            mAddMembersAdapter.updateData(mList)
        })
    }

    override fun onPermission(jobs: Jobs, possition: Int, userName: String) {
        editPosition = possition
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = EditMembersPermissionsFragment.newInstance(
                jobs, userName, EditMembersPermissionsFragment.BUNDLE_ADD
            ),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )

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
            R.id.btnInviteMembers -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = InviteMembersPermissionsFragment.newInstance(mLatitude, mLongitude),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
            R.id.btnAddMembers -> {
                //Save the selected users list globally
                ApplicationGlobal.mJobUsersList.addAll(mAddMembersAdapter.getSelectedMembers())

                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = CreateNewJobFragment.newInstance(
                        mLatitude,
                        mLongitude, BUNDLE_ADD
                    ),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
            R.id.tvSkip -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = CreateNewJobFragment.newInstance(
                        mLatitude,
                        mLongitude, BUNDLE_ADD
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

    private val mGetUpdateDataBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                mJobs = p1!!.getParcelableExtra<Jobs>(INTENT_EDIT_DATA)!!

                //Update adapter
                val accounts = ArrayList<AccountDetail>()
                accounts.clear()
                accounts.addAll(mList[editPosition].accounts!!)
                accounts[0].permissions.jobs = mJobs

                mList[editPosition].accounts!!.addAll(accounts)
                mAddMembersAdapter.updateData(mList)

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ApplicationGlobal.mJobUsersList = ArrayList()
    }

}