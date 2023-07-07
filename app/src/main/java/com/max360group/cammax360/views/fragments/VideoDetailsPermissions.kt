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
import com.max360group.cammax360.repository.models.model.VideosModel
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

class VideoDetailsPermissions : BaseFragment(), View.OnClickListener,
    PhotoDetailPermissionsListener {

    companion object {
        const val BUNDLE_JOB_ID = "jobId"
        const val PARAM_SUBKIND = "mediaPhotos"
        const val PARAM_POSITION = "position"
        const val PARAM_VIDEO_LIST = "videoList"

        fun newInstance(
            mJobId: String,
            mImageList: ArrayList<VideosModel>,
            mPosition: Int
        ): VideoDetailsPermissions {
            val mFragment = VideoDetailsPermissions()
            val bundle = Bundle()
            bundle.putString(BUNDLE_JOB_ID, mJobId)
            bundle.putInt(PARAM_POSITION, mPosition)
            bundle.putParcelableArrayList(PARAM_VIDEO_LIST, mImageList)
            mFragment.arguments = bundle
            return mFragment
        }
    }

    private var mJobId = ""
    private var mPosition = 0
    private var mSelectedVideoList = ArrayList<VideosModel>()
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
            mSelectedVideoList =
                it.getParcelableArrayList<VideosModel>(PARAM_VIDEO_LIST) as ArrayList<VideosModel>
            mPosition = it.getInt(PARAM_POSITION)
            mJobId = it.getString(BUNDLE_JOB_ID).toString()
        }

        //Set data
        sdvPhoto.setImageURI(GeneralFunctions.getLocalImageFile(File(mSelectedVideoList[mPosition].video)))
        tvName.text=mSelectedVideoList[mPosition].videoName
        tvUserName.text=mUserPrefsManager.loginedUser!!.firstName
        ivPlayView.visibility=View.VISIBLE

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
            mMembersList.clear()
            mMembersList.addAll(it)

            //Remove my self from the list
            for (i in mMembersList.indices) {
                if (mMembersList[i].id == mUserPrefsManager.loginedUser!!.id) {
                    mMembersList.removeAt(i)
                    break
                }
            }

            if (mSelectedVideoList[mPosition].isUpdated){
                //Set the updated data
                mPhotoDetailPermissionsAdapter.updateData(mSelectedVideoList[mPosition].users!!)
            }else {
                //Add jobs users in image list
                for (i in mMembersList.indices) {
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

                //Add the default permissions
                mSelectedVideoList[mPosition].users = users

            }
            //Update adapter
            mPhotoDetailPermissionsAdapter.updateData(mSelectedVideoList[mPosition].users!!)
        })
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSave -> {
                //Update the selected video
                mSelectedVideoList[mPosition].users =
                    mPhotoDetailPermissionsAdapter.getUpdatedList()
                mSelectedVideoList[mPosition].isUpdated=true

                //Send broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(VideoPreviewFragment.INTENT_VIDEO).putParcelableArrayListExtra(
                            VideoPreviewFragment.INTENT_VIDEO_LIST, mSelectedVideoList)
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