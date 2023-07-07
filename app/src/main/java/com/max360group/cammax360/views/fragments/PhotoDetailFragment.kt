package com.max360group.cammax360.views.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.JobMediaList
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.Constants.DOCS_URL
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.MediaDetailViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.VideoPlayerActivity
import com.max360group.cammax360.views.activities.VideoPlayerActivity.Companion.INTENT_VIDEO_DATA
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.CommentsAdapter
import com.max360group.cammax360.views.adapters.TagsAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.dialgofragments.AlertDialogFragment
import com.max360group.cammax360.views.dialgofragments.UpdateMediaInfoDialogFragment
import com.max360group.cammax360.views.utils.JobsConstants
import com.max360group.cammax360.views.utils.JobsConstants.JOB_KIND_COMMENT
import kotlinx.android.synthetic.main.fragment_photo_detail.*
import kotlinx.android.synthetic.main.fragment_photo_detail.btnMenu
import kotlinx.android.synthetic.main.fragment_photo_detail.ivDelete
import kotlinx.android.synthetic.main.fragment_photo_detail.webView
import kotlinx.android.synthetic.main.load_jobs_image.view.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


class PhotoDetailFragment : BaseFragment(), View.OnClickListener {

    companion object {
        const val BUNDLE_MEDIA = "jobId"
        const val INTENT_PHOTO_DETAIL = "photoDetail"
        const val INTENT_PHOTO_UPLOAD = "photoUpload"
        const val INTENT_FROM_CONVERSATION = 0
        const val INTENT_FROM_MEDIA = 1
        const val INTENT_FROM = "from"

        fun newInstance(mJobMediaList: JobMediaList): PhotoDetailFragment {
            val mPhotoDetailFragment = PhotoDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_MEDIA, mJobMediaList)
            mPhotoDetailFragment.arguments = bundle

            return mPhotoDetailFragment
        }
    }

    private val mMediaDetailViewModel by lazy {
        ViewModelProvider(this).get(MediaDetailViewModel::class.java)
    }

    private val mConversationAdapter by lazy {
        CommentsAdapter(this)
    }
    private val mTagsAdapter by lazy {
        TagsAdapter(this)
    }

    private var isMenu = true
    lateinit var mJobMediaList: JobMediaList
    private var mMediaKind = ""
    var doubleClick: Boolean? = false
    lateinit var player: SimpleExoPlayer

    override val layoutId: Int
        get() = R.layout.fragment_photo_detail

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
        mJobMediaList = arguments?.getParcelable(BUNDLE_MEDIA)!!
        initSetData(mJobMediaList)

        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mUpdateDialogReceiver,
                IntentFilter(INTENT_PHOTO_DETAIL)
            )

        //Set adapter
        rvTags.adapter = mTagsAdapter
        rvUsersList.adapter = mConversationAdapter

        //Set click listener
        btnMenu.setOnClickListener(this)
        btnAddPost.setOnClickListener(this)
        ivTags.setOnClickListener(this)
        ivVisibility.setOnClickListener(this)
        ivDelete.setOnClickListener(this)
        ivEdit.setOnClickListener(this)
        playerView.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)


        //Get comments
        mMediaDetailViewModel.getMediaComment(
            mJobMediaList.jobId, mJobMediaList.id, mJobMediaList.medias!![0].id,
            JOB_KIND_COMMENT
        )


    }

    override val viewModel: BaseViewModel?
        get() = mMediaDetailViewModel

    override fun observeProperties() {
        mMediaDetailViewModel.onGetComments().observe(this, Observer {
            mConversationAdapter.updateFunction(it)
        })

        mMediaDetailViewModel.onShowShimmer().observe(this, Observer {
            if (it) {
                rvUsersList.showShimmerAdapter()
            } else {
                rvUsersList.hideShimmerAdapter()
            }
        })

        mMediaDetailViewModel.onSuccess().observe(this, Observer {
            mMediaDetailViewModel.getPhotoDetail(
                mJobMediaList.jobId,
                mJobMediaList.id, JobsConstants.JOB_KIND_PHOTO, mJobMediaList.medias!![0].id
            )
        })

        mMediaDetailViewModel.onGetMediaDetail().observe(this, Observer {
            tvImageName.text = it.medias!![0].name
            (it.creatorId.firstName + " " + it.creatorId.lastName).also { tvUserName.text = it }

            //Set image
            val imageName =
                it.medias[0].media.split("${JobsConstants.JOB_KIND_PHOTO}/")

            // Check if local file exists, if it exists set file from local else downloads from server
            val file = GeneralFunctions
                .getLocalMediaFile(requireActivity(), imageName[1])
            if (file.exists()) {
                sdvImage.setImageURI(
                    GeneralFunctions.getLocalImageFile(file)
                )
            } else {
                sdvImage.setImageURI(it.medias[0].mediaURL)
            }

            mJobMediaList.medias!![0].name = it.medias[0].name
            mJobMediaList.medias!![0].tags = it.medias[0].tags

            //Update adapter
            mTagsAdapter.updateData(it.medias[0].tags)
        })

        mMediaDetailViewModel.onGetPhotoDetailFromLocal().observe(this, Observer {
            tvImageName.text = it.medias!![0].name
            (it.creatorId.firstName + " " + it.creatorId.lastName).also { tvUserName.text = it }

            if (mJobMediaList.medias!![0].mediaURL.startsWith("https://")) {
                sdvImage.setImageURI(it.medias!![0].mediaURL)
            } else {
                sdvImage.setImageURI(GeneralFunctions.getLocalImageFile(File(it.medias!![0].mediaURL)))
            }

            mJobMediaList.medias!![0].name = it.medias!![0].name
            mJobMediaList.medias!![0].tags = it.medias!![0].tags

            //Update adapter
            mTagsAdapter.updateData(it.medias!![0].tags!!)
        })

        mMediaDetailViewModel.onDeleteMedia().observe(this, Observer {
            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(JobDetailsFragment.BUNDLE_JOB_DETAIL_INTENT)
                )
            (requireContext() as BaseAppCompactActivity).onBackPressed()
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnMenu -> {
                if (isMenu) {
                    isMenu = false
                    viewGoneAnimator(ivShare)
                    viewGoneAnimator(ivDelete)
                    viewGoneAnimator(ivEdit)
                    viewGoneAnimator(ivShare)
                    viewGoneAnimator(ivTags)
                    viewGoneAnimator(ivVisibility)

                } else {
                    isMenu = true
                    viewVisibleAnimator(ivShare)
                    viewVisibleAnimator(ivDelete)
                    viewVisibleAnimator(ivEdit)
                    viewVisibleAnimator(ivShare)
                    viewVisibleAnimator(ivTags)
                    viewVisibleAnimator(ivVisibility)
                }
            }

            R.id.btnAddPost -> {
                mMediaDetailViewModel.createComment(
                    mJobMediaList.jobId, mJobMediaList.id, mJobMediaList.medias!![0].id,
                    JOB_KIND_COMMENT, etComment.text.toString().trim()
                )
                etComment.setText("")
            }

            R.id.ivTags -> {
                UpdateMediaInfoDialogFragment.newInstance(
                    mJobMediaList.mediaLocalId,
                    mJobMediaList.medias!![0],
                    0,
                    mMediaKind
                )
                    .show(childFragmentManager, "")
            }

            R.id.ivDelete -> {
                val mAlertDialogFragment = AlertDialogFragment.newInstance(
                    getString(R.string.st_delete_media_message) +
                            mJobMediaList.medias!![0].name,
                    getString(R.string.delete_confirmation)
                )
                mAlertDialogFragment.setTargetFragment(this, 1234)
                mAlertDialogFragment.show(
                    parentFragmentManager,
                    getString(R.string.dialog)
                )
            }

            R.id.ivVisibility -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = MediaVisibilityFragment.newInstance(mJobMediaList, mMediaKind),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }

            R.id.ivEdit -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = EditPhotoFragment.newInstance(mJobMediaList!!, 0),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }

            R.id.playerView -> {
                // Set double click for superLike
                if (doubleClick!!) {
                    val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
                    intent.putExtra(INTENT_VIDEO_DATA, mJobMediaList.medias!![0].mediaURL)
                    startActivity(intent)

                    //Pause the player
                    player.playWhenReady = false
                    player.playbackState
                }
                doubleClick = true
                Handler().postDelayed({
                    doubleClick = false
                }, 300)
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

    override fun onPause() {
        super.onPause()
        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .unregisterReceiver(
                mUpdateDialogReceiver
            )
    }

    private fun initSetData(mData: JobMediaList?) {
        when (mData!!.kind) {
            JobsConstants.JOB_KIND_VIDEO -> {
                playerView.visibility = VISIBLE
                ivEdit.visibility = GONE
                mMediaKind = JobsConstants.JOB_KIND_VIDEO

                //Initialize player
                player = ExoPlayerFactory.newSimpleInstance(requireContext())

                val mediaDataSourceFactory = DefaultDataSourceFactory(
                    requireContext(),
                    Util.getUserAgent(requireContext(), "mediaPlayerSample")
                )

                val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory)
                    .createMediaSource(Uri.parse(mData.medias!![0].mediaURL))

                with(player) {
                    prepare(mediaSource, false, false)
                    playWhenReady = true
                }

                playerView.setShutterBackgroundColor(Color.TRANSPARENT)
                playerView.player = player
                playerView.requestFocus()

            }
            JobsConstants.JOB_KIND_PHOTO -> {
                mMediaKind = JobsConstants.JOB_KIND_PHOTO

                if (mData.medias!![0].mediaURL.startsWith("https://")) {
                    val imageName =
                        mData.medias!![0].media.split("${JobsConstants.JOB_KIND_PHOTO}/")

                    // Check if local file exists, if it exists set file from local else downloads from server
                    val file = GeneralFunctions
                        .getLocalMediaFile(requireActivity(), imageName[1])
                    if (file.exists()) {
                        sdvImage.setImageURI(
                            GeneralFunctions.getLocalImageFile(file)
                        )
                    } else {
                        sdvImage.setImageURI(mData.medias!![0].mediaURL)
                    }

                } else {
                    sdvImage.setImageURI(GeneralFunctions.getLocalImageFile(File(mData.medias!![0].mediaURL)))
                }
            }
            else -> {
                ivEdit.visibility = GONE
                mMediaKind = JobsConstants.JOB_KIND_DOCUMENT

                webView.settings.javaScriptEnabled = true
                webView.settings.javaScriptCanOpenWindowsAutomatically = true
                webView.settings.domStorageEnabled = true
                webView.settings.setAppCacheEnabled(true)
                webView.settings.loadsImagesAutomatically = true
                webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;
                webView.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        if (view.title.equals(""))
                            view.reload()
                    }
                }
                var url = ""
                try {
                    url = URLEncoder.encode(mData.medias!![0].mediaURL, "utf-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

                if (mData.medias!![0].mediaURL.startsWith("https://")) {
                    webView.visibility = VISIBLE
                    webView.loadUrl(DOCS_URL + url)
                } else {
                    pdfView.visibility = VISIBLE
                    pdfView.fromUri(Uri.parse(GeneralFunctions.getLocalImageFile(File(mData.medias!![0].mediaURL))))
                        .defaultPage(0)
                        .spacing(10)
                        .load()
                }
            }
        }

        tvImageName.text = mData.medias!![0].name
        (mData.creatorId.firstName + " " + mData.creatorId.lastName).also { tvUserName.text = it }

        //Update adapter
        mTagsAdapter.updateData(mData.medias!![0].tags!!)
    }

    private fun viewGoneAnimator(view: View) {
        view.animate()
            .alpha(0f)
            .setDuration(600)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                }
            })
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Activity.RESULT_OK == resultCode && 1234 == requestCode) {
            when (intent?.getIntExtra(AlertDialogFragment.INTENT_SUBMIT, 0)) {
                AlertDialogFragment.VALUE_TRUE -> {
                    var mList = ArrayList<String>()
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mList.add(mJobMediaList.id)
                        //call api
                        mMediaDetailViewModel.deleteMedia(
                            mJobMediaList.jobId,
                            mMediaKind, mList
                        )
                    } else {
                        //call api
                        mList.add(mJobMediaList.id)
                        mMediaDetailViewModel.deleteMediaFromLocal(
                            mJobMediaList.mediaLocalId.toString(),
                            mMediaKind,
                            mJobMediaList.jobId,
                            mList
                        )
                    }
                }

            }
        }
    }

    private val mUpdateDialogReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mMediaDetailViewModel.getPhotoDetail(
                        mJobMediaList.jobId,
                        mJobMediaList.id, mMediaKind, mJobMediaList.medias!![0].id
                    )
                } else {
                    mMediaDetailViewModel.getMediaFromLocal(mJobMediaList.mediaLocalId.toString())
                }
            } catch (e: Exception) {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mMediaKind == JobsConstants.JOB_KIND_VIDEO) {
            player.stop()
        }
    }
}