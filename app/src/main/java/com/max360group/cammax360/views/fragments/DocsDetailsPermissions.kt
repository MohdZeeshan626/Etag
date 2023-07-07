package com.max360group.cammax360.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.DocsModel
import com.max360group.cammax360.repository.models.JobMembers
import com.max360group.cammax360.repository.models.model.*
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobMediaViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.PhotoDetailPermissionsAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.interfaces.PhotoDetailPermissionsListener
import kotlinx.android.synthetic.main.fragment_create_new_job.rvJobsList
import kotlinx.android.synthetic.main.fragment_photo_details_permissions.*
import kotlinx.android.synthetic.main.toolbar.*

class DocsDetailsPermissions : BaseFragment(), View.OnClickListener,
    PhotoDetailPermissionsListener {

    companion object {
        const val PARAM_POSITION = "position"
        const val PARAM_DOCS_LIST = "videoList"
        const val PARAM_USERS = "users"
        const val INTENT_DOCS = "users"
        const val INTENT_DOCS_LIST = "users"

        fun newInstance(
            mDocsList: ArrayList<DocsModel>,
            mMembersList: ArrayList<JobMembers>,
            mPosition: Int
        ): DocsDetailsPermissions {
            val mFragment = DocsDetailsPermissions()
            val bundle = Bundle()
            bundle.putInt(PARAM_POSITION, mPosition)
            bundle.putParcelableArrayList(PARAM_DOCS_LIST, mDocsList)
            bundle.putParcelableArrayList(PARAM_USERS, mMembersList)
            mFragment.arguments = bundle
            return mFragment
        }
    }


    private var mPosition = 0
    private var mSelectedDocsList = ArrayList<DocsModel>()
    private var mMembersList = ArrayList<JobMembers>()
    private var users: ArrayList<Users>? = ArrayList()

    private val mPhotoDetailPermissionsAdapter by lazy {
        PhotoDetailPermissionsAdapter(this)
    }

    private val mJobMediaViewModel by lazy {
        ViewModelProvider(this).get(JobMediaViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_docs_details_permissions

    override fun init(savedInstanceState: Bundle?) {
        //Set toolbar
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)
        ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)

        //Get arguments
        arguments?.let {
            mSelectedDocsList =
                it.getParcelableArrayList<DocsModel>(PARAM_DOCS_LIST) as ArrayList<DocsModel>
            mPosition = it.getInt(PARAM_POSITION)
            mMembersList = it.getParcelableArrayList<JobMembers>(PARAM_USERS) as ArrayList<JobMembers>
        }

        //Set data
        tvName.text=mSelectedDocsList[mPosition].docsName
        tvUserName.text=mUserPrefsManager.loginedUser!!.firstName
        ivPlayView.visibility=View.VISIBLE

        //Set click listener
        btnSave.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

        //Set adapter
        rvJobsList.adapter = mPhotoDetailPermissionsAdapter

        //Add Users
        initAddUsers()
    }

    override val viewModel: BaseViewModel?
        get() = mJobMediaViewModel

    override fun observeProperties() {
    }

    private fun initAddUsers(){
        if (mSelectedDocsList[mPosition].isUpdated){
            //Set the updated data
            mPhotoDetailPermissionsAdapter.updateData(mSelectedDocsList[mPosition].users!!)
        }else {
            //Add jobs users in image list
            for (i in mMembersList!!.indices) {
                users!!.add(Users(userId = mMembersList!![i].id,
                    name = mMembersList!![i].firstName + " " + mMembersList!![i].lastName,
                    email = mMembersList!![i].email,
                    primaryUserId = mMembersList!![i].accounts[0].primaryUserId,
                    permissions = Permissions(
                        base = mMembersList!![i].accounts[0].permissions.jobs!!.documents,
                        comments = mMembersList!![i].accounts[0].permissions.jobs!!.comments,
                        members = mMembersList!![i].accounts[0].permissions.jobs!!.members))
                )
            }

            //Add the default permissions
            mSelectedDocsList[mPosition].users = users

        }
        //Update adapter
        mPhotoDetailPermissionsAdapter.updateData(mSelectedDocsList[mPosition].users!!)
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSave -> {
                //Update the selected video
                mSelectedDocsList[mPosition].users =
                    mPhotoDetailPermissionsAdapter.getUpdatedList()
                mSelectedDocsList[mPosition].isUpdated=true

                //Send broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(INTENT_DOCS).putParcelableArrayListExtra(
                            INTENT_DOCS_LIST, mSelectedDocsList)
                    )

                (requireContext() as BaseAppCompactActivity).onBackPressed()

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

    override fun updateData(position: Int) {
        mPhotoDetailPermissionsAdapter.notifyDataSetChanged()
    }
}