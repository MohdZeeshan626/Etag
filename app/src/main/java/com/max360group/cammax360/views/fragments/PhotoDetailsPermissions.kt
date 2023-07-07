package com.max360group.cammax360.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.JobMembers
import com.max360group.cammax360.repository.models.model.BeforeAfterImageModel
import com.max360group.cammax360.repository.models.model.Permissions
import com.max360group.cammax360.repository.models.model.Users
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobMediaViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.PhotoDetailPermissionsAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.dialgofragments.MediaTagsDialogFragment
import com.max360group.cammax360.views.dialgofragments.MediaTagsDialogFragment.Companion.BUNDLE_IMAGE_AFTER
import com.max360group.cammax360.views.dialgofragments.MediaTagsDialogFragment.Companion.BUNDLE_IMAGE_BEFORE
import com.max360group.cammax360.views.dialgofragments.MediaTagsDialogFragment.Companion.BUNDLE_IMAGE_SIMPLE
import com.max360group.cammax360.views.interfaces.PhotoDetailPermissionsListener
import kotlinx.android.synthetic.main.fragment_create_new_job.rvJobsList
import kotlinx.android.synthetic.main.fragment_photo_details_permissions.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File

class PhotoDetailsPermissions : BaseFragment(), View.OnClickListener,
    PhotoDetailPermissionsListener {

    companion object {
        const val BUNDLE_JOB_ID = "jobId"
        const val PARAM_SUBKIND = "mediaPhotos"
        const val PARAM_POSITION = "position"
        const val PARAM_IMAGE_LIST = "imageList"
        const val PARAM_IMAGE_TYPE = "type"

        fun newInstance(
            mJobId: String,
            mImageList: ArrayList<BeforeAfterImageModel>,
            mPosition: Int,
            mImageType: Int
        ): PhotoDetailsPermissions {
            val mFragment = PhotoDetailsPermissions()
            val bundle = Bundle()
            bundle.putString(BUNDLE_JOB_ID, mJobId)
            bundle.putInt(PARAM_POSITION, mPosition)
            bundle.putInt(PARAM_IMAGE_TYPE, mImageType)
            bundle.putParcelableArrayList(PARAM_IMAGE_LIST, mImageList)
            mFragment.arguments = bundle
            return mFragment
        }
    }

    private var mJobId = ""
    private var mPosition = 0
    private var mImageType = 0
    private var mSelectedImageList = ArrayList<BeforeAfterImageModel>()
    private var mMembersList = mutableListOf<JobMembers>()
    private var users: ArrayList<Users>? = ArrayList()

    private val mPhotoDetailPermissionsAdapter by lazy {
        PhotoDetailPermissionsAdapter(this)
    }

    private val mJobMediaViewModel by lazy {
        ViewModelProvider(this).get(JobMediaViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_photo_details_permissions

    override fun init(savedInstanceState: Bundle?) {
        //Set toolbar
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)
        ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)

        //Get arguments
        arguments?.let {
            mJobId = it.getString(BUNDLE_JOB_ID).toString()
            mPosition = it.getInt(PARAM_POSITION)
            mImageType = it.getInt(PARAM_IMAGE_TYPE)
            mSelectedImageList =
                it.getParcelableArrayList<BeforeAfterImageModel>(PARAM_IMAGE_LIST) as ArrayList<BeforeAfterImageModel>

        }

        //Set data
        tvUserName.text = mUserPrefsManager.loginedUser!!.firstName

        //Set click listener
        btnSave.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

        //Call api
        mJobMediaViewModel.getJobMembers(PARAM_SUBKIND, mJobId)

        //Set adapter
        rvJobsList.adapter = mPhotoDetailPermissionsAdapter
    }

    override val viewModel: BaseViewModel?
        get() = mJobMediaViewModel

    override fun observeProperties() {
        mJobMediaViewModel.onGetMembers().observe(this, Observer {
            if (it.isNotEmpty()) {
                mMembersList.clear()
                mMembersList.addAll(it)

                //Remove my self from the list
                for (i in mMembersList.indices) {
                    if (mMembersList[i].id == mUserPrefsManager.loginedUser!!.id) {
                        mMembersList.removeAt(i)
                        break
                    }
                }

                //Show members permissions bases on image type
                when (mImageType) {
                    BUNDLE_IMAGE_AFTER -> {
                        if (mSelectedImageList[mPosition].usersAfter!!.isNotEmpty()) {
                            //updated users permissions
                            updateData()

                        } else {
                            initAddMembers()

                        }
                    }
                    BUNDLE_IMAGE_BEFORE -> {
                        if (mSelectedImageList[mPosition].usersbefore!!.isNotEmpty()) {
                            //updated users permissions
                            updateData()

                        } else {
                            initAddMembers()

                        }
                    }
                    BUNDLE_IMAGE_SIMPLE -> {
                        if (mSelectedImageList[mPosition].users!!.isNotEmpty()) {
                            //updated users permissions
                            updateData()

                        } else {
                            initAddMembers()

                        }
                    }
                }
            }
        })
    }

    private fun initAddMembers() {
        //Add jobs users in image list
        for (i in mMembersList.indices) {
            var mUsers=Users()
            mUsers.userId
            users!!.add(Users(userId = mMembersList[i].id,
                name = mMembersList[i].firstName + " " + mMembersList[i].lastName,
                email = mMembersList[i].email,
                primaryUserId = mMembersList[i].accounts[0].primaryUserId,
                permissions = Permissions(
                    base = mMembersList[i].accounts[0].permissions.jobs!!.mediaPhotos,
                    comments = mMembersList[i].accounts[0].permissions.jobs!!.comments,
                    members = mMembersList[i].accounts[0].permissions.jobs!!.members))
            )
        }

        //Add by default all members in  image list
        when (mImageType) {
            MediaTagsDialogFragment.BUNDLE_IMAGE_AFTER -> {
                mSelectedImageList[mPosition].usersAfter = users
            }
            MediaTagsDialogFragment.BUNDLE_IMAGE_BEFORE -> {
                mSelectedImageList[mPosition].usersbefore = users

            }
            MediaTagsDialogFragment.BUNDLE_IMAGE_SIMPLE -> {
                mSelectedImageList[mPosition].users = users
            }
        }

        //update adapter and image name bases on image type
        updateData()
    }

    private fun updateData() {
        when (mImageType) {
            MediaTagsDialogFragment.BUNDLE_IMAGE_AFTER -> {
                tvName.text = mSelectedImageList[mPosition].afterImageName
                sdvPhoto.setImageURI(GeneralFunctions.getLocalImageFile(File(
                    mSelectedImageList[mPosition].afterImage)))

                mPhotoDetailPermissionsAdapter.updateData(mSelectedImageList[mPosition].usersAfter!!)
            }
            MediaTagsDialogFragment.BUNDLE_IMAGE_BEFORE -> {
                tvName.text = mSelectedImageList[mPosition].beforeImageName
                sdvPhoto.setImageURI(GeneralFunctions.getLocalImageFile(File(
                    mSelectedImageList[mPosition].beforeImage)))

                mPhotoDetailPermissionsAdapter.updateData(mSelectedImageList[mPosition].usersbefore!!)
            }
            MediaTagsDialogFragment.BUNDLE_IMAGE_SIMPLE -> {
                tvName.text = mSelectedImageList[mPosition].simpleImageName
                sdvPhoto.setImageURI(GeneralFunctions.getLocalImageFile(File(
                    mSelectedImageList[mPosition].simpleImage)))

                mPhotoDetailPermissionsAdapter.updateData(mSelectedImageList[mPosition].users!!)
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSave -> {
                //update the permissions bases on image type
                when (mImageType) {
                    BUNDLE_IMAGE_AFTER -> {
                        mSelectedImageList[mPosition].usersAfter =
                            mPhotoDetailPermissionsAdapter.getUpdatedList()

                    }

                    BUNDLE_IMAGE_BEFORE -> {
                        mSelectedImageList[mPosition].usersbefore =
                            mPhotoDetailPermissionsAdapter.getUpdatedList()

                    }

                    BUNDLE_IMAGE_SIMPLE -> {
                        mSelectedImageList[mPosition].users =
                            mPhotoDetailPermissionsAdapter.getUpdatedList()

                    }
                }

                //Send broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(BeforeAfterImageUpdateFragment.INTENT_IMAGE).putExtra(
                            BeforeAfterImageUpdateFragment.BUNDLE_IMAGE_LIST, mSelectedImageList
                        )
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