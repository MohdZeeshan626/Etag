package com.max360group.cammax360.views.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.JobMediaList
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobDetailViewModel
import com.max360group.cammax360.viewmodels.MediaDetailViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.CommentsAdapter
import com.max360group.cammax360.views.adapters.TagsAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.dialgofragments.AlertDialogFragment
import com.max360group.cammax360.views.dialgofragments.CameraDialogFragment.CameraDialogFragment.Companion.BUNDLE_AFTER
import com.max360group.cammax360.views.dialgofragments.CameraEditDialogFragment
import com.max360group.cammax360.views.dialgofragments.UpdateMediaInfoDialogFragment
import com.max360group.cammax360.views.fragments.AccountsFragment.Companion.BUNDLE_NORMAl_VIEWS
import com.max360group.cammax360.views.utils.JobsConstants
import kotlinx.android.synthetic.main.fragment_before_after_detail.*
import kotlinx.android.synthetic.main.fragment_before_after_detail.etComment
import kotlinx.android.synthetic.main.fragment_before_after_detail.ivTagsBefore
import kotlinx.android.synthetic.main.fragment_before_after_detail.sdvImageAfter
import kotlinx.android.synthetic.main.fragment_before_after_detail.sdvImageBefore
import kotlinx.android.synthetic.main.fragment_photo_detail.*
import kotlinx.android.synthetic.main.fragment_photo_detail.btnMenu
import kotlinx.android.synthetic.main.fragment_photo_detail.rvTags
import kotlinx.android.synthetic.main.fragment_photo_detail.rvUsersList
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File


class BeforeAfterPhotoDetailFragment : BaseFragment(), View.OnClickListener {

