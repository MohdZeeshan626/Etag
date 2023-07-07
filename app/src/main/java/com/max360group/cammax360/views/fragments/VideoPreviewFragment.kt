package com.max360group.cammax360.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.model.VideosModel
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.adapters.VideosAdapter
import com.max360group.cammax360.views.interfaces.VideoListener
import kotlinx.android.synthetic.main.fragment_video_view.*
import kotlinx.android.synthetic.main.toolbar.*
import android.media.MediaPlayer.OnCompletionListener
import androidx.core.content.ContextCompat
import androidx.lifecycle.GeneratedAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.max360group.cammax360.repository.models.JobMembers
import com.max360group.cammax360.repository.models.model.Permissions
import com.max360group.cammax360.repository.models.model.User
import com.max360group.cammax360.services.CheckInternetService
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.JobMediaViewModel
import com.max360group.cammax360.views.activities.HomeActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.dialgofragments.VideoTagsDialogFragment
import java.lang.Exception


class VideoPreviewFragment : BaseFragment(), View.OnClickListener, VideoListener {

    companion object {
        const val BUNDLE_VIDEO_LIST = "image list"
        const val BUNDLE_POSITION = "position"
        const val INTENT_VIDEO = "videoIntent"
        const val INTENT_VIDEO_LIST = "videoList"
        const val BUNDLE_JOB_ID = "jobId"
        const val BUNDLE_LOCAL_ID = "localId"
        const val BUNDLE_USERS = "users"

        fun newInstance(
            position: Int,
            mImageList: ArrayList<VideosModel>,
            mJobId: String,
            mLocalId: String,
            mUser: ArrayList<User>?
        ): VideoPreviewFragment {
            val mFragment = VideoPreviewFragment()
            val bundle = Bundle()
            bundle.putInt(BUNDLE_POSITION, position)
            bundle.putString(BUNDLE_JOB_ID, mJobId)
            bundle.putString(BUNDLE_LOCAL_ID, mLocalId)
            bundle.putParcelableArrayList(BUNDLE_VIDEO_LIST, mImageList)
            bundle.putParcelableArrayList(BUNDLE_USERS, mUser)
            mFragment.arguments = bundle
            return mFragment
        }
    }

    private val mJobMediaViewModel by lazy {
        ViewModelProvider(this).get(JobMediaViewModel::class.java)
    }

    private val mVideosAdapter by lazy {
        VideosAdapter(this)
    }
    private var mPosition = -1
    private var mJobId = ""
    private var mLocalId = ""
    private var mMembersList = mutableListOf<JobMembers>()
    private var mUser: ArrayList<User>? =
        ArrayList()


    private var mVideoList = ArrayList<VideosModel>()

    override val layoutId: Int
        get() = R.layout.fragment_video_view

    override fun init(savedInstanceState: Bundle?) {
        // Set toolbar
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)
        ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)

        //Get arguments
        mVideoList = arguments?.getParcelableArrayList<VideosModel>(BUNDLE_VIDEO_LIST)!!
        mUser = arguments?.getParcelableArrayList<User>(BUNDLE_USERS)!!
        mPosition = arguments?.getInt(BUNDLE_POSITION)!!
        mJobId = arguments?.getString(BUNDLE_JOB_ID)!!
        mLocalId = arguments?.getString(BUNDLE_LOCAL_ID)!!

        //Set click listener
        ivDone.setOnClickListener(this)
        ivPlay.setOnClickListener(this)
        ivCamera.setOnClickListener(this)
        ivPreview.setOnClickListener(this)
        ivDetail.setOnClickListener(this)
        ivDelete.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

        //Set  adapter
        rvVideoView.adapter = mVideosAdapter
        mVideosAdapter.updateData(mVideoList, mPosition)


        videoPlayer.setVideoPath(mVideoList[mPosition].video)
        videoPlayer.start()
        videoPlayer.setOnCompletionListener(OnCompletionListener {
            ivPlay.visibility = View.VISIBLE
        })

    }

    override val viewModel: BaseViewModel?
        get() = mJobMediaViewModel

    override fun observeProperties() {
        mJobMediaViewModel.onMediaUpdated().observe(this, Observer {
            //Send broadcast
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(JobDetailsFragment.BUNDLE_JOB_DETAIL_INTENT)
                )
            (requireContext() as BaseAppCompactActivity).onBackPressed()
            (requireContext() as BaseAppCompactActivity).onBackPressed()
        })

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivCamera -> {
                (requireContext() as BaseAppCompactActivity).onBackPressed()
            }

            R.id.ivDetail -> {
                VideoTagsDialogFragment.newInstance(mVideoList, mPosition)
                    .show(childFragmentManager, "")
            }

            R.id.ivDelete -> {
                mVideoList.removeAt(mPosition)
                if (mVideoList.isEmpty()) {
                    (requireContext() as BaseAppCompactActivity).onBackPressed()
                } else {
                    mVideosAdapter.updateData(mVideoList, mPosition)
                }

                //Send broadcast
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(INTENT_VIDEO).putParcelableArrayListExtra(
                            INTENT_VIDEO_LIST,
                            mVideoList
                        )
                    )
            }
            R.id.ivPreview -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = VideoDetailsPermissions.newInstance(mJobId, mVideoList, mPosition),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )

            }
            R.id.ivPlay -> {
                videoPlayer.start()
                ivPlay.visibility = View.GONE
            }
            R.id.ivDone -> {
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mJobMediaViewModel.addJobVideo(mJobId, mVideoList, mUser!!)
                } else {
                    mJobMediaViewModel.saveVideoInLocal(mJobId, mLocalId, mVideoList, mUser)
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

    override fun onVideoClick(position: Int) {
        mPosition = position
        videoPlayer.setVideoPath(mVideoList[position].video)
        videoPlayer.start()
        ivPlay.visibility = View.GONE
    }

    override fun onDeleteImage(position: Int) {
        mVideoList.removeAt(position)
        if (mVideoList.isEmpty()) {
            (requireContext() as BaseAppCompactActivity).onBackPressed()
        } else {
            if (mPosition == mVideoList.size) {
                mPosition = 0
            }
            mVideosAdapter.updateData(mVideoList, mPosition)
        }

        //Send broadcast
        LocalBroadcastManager.getInstance(requireContext())
            .sendBroadcast(
                Intent(INTENT_VIDEO).putParcelableArrayListExtra(INTENT_VIDEO_LIST, mVideoList)
            )
    }


}