    companion object {
        const val BUNDLE_MEDIA = "jobId"
        const val BUNDLE_TYPE = "type"

        fun newInstance(mJobMediaList: JobMediaList, type: Int): BeforeAfterPhotoDetailFragment {
            val mBeforeAfterPhotoDetailFragment = BeforeAfterPhotoDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_MEDIA, mJobMediaList)
            bundle.putInt(BUNDLE_TYPE, type)
            mBeforeAfterPhotoDetailFragment.arguments = bundle

            return mBeforeAfterPhotoDetailFragment
        }
    }

    private val mMediaDetailViewModel by lazy {
        ViewModelProvider(this).get(MediaDetailViewModel::class.java)
    }


    private val mJobDetailViewModel by lazy {
        ViewModelProvider(this).get(JobDetailViewModel::class.java)
    }

    private val mConversationAdapter by lazy {
        CommentsAdapter(this)
    }
    private val mTagsAdapter by lazy {
        TagsAdapter(this)
    }

    var isMenu = true
    private var mJobMediaList: JobMediaList? = null
    private var mType = 0// 0:before 1:after

    override val layoutId: Int
        get() = R.layout.fragment_before_after_detail

    override fun init(savedInstanceState: Bundle?) {
        // Set toolbar
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)
        ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)
        Glide
            .with(requireContext())
            .load(ApplicationGlobal.organisationLogo)
            .placeholder(R.drawable.ic_mimi_logo)
            .into(ivToolbarLeftIcon)

        //get arguments
        mJobMediaList = arguments?.getParcelable(BUNDLE_MEDIA)
        mType = arguments?.getInt(BUNDLE_TYPE)!!

        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mUpdateDialogReceiver,
                IntentFilter(PhotoDetailFragment.INTENT_PHOTO_DETAIL)
            )

        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mUpdatePhotoReceiver,
                IntentFilter(PhotoDetailFragment.INTENT_PHOTO_UPLOAD)
            )


        //Set detail
        initSetData(mJobMediaList, mType)

        //Set adapter
        rvTags.adapter = mTagsAdapter
        rvUsersList.adapter = mConversationAdapter

        //Set click listener
        btnMenu.setOnClickListener(this)
        sdvImageBefore.setOnClickListener(this)
        sdvImageAfter.setOnClickListener(this)
        btnPost.setOnClickListener(this)
        ivAfterVisibility.setOnClickListener(this)
        ivBeforeVisibility.setOnClickListener(this)
        ivTagsBefore.setOnClickListener(this)
        ivTagsAfter.setOnClickListener(this)
        ivCancelBefore.setOnClickListener(this)
        ivCancelAfter.setOnClickListener(this)
        ivEditBefore.setOnClickListener(this)
        ivEditAfter.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

        //Get comments
        mJobDetailViewModel.getMediaComment(
            mJobMediaList!!.jobId, mJobMediaList!!.id, mJobMediaList!!.medias!![mType].id,
            JobsConstants.JOB_KIND_COMMENT
        )
    }

    override val viewModel: BaseViewModel?
        get() = mMediaDetailViewModel

    override fun observeProperties() {
        mJobDetailViewModel.onGetComments().observe(this, Observer {
            mConversationAdapter.updateFunction(it)
        })

        mJobDetailViewModel.onShowShimmer().observe(this, Observer {
            if (it) {
                rvUsersList.showShimmerAdapter()
            } else {
                rvUsersList.hideShimmerAdapter()
            }
        })

        mMediaDetailViewModel.onGetMediaDetail().observe(this, Observer {
            mJobMediaList!!.medias!![mType].name = it.medias!![0].name
            mJobMediaList!!.medias!![mType].tags = it.medias[0].tags
            mJobMediaList!!.medias!![mType].media = it.medias[0].media
            mJobMediaList!!.medias!![mType].mediaURL = it.medias[0].mediaURL

            initSetData(mJobMediaList, mType)
        })

        mMediaDetailViewModel.onGetPhotoDetailFromLocal().observe(this, Observer {
            mJobMediaList!!.medias!![mType].name = it.medias!![mType].name
            mJobMediaList!!.medias!![mType].tags = it.medias!![mType].tags
            mJobMediaList!!.medias!![mType].mediaURL = it.medias!![mType].mediaURL

            initSetData(mJobMediaList, mType)
        })

        mMediaDetailViewModel.onDeleteMedia().observe(this, Observer {
            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(JobDetailsFragment.BUNDLE_JOB_DETAIL_INTENT))
            (requireContext() as BaseAppCompactActivity).onBackPressed()
        })

        mMediaDetailViewModel.onSuccess().observe(this, Observer {
            //Call api refresh data
            mMediaDetailViewModel.getPhotoDetail(
                mJobMediaList!!.jobId,
                mJobMediaList!!.id,
                JobsConstants.JOB_KIND_PHOTO,
                mJobMediaList!!.medias!![mType].id
            )
        })
    }

    private fun initSetData(mData: JobMediaList?, mType: Int) {
        if (mType == 0) {
            ivAfterImageSelected.visibility = GONE
            ivBeforeImageSelected.visibility = VISIBLE
        } else {
            ivAfterImageSelected.visibility = VISIBLE
            ivBeforeImageSelected.visibility = GONE
        }

        //Set before image
        if (mData!!.medias!![0].mediaURL.startsWith("https://")) {
            val imageName =
                mData.medias!![0].media.split("${JobsConstants.JOB_KIND_PHOTO}/")

            // Check if local file exists, if it exists set file from local else downloads from server
            val file = GeneralFunctions
                .getLocalMediaFile(requireActivity(), imageName[1])
            if (file.exists()) {
                sdvImageBefore.setImageURI(
                    GeneralFunctions.getLocalImageFile(file))
            } else {
                sdvImageBefore.setImageURI(mData.medias!![0].mediaURL)
            }

        } else {
            sdvImageBefore.setImageURI(GeneralFunctions.getLocalImageFile(File(mData.medias!![0].mediaURL)))
        }

        //Set after image
        if (mData.medias!![1].mediaURL.startsWith("https://")) {
            val imageName =
                mData.medias!![1].media.split("${JobsConstants.JOB_KIND_PHOTO}/")

            // Check if local file exists, if it exists set file from local else downloads from server
            val file = GeneralFunctions
                .getLocalMediaFile(requireActivity(), imageName[1])
            if (file.exists()) {
                sdvImageAfter.setImageURI(GeneralFunctions.getLocalImageFile(file))
            } else {
                sdvImageAfter.setImageURI(mData.medias!![1].mediaURL)
            }
        } else {
            sdvImageAfter.setImageURI(GeneralFunctions.getLocalImageFile(File(mData.medias!![1].mediaURL)))
        }

        if (mData.medias!![1].mediaURL.contains("Placeholder_")) {
            ivEditAfter.setImageResource(R.drawable.ic_camera_intent)
        }

        if (mData.medias!![0].mediaURL.contains("Placeholder_")) {
            ivEditBefore.setImageResource(R.drawable.ic_camera_intent)
        }


        tvMedianame.text = mData.medias!![mType].name
        (mData.creatorId.firstName + " " + mData.creatorId.lastName).also { tvUser.text = it }

        //Update adapter
        mData.medias!![mType].tags?.let { mTagsAdapter.updateData(it) }
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnMenu -> {
                if (isMenu) {
                    isMenu = false
                    viewGoneAnimator(ivShareBefore)
                    viewGoneAnimator(ivCancelBefore)
                    viewGoneAnimator(ivEditBefore)
                    viewGoneAnimator(ivShareBefore)
                    viewGoneAnimator(ivTagsBefore)
                    viewGoneAnimator(ivBeforeVisibility)

                    viewGoneAnimator(ivShareAfter)
                    viewGoneAnimator(ivCancelAfter)
                    viewGoneAnimator(ivEditAfter)
                    viewGoneAnimator(ivShareAfter)
                    viewGoneAnimator(ivTagsAfter)
                    viewGoneAnimator(ivAfterVisibility)

                } else {
                    isMenu = true
                    viewVisibleAnimator(ivShareBefore)
                    viewVisibleAnimator(ivCancelBefore)
                    viewVisibleAnimator(ivEditBefore)
                    viewVisibleAnimator(ivShareBefore)
                    viewVisibleAnimator(ivTagsBefore)
                    viewVisibleAnimator(ivBeforeVisibility)

                    viewVisibleAnimator(ivShareAfter)
                    viewVisibleAnimator(ivCancelAfter)
                    viewVisibleAnimator(ivEditAfter)
                    viewVisibleAnimator(ivShareAfter)
                    viewVisibleAnimator(ivTagsAfter)
                    viewVisibleAnimator(ivAfterVisibility)
                }
            }

            R.id.ivTagsAfter -> {
                mType = 1
                UpdateMediaInfoDialogFragment.newInstance(
                    mJobMediaList!!.mediaLocalId,
                    mJobMediaList!!.medias!![1],
                    mType,
                    JobsConstants.JOB_KIND_PHOTO
                ).show(childFragmentManager, "")
            }

            R.id.ivTagsBefore -> {
                mType = 0
                UpdateMediaInfoDialogFragment.newInstance(
                    mJobMediaList!!.mediaLocalId,
                    mJobMediaList!!.medias!![0],
                    mType,
                    JobsConstants.JOB_KIND_PHOTO
                ).show(childFragmentManager, "")
            }

            R.id.sdvImageAfter -> {
                //Set detail
                mType = 1
                initSetData(mJobMediaList, mType)

                //Get comments
                mJobDetailViewModel.getMediaComment(
                    mJobMediaList!!.jobId, mJobMediaList!!.id, mJobMediaList!!.medias!![mType].id,
                    JobsConstants.JOB_KIND_COMMENT
                )

            }

            R.id.sdvImageBefore -> {
                //Set detail
                mType = 0
                initSetData(mJobMediaList, mType)

                //Get comments
                mJobDetailViewModel.getMediaComment(
                    mJobMediaList!!.jobId, mJobMediaList!!.id, mJobMediaList!!.medias!![mType].id,
                    JobsConstants.JOB_KIND_COMMENT
                )

            }

            R.id.ivBeforeVisibility -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = MediaVisibilityFragment.newInstance(
                        mJobMediaList,
                        JobsConstants.JOB_KIND_PHOTO,
                        0
                    ),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }

            R.id.ivAfterVisibility -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = MediaVisibilityFragment.newInstance(
                        mJobMediaList,
                        JobsConstants.JOB_KIND_PHOTO,
                        1
                    ),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }

            R.id.btnPost -> {
                mJobDetailViewModel.createComment(
                    mJobMediaList!!.jobId, mJobMediaList!!.id, mJobMediaList!!.medias!![mType].id,
                    JobsConstants.JOB_KIND_COMMENT, etComment.text.toString().trim()
                )
                etComment.setText("")
            }

            R.id.ivCancelBefore -> {
                var mAlertDialogFragment = AlertDialogFragment.newInstance(
                    getString(R.string.st_delete_media_message) +
                            mJobMediaList!!.medias!![0].id,
                    getString(R.string.delete_confirmation)
                )
                mAlertDialogFragment.setTargetFragment(this, 1234)
                mAlertDialogFragment.show(
                    parentFragmentManager,
                    getString(R.string.dialog)
                )
            }
            R.id.ivCancelAfter -> {
                var mAlertDialogFragment = AlertDialogFragment.newInstance(
                    getString(R.string.st_delete_media_message) +
                            mJobMediaList!!.medias!![0].id,
                    getString(R.string.delete_confirmation)
                )
                mAlertDialogFragment.setTargetFragment(this, 1234)
                mAlertDialogFragment.show(
                    parentFragmentManager,
                    getString(R.string.dialog)
                )
            }
            R.id.ivEditAfter -> {
                mType = 1
                if (mJobMediaList!!.medias!![1].mediaURL.contains("Placeholder_")) {
                    (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                        fragment = CameraEditDialogFragment.newInstance(
                            BUNDLE_AFTER,
                            mJobMediaList!!.medias!![0].mediaURL,
                            mJobMediaList!!.medias!![0].media
                        ),
                        containerViewId = R.id.flFragContainerMain,
                        enterAnimation = R.animator.slide_right_in,
                        popExitAnimation = R.animator.slide_right_out
                    )
                } else {
                    (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                        fragment = EditPhotoFragment.newInstance(mJobMediaList!!, mType),
                        containerViewId = R.id.flFragContainerMain,
                        enterAnimation = R.animator.slide_right_in,
                        popExitAnimation = R.animator.slide_right_out
                    )
                }
            }

            R.id.ivEditBefore -> {
                mType = 0
                if (mJobMediaList!!.medias!![0].mediaURL.contains("Placeholder_")) {
                    (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                        fragment = CameraEditDialogFragment.newInstance(
                            BUNDLE_AFTER,
                            mJobMediaList!!.medias!![1].mediaURL,
                            mJobMediaList!!.medias!![1].media
                        ),
                        containerViewId = R.id.flFragContainerMain,
                        enterAnimation = R.animator.slide_right_in,
                        popExitAnimation = R.animator.slide_right_out
                    )
                } else {
                    (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                        fragment = EditPhotoFragment.newInstance(mJobMediaList!!, mType),
                        containerViewId = R.id.flFragContainerMain,
                        enterAnimation = R.animator.slide_right_in,
                        popExitAnimation = R.animator.slide_right_out
                    )
                }
            }

            R.id.ivToolbarUserImage -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = AccountsFragment.newInstance(BUNDLE_NORMAl_VIEWS),
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

    private fun viewGoneAnimator(view: View) {
        view.animate()
            .alpha(0f)
            .setDuration(600)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = GONE
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Activity.RESULT_OK == resultCode && 1234 == requestCode) {
            when (intent?.getIntExtra(AlertDialogFragment.INTENT_SUBMIT, 0)) {
                AlertDialogFragment.VALUE_TRUE -> {
                    var mList = ArrayList<String>()
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mList.add(mJobMediaList!!.id)
                        //call api
                        mMediaDetailViewModel.deleteMedia(
                            mJobMediaList!!.jobId,
                            JobsConstants.JOB_KIND_PHOTO, mList
                        )
                    } else {
                        mList.add(mJobMediaList!!.id)
                        mMediaDetailViewModel.deleteMediaFromLocal(
                            mJobMediaList!!.mediaLocalId.toString(),
                            JobsConstants.JOB_KIND_PHOTO,
                            mJobMediaList!!.jobId, mList
                        )
                    }
                }

            }
        }
    }

    private fun viewVisibleAnimator(view: View) {
        view.animate()
            .alpha(1f)
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = VISIBLE
                }
            })
    }

    private val mUpdateDialogReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mMediaDetailViewModel.getPhotoDetail(
                        mJobMediaList!!.jobId,
                        mJobMediaList!!.id,
                        JobsConstants.JOB_KIND_PHOTO,
                        mJobMediaList!!.medias!![mType].id
                    )
                } else {
                    mMediaDetailViewModel.getMediaFromLocal(mJobMediaList!!.mediaLocalId.toString())
                }
            } catch (e: Exception) {

            }

        }
    }

    private val mUpdatePhotoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                val mImage = p1?.getStringExtra(BeforeAfterImageUpdateFragment.INTENT_AFTER_IMAGE)
                if (mType == 0) {
                    sdvImageBefore.setImageURI(GeneralFunctions.getLocalImageFile(File(mImage)))
                    ivEditBefore.setImageResource(R.drawable.ic_edit)
                } else {
                    sdvImageAfter.setImageURI(GeneralFunctions.getLocalImageFile(File(mImage)))
                    ivEditAfter.setImageResource(R.drawable.ic_edit)
                }

                //Save data
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mMediaDetailViewModel.updateMediaInfo(
                        id = mJobMediaList!!.medias!![mType].id,
                        kind = mJobMediaList!!.kind,
                        name = mJobMediaList!!.medias!![mType].name,
                        mTagsList = mJobMediaList!!.medias!![mType].tags!!,
                        mMedia = mJobMediaList!!.medias!![mType].media,
                        mEditMediaFil = mImage!!,
                        mJobId = mJobMediaList!!.jobId
                    )
                } else {
                    mMediaDetailViewModel.updateMediaInfoInLocal(
                        id = mJobMediaList!!.mediaLocalId.toString(),
                        kind = mJobMediaList!!.kind,
                        name = mJobMediaList!!.medias!![mType].name,
                        mTagsList = mJobMediaList!!.medias!![mType].tags!!,
                        media = mImage!!,
                        mediaUrl = mImage,
                        mType,
                        thumbnail = mJobMediaList!!.medias!![mType].thumbnailURL,
                        true
                    )
                }

            } catch (e: Exception) {

            }

        }
    }

}