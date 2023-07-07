package com.max360group.cammax360.views.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.google.android.material.color.MaterialColors
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_DISPLAY
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_SERVER_ISO
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobDetailViewModel
import com.max360group.cammax360.viewmodels.JobDetailViewModel.Companion.SORT_BY_CONTRIBUTOR
import com.max360group.cammax360.viewmodels.JobDetailViewModel.Companion.SORT_BY_DATE
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.HomeActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.*
import com.max360group.cammax360.views.adapters.GallaryAdapter.Companion.VIEW_LARGE
import com.max360group.cammax360.views.adapters.GallaryAdapter.Companion.VIEW_MEDIUM
import com.max360group.cammax360.views.adapters.GallaryAdapter.Companion.VIEW_SMALL
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.dialgofragments.AlertDialogFragment
import com.max360group.cammax360.views.dialgofragments.AlertDialogFragment.Companion.INTENT_SUBMIT
import com.max360group.cammax360.views.dialgofragments.CameraDialogFragment.CameraDialogFragment
import com.max360group.cammax360.views.dialgofragments.ContributorsDialogFragment
import com.max360group.cammax360.views.dialgofragments.TimeLineFilterDialogFragment
import com.max360group.cammax360.views.dialgofragments.TimeLineFilterDialogFragment.Companion.BUNDLE_FILTER_DATA
import com.max360group.cammax360.views.fragments.CalenderFragment.Companion.BUNDLE_NORMAl_VIEWS
import com.max360group.cammax360.views.interfaces.JobsLDetailListener
import com.max360group.cammax360.views.utils.JobsConstants
import kotlinx.android.synthetic.main.fragment_create_owner.*
import kotlinx.android.synthetic.main.fragment_job_details.*
import kotlinx.android.synthetic.main.fragment_job_details.btnMenu
import kotlinx.android.synthetic.main.fragment_job_details.etSearch
import kotlinx.android.synthetic.main.fragment_job_details.ivCamera
import kotlinx.android.synthetic.main.load_jobs_layout.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.collections.ArrayList


class JobDetailsFragment : BaseFragment(), DetailsMenuAdapter.onJobDetailListener,
    View.OnClickListener, JobsLDetailListener {

    companion object {
        const val BUNDLE_JOB_ID = "jobId"
        const val BUNDLE_LOCAL_ID = "localId"
        const val BUNDLE_JOB_DETAIL_INTENT = "jobDetailIntent"

        fun newInstance(mJobId: String, mLocalId: String): JobDetailsFragment {
            val takePhotosFragment = JobDetailsFragment()
            val bundle = Bundle()
            bundle.putString(BUNDLE_JOB_ID, mJobId)
            bundle.putString(BUNDLE_LOCAL_ID, mLocalId)
            takePhotosFragment.arguments = bundle

            return takePhotosFragment
        }
    }

    private val mJobDetailViewModel by lazy {
        ViewModelProvider(this).get(JobDetailViewModel::class.java)
    }


    private val mDetailsMenuAdapter by lazy {
        DetailsMenuAdapter(this)
    }

    private val mMediaAdapter by lazy {
        MediaAdapter(this)
    }

    private val mDocumentsAdapter by lazy {
        DocumentsAdapter(this)
    }

    private val mConversationAdapter by lazy {
        ConversationAdapter(this)
    }

    private val mNotesAdapter by lazy {
        NotesAdapter(this)
    }

    private val mTimeLineAdapter by lazy {
        TimeLineAdapter(this)
    }

    private var mCurrentTabIndex=0
    private var mJobId = ""
    private var mLocalId = ""
    private var mLatitude = 0.0
    private var mLongitude = 0.0
    private var mDetails = Details()
    private var mContributionList = ArrayList<UserX>()
    private var mJobMemberList = ArrayList<UserX>()
    private var mJobMediaList = ArrayList<JobMediaList>()
    private var mJobKind = JobsConstants.JOB_KIND_PHOTO
    private var mSortBy = SORT_BY_DATE
    private var mJobDetail: JobDetail? = null
    private var mTimeLineRequestModel = TimeLineRequestModel()

    override val layoutId: Int
        get() = R.layout.fragment_job_details

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

        //Get arguments
        mJobId = arguments?.getString(BUNDLE_JOB_ID).toString()
        mLocalId = arguments?.getString(BUNDLE_LOCAL_ID).toString()

        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mGetUpdateDataBroadcastReceiver,
                IntentFilter(BUNDLE_JOB_DETAIL_INTENT)
            )


        //Set adapter
        rvMediaListing.adapter = mMediaAdapter
        rvMenuList.adapter = mDetailsMenuAdapter

        //Set click listener
        ivGallaryLarge.setOnClickListener(this)
        ivGallarySmall.setOnClickListener(this)
        ivGallaryMedium.setOnClickListener(this)
        ivDirection.setOnClickListener(this)
        ivCamera.setOnClickListener(this)
        btnMenu.setOnClickListener(this)
        fabAddDocs.setOnClickListener(this)
        tvSortBy.setOnClickListener(this)
        btnPost.setOnClickListener(this)
        tvSeeAllContributors.setOnClickListener(this)
        tvTimeLineFilter.setOnClickListener(this)
        ivTimelineFilterAsc.setOnClickListener(this)
        ivTimelineFilterDesc.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

        //Call api
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mJobDetailViewModel.getJobDetail(mJobId, false)
            mJobDetailViewModel.getJobMedia(mJobId, mLocalId, mJobKind, mSortBy)
        } else {
            mJobDetailViewModel.getJobDetailFromLocal(mLocalId, mJobId)
            mJobDetailViewModel.getMediaFromLocal(mLocalId, mSortBy, mJobKind, mJobId)
            mDetailsMenuAdapter.updateData(mDetails)
        }

        //Search text listener
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                // filter your list from your input
                if (mJobKind == JobsConstants.JOB_KIND_COMMENT) {
                    mJobDetailViewModel.getAllComments(mJobId, s.toString())
                } else {
                    mJobDetailViewModel.getConversation(mJobId, mJobKind, s.toString(), false)
                }
            }
        })
    }

    override val viewModel: BaseViewModel?
        get() = mJobDetailViewModel

    override fun observeProperties() {
        mJobDetailViewModel.onGetJobDetail().observe(this, Observer {
            initJobDetail(it)
        })

        mJobDetailViewModel.onDeleteJob().observe(this, Observer {
            val intent = Intent(requireContext(), HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        })

        mJobDetailViewModel.onGetJobMedia().observe(this, Observer {
            mJobMediaList.clear()
            mJobMediaList.addAll(it)

            if (mJobKind == JobsConstants.JOB_KIND_PHOTO) {
                mDetails.mediaPhotosCount = it.size
                mDetailsMenuAdapter.updateData(mDetails)

            } else {
                mDetails.mediaDocsCount = it.size
                mDetailsMenuAdapter.updateData(mDetails)
            }
        })

        mJobDetailViewModel.onFilteredGetJobMedia().observe(this, Observer {
            if (mJobKind == JobsConstants.JOB_KIND_PHOTO) {
                mMediaAdapter.updateData(it)

            } else {
                mDocumentsAdapter.updateData(it)
            }
        })

        mJobDetailViewModel.onShowShimmer().observe(this, Observer {
            if (it) {
                rvMediaListing.showShimmerAdapter()
                rvConversationListing.showShimmerAdapter()
            } else {
                rvMediaListing.hideShimmerAdapter()
                rvConversationListing.hideShimmerAdapter()
            }
        })

        mJobDetailViewModel.onGetConversation().observe(this, Observer {
            when (mJobKind) {
                JobsConstants.JOB_KIND_CONVERSATION -> {
                    mDetails.conversationCount = it.size
                    mDetailsMenuAdapter.updateData(mDetails)
                    mConversationAdapter.updateFunction(it)
                }
                JobsConstants.JOB_KIND_COMMENT -> {
                    mDetails.commentsCount = it.size
                    mDetailsMenuAdapter.updateData(mDetails)
                    mNotesAdapter.updateFunction(it)
                }
                JobsConstants.JOB_KIND_NOTE -> {
                    mDetails.notesCount = it.size
                    mDetailsMenuAdapter.updateData(mDetails)
                    mConversationAdapter.updateFunction(it)
                }
            }
            rvConversationListing.setPadding(0, 0, 0, 0)
        })

        mJobDetailViewModel.onGetTimeline().observe(this, Observer {
            mTimeLineAdapter.updateData(it.list)
            rvConversationListing.setPadding(0, 20, 0, 0)
        })
    }

    private fun initJobDetail(jobDetail: JobDetail) {
        mJobDetail = jobDetail

        //Set detail
        clDetailView.visibility = View.VISIBLE
        tvJobName.text = jobDetail.job.title
        tvAddress.text = jobDetail.job.address.formatted
        (getString(R.string.st_due) + GeneralFunctions.changeUtcToLocal(
            jobDetail.job.endDt, DATE_FORMAT_SERVER_ISO, DATE_FORMAT_DISPLAY
        )).also { tvDate.text = it }

        if (jobDetail.job.details.contributionUpdatedAt != null) {
            (getString(R.string.st_most_recent_contribution) +
                    GeneralFunctions.changeDateFormat(
                        jobDetail.job.details.contributionUpdatedAt,
                        DATE_FORMAT_SERVER_ISO, DATE_FORMAT_DISPLAY
                    )).also { tvMostRecent.text = it }
        }

        mLatitude = jobDetail.job.address.location.coordinates!![0]
        mLongitude = jobDetail.job.address.location.coordinates!![1]

        //update list count
        mDetailsMenuAdapter.updateData(jobDetail.job.details)
        mDetails = jobDetail.job.details

        //Add contribution users
        mContributionList.clear()
        mJobMemberList.clear()
        mJobMemberList.addAll(jobDetail.job.users!!)
        for (mData in jobDetail.job.users!!) {
            if (mData.details.contributionCount != 0) {
                mContributionList.add(mData)
            }
        }

        if (mContributionList.isEmpty()) {
            tvContributors.visibility = View.GONE
            rvContributionList.visibility = View.GONE
            tvMostRecent.visibility = View.GONE
            view3.visibility = View.GONE
            tvSeeAllContributors.visibility = View.GONE
        } else {
            tvContributors.visibility = View.VISIBLE
            rvContributionList.visibility = View.VISIBLE
            tvMostRecent.visibility = View.VISIBLE
            view3.visibility = View.VISIBLE
            tvSeeAllContributors.visibility = View.VISIBLE
        }

        //Set jobImage
        for (i in jobDetail.job.medias!!.indices) {
            if (jobDetail.job.medias[i].kind == JobsConstants.JOB_KIND_PHOTO) {
                
                val imageName =
                    jobDetail.job.medias[i].medias!![0].media.split("${JobsConstants.JOB_KIND_PHOTO}/")
                // Check if local file exists, if it exists set file from local else downloads from server
                val file = GeneralFunctions
                    .getLocalMediaFile(requireActivity(), imageName[1])
                if (file.exists()) {
                    ivSdvImage.setImageURI(
                        GeneralFunctions.getLocalImageFile(file)
                    )
                } else {
                    ivSdvImage.setImageURI(jobDetail.job.medias[i].medias!![0].mediaURL)
                }

                break
            }
        }

        //Set contribution list
        val mJobsContributionsAdapter = JobsContributionsAdapter(this, mContributionList)
        mJobsContributionsAdapter.notifyDataSetChanged()
        rvContributionList.adapter = mJobsContributionsAdapter
    }

    private fun popUpMenu() {
        var wrapper: Context =
            ContextThemeWrapper(requireContext(), R.style.StylePopupMenu)
        var popUp = PopupMenu(wrapper, ivCamera, Gravity.END)
        popUp.menuInflater.inflate(R.menu.mediatype_pop_up_menu, popUp.menu)
        //Set Click Listener on Popup Menu Item
        popUp.setOnMenuItemClickListener { myItem ->
            val item = myItem!!.itemId

            when (item) {
                R.id.actionVideo -> {
                    (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                        fragment = RecordVideoFragment.newInstance(mJobId, mLocalId),
                        containerViewId = R.id.flFragContainerMain,
                        enterAnimation = R.animator.slide_right_in,
                        popExitAnimation = R.animator.slide_right_out
                    )
                }
                R.id.actionImage -> {
                    (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                        fragment = CameraDialogFragment.newInstance(
                            CameraDialogFragment.BUNDLE_SIMPLE,
                            "",
                            mJobId, mLocalId
                        ),
                        containerViewId = R.id.flFragContainerMain,
                        enterAnimation = R.animator.slide_right_in,
                        popExitAnimation = R.animator.slide_right_out
                    )
                }
            }
            true
        }
        popUp.show()
    }

    private fun popUpMenuDetail() {
        var wrapper: Context =
            ContextThemeWrapper(requireContext(), R.style.StylePopupMenu)
        var popUp = PopupMenu(wrapper, btnMenu, Gravity.END)
        popUp.menuInflater.inflate(R.menu.detail_pop_up_menu, popUp.menu)
        //Set Click Listener on Popup Menu Item
        popUp.setOnMenuItemClickListener { myItem ->
            when (myItem!!.itemId) {
                R.id.actionDescription -> {
                    (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                        fragment = EditMembersFragment.newInstance(mJobDetail),
                        containerViewId = R.id.flFragContainerMain,
                        enterAnimation = R.animator.slide_right_in,
                        popExitAnimation = R.animator.slide_right_out
                    )
                }
                R.id.actionEditJobDetail -> {
                    (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                        fragment = EditJobDetailFragment.newInstance(mJobDetail),
                        containerViewId = R.id.flFragContainerMain,
                        enterAnimation = R.animator.slide_right_in,
                        popExitAnimation = R.animator.slide_right_out
                    )
                }

                R.id.actionDeleteJob -> {
                    var mAlertDialogFragment = AlertDialogFragment.newInstance(
                        getString(R.string.st_delete_job_message),
                        getString(R.string.delete_confirmation)
                    )
                    mAlertDialogFragment.setTargetFragment(this, 1234)
                    mAlertDialogFragment.show(
                        parentFragmentManager,
                        getString(R.string.dialog)
                    )
                }

                R.id.actionVisibility -> {
                }
            }
            true
        }
        popUp.show()
    }

    private fun popUpMenuSortMedia() {
        var wrapper: Context =
            ContextThemeWrapper(requireContext(), R.style.StylePopupMenu)
        var popUp = PopupMenu(wrapper, tvSortBy, Gravity.CENTER)
        popUp.menuInflater.inflate(R.menu.sorting_media_pop_up_menu, popUp.menu)

        //Set Click Listener on Popup Menu Item
        popUp.setOnMenuItemClickListener { myItem ->
            val item = myItem!!.itemId
            when (item) {
                R.id.actionByDate -> {
                    tvSortBy.text = getString(R.string.st_by_date)
                    mSortBy = SORT_BY_DATE
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mJobDetailViewModel.getJobMedia(
                            mJobId,
                            mLocalId,
                            mJobKind,
                            mSortBy
                        )
                    } else {
                        mJobDetailViewModel.getMediaFromLocal(mLocalId, mSortBy, mJobKind, mJobId)
                    }
                }

                R.id.actionByContributor -> {
                    tvSortBy.text = getString(R.string.st_vy_contributor)
                    mSortBy = SORT_BY_CONTRIBUTOR
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mJobDetailViewModel.getJobMedia(
                            mJobId,
                            mLocalId,
                            mJobKind,
                            mSortBy
                        )
                    } else {
                        mJobDetailViewModel.getMediaFromLocal(mLocalId, mSortBy, mJobKind, mJobId)
                    }
                }

            }
            true
        }
        popUp.show()
    }


    override fun onItemClick(position: Int) {
        //For tab scrolling on click
        if (position!=0){
            if (position > mCurrentTabIndex) {
                rvMenuList.smoothScrollToPosition(position + 1)
            } else {
                rvMenuList.smoothScrollToPosition(position - 1)
            }
        }
        mCurrentTabIndex = position
        when (position) {
            0 -> {
                rvMediaListing.adapter = mMediaAdapter
                tvSortBy.visibility = View.VISIBLE
                ivGallaryMedium.visibility = View.VISIBLE
                ivGallarySmall.visibility = View.VISIBLE
                ivGallaryLarge.visibility = View.VISIBLE
                rvMediaListing.visibility = View.VISIBLE
                rvConversationListing.visibility = View.GONE
                etSearch.visibility = View.GONE
                fabAddDocs.visibility = View.GONE
                etMessage.visibility = View.GONE
                btnPost.visibility = View.GONE
                tvTimeLineFilter.visibility = View.GONE
                ivTimelineFilterDesc.visibility = View.GONE
                ivTimelineFilterAsc.visibility = View.GONE

                //Call api
                if (mJobKind != JobsConstants.JOB_KIND_PHOTO) {
                    mJobKind = JobsConstants.JOB_KIND_PHOTO
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mJobDetailViewModel.getJobMedia(
                            mJobId,
                            mLocalId,
                            mJobKind,
                            mSortBy
                        )
                    } else {
                        mJobDetailViewModel.getMediaFromLocal(mLocalId, mSortBy, mJobKind, mJobId)
                    }
                }
            }
            1 -> {
                rvMediaListing.adapter = mDocumentsAdapter
                tvSortBy.visibility = View.VISIBLE
                ivGallaryMedium.visibility = View.VISIBLE
                ivGallarySmall.visibility = View.VISIBLE
                ivGallaryLarge.visibility = View.VISIBLE
                rvMediaListing.visibility = View.VISIBLE
                rvConversationListing.visibility = View.GONE
                etSearch.visibility = View.GONE
                fabAddDocs.visibility = View.VISIBLE
                etMessage.visibility = View.GONE
                btnPost.visibility = View.GONE
                tvTimeLineFilter.visibility = View.GONE
                ivTimelineFilterDesc.visibility = View.GONE
                ivTimelineFilterAsc.visibility = View.GONE

                //Call api
                if (mJobKind != JobsConstants.JOB_KIND_DOCUMENT) {
                    mJobKind = JobsConstants.JOB_KIND_DOCUMENT
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mJobDetailViewModel.getJobMedia(
                            mJobId,
                            mLocalId,
                            mJobKind,
                            mSortBy
                        )
                    } else {
                        mJobDetailViewModel.getMediaFromLocal(mLocalId, mSortBy, mJobKind, mJobId)
                    }
                }
            }

            2 -> {
                rvConversationListing.adapter = mConversationAdapter
                tvSortBy.visibility = View.GONE
                ivGallaryMedium.visibility = View.GONE
                ivGallarySmall.visibility = View.GONE
                ivGallaryLarge.visibility = View.GONE
                rvMediaListing.visibility = View.GONE
                rvConversationListing.visibility = View.VISIBLE
                etSearch.visibility = View.VISIBLE
                fabAddDocs.visibility = View.GONE
                etMessage.visibility = View.VISIBLE
                btnPost.visibility = View.VISIBLE
                tvTimeLineFilter.visibility = View.GONE
                ivTimelineFilterDesc.visibility = View.GONE
                ivTimelineFilterAsc.visibility = View.GONE

                //Call api
                if (mJobKind != JobsConstants.JOB_KIND_CONVERSATION) {
                    mJobDetailViewModel.getConversation(
                        mJobId,
                        JobsConstants.JOB_KIND_CONVERSATION
                    )
                    mJobKind = JobsConstants.JOB_KIND_CONVERSATION
                }
            }

            3 -> {
                rvConversationListing.adapter = mConversationAdapter
                tvSortBy.visibility = View.GONE
                ivGallaryMedium.visibility = View.GONE
                ivGallarySmall.visibility = View.GONE
                ivGallaryLarge.visibility = View.GONE
                rvMediaListing.visibility = View.GONE
                rvConversationListing.visibility = View.VISIBLE
                etSearch.visibility = View.VISIBLE
                fabAddDocs.visibility = View.GONE
                etMessage.visibility = View.VISIBLE
                btnPost.visibility = View.VISIBLE
                tvTimeLineFilter.visibility = View.GONE
                ivTimelineFilterDesc.visibility = View.GONE
                ivTimelineFilterAsc.visibility = View.GONE

                //Call api
                if (mJobKind != JobsConstants.JOB_KIND_NOTE) {
                    mJobDetailViewModel.getConversation(
                        mJobId,
                        JobsConstants.JOB_KIND_NOTE
                    )
                    mJobKind = JobsConstants.JOB_KIND_NOTE
                }
            }

            4 -> {
                rvConversationListing.adapter = mNotesAdapter
                tvSortBy.visibility = View.GONE
                ivGallaryMedium.visibility = View.GONE
                ivGallarySmall.visibility = View.GONE
                ivGallaryLarge.visibility = View.GONE
                rvMediaListing.visibility = View.GONE
                rvConversationListing.visibility = View.VISIBLE
                etSearch.visibility = View.VISIBLE
                fabAddDocs.visibility = View.GONE
                etMessage.visibility = View.GONE
                btnPost.visibility = View.GONE
                tvTimeLineFilter.visibility = View.GONE
                ivTimelineFilterDesc.visibility = View.GONE
                ivTimelineFilterAsc.visibility = View.GONE

                //Call api
                if (mJobKind != JobsConstants.JOB_KIND_COMMENT) {
                    mJobDetailViewModel.getAllComments(mJobId)
                    mJobKind = JobsConstants.JOB_KIND_COMMENT
                }
            }

            5 -> {
                rvConversationListing.adapter = mTimeLineAdapter
                tvSortBy.visibility = View.GONE
                ivGallaryMedium.visibility = View.GONE
                ivGallarySmall.visibility = View.GONE
                ivGallaryLarge.visibility = View.GONE
                rvMediaListing.visibility = View.GONE
                rvConversationListing.visibility = View.VISIBLE
                etSearch.visibility = View.GONE
                fabAddDocs.visibility = View.GONE
                etMessage.visibility = View.GONE
                btnPost.visibility = View.GONE
                tvTimeLineFilter.visibility = View.VISIBLE
                ivTimelineFilterDesc.visibility = View.VISIBLE
                ivTimelineFilterAsc.visibility = View.VISIBLE

                //Call api
                mJobDetailViewModel.getJobTimeLine(mJobId)
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivGallaryLarge -> {
                ivGallaryLarge.setColorFilter(
                    MaterialColors.getColor(
                        requireContext(),
                        R.attr.primaryTextColor,
                        Color.BLACK
                    )
                )
                ivGallarySmall.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.colorCheckBoxNormal),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                ivGallaryMedium.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.colorCheckBoxNormal),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )

                mMediaAdapter.updateViewType(VIEW_LARGE)
                mDocumentsAdapter.updateViewType(VIEW_LARGE)

            }
            R.id.ivGallarySmall -> {
                ivGallaryLarge.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.colorCheckBoxNormal),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                ivGallarySmall.setColorFilter(
                    MaterialColors.getColor(
                        requireContext(),
                        R.attr.primaryTextColor,
                        Color.BLACK
                    )
                )
                ivGallaryMedium.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.colorCheckBoxNormal),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )

                mMediaAdapter.updateViewType(VIEW_SMALL)
                mDocumentsAdapter.updateViewType(VIEW_SMALL)
            }
            R.id.ivGallaryMedium -> {
                ivGallaryLarge.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.colorCheckBoxNormal),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                ivGallarySmall.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.colorCheckBoxNormal),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                ivGallaryMedium.setColorFilter(
                    MaterialColors.getColor(
                        requireContext(),
                        R.attr.primaryTextColor,
                        Color.BLACK
                    )
                )

                mMediaAdapter.updateViewType(VIEW_MEDIUM)
                mDocumentsAdapter.updateViewType(VIEW_MEDIUM)
            }
            R.id.ivDirection -> {
                val uri = "http://maps.google.com/maps?daddr=$mLatitude,$mLongitude (Destination)"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }
            R.id.ivCamera -> {
                popUpMenu()
            }
            R.id.btnMenu -> {
                popUpMenuDetail()
            }
            R.id.fabAddDocs -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = DocumentPreviewFragment.newInstance(mJobId, mLocalId),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
            R.id.tvSortBy -> {
                popUpMenuSortMedia()
            }

            R.id.tvTimeLineFilter -> {
                var mTimeLineFilterDialogFragment =
                    TimeLineFilterDialogFragment.newInstance(mJobMemberList)
                mTimeLineFilterDialogFragment.setTargetFragment(this, 2234)
                mTimeLineFilterDialogFragment.show(
                    parentFragmentManager,
                    getString(R.string.dialog)
                )

            }

            R.id.tvSeeAllContributors -> {
                ContributorsDialogFragment.newInstance(mContributionList)
                    .show(childFragmentManager, "")
            }

            R.id.btnPost -> {
                mJobDetailViewModel.createConversation(
                    mJobId,
                    mJobKind,
                    etMessage.text.toString().trim()
                )
                etMessage.setText("")
            }
            R.id.ivTimelineFilterDesc -> {
                ivTimelineFilterDesc.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                ivTimelineFilterAsc.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorCheckBoxNormal
                    )
                )

                //Call api
                mTimeLineRequestModel.sortOrder = "desc"
                mJobDetailViewModel.getJobTimeLine(mJobId, mTimeLineRequestModel)
            }

            R.id.ivTimelineFilterAsc -> {
                ivTimelineFilterAsc.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                ivTimelineFilterDesc.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorCheckBoxNormal
                    )
                )

                //Call api
                mTimeLineRequestModel.sortOrder = "asc"
                mJobDetailViewModel.getJobTimeLine(mJobId, mTimeLineRequestModel)
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

    override fun onItemClick(jobMediaList: JobMediaList, mType: Int) {
        if (jobMediaList.subKind == "dual") {
            (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                fragment = BeforeAfterPhotoDetailFragment.newInstance(jobMediaList, mType),
                containerViewId = R.id.flFragContainerMain,
                enterAnimation = R.animator.slide_right_in,
                popExitAnimation = R.animator.slide_right_out
            )
        } else {
            (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                fragment = PhotoDetailFragment.newInstance(jobMediaList),
                containerViewId = R.id.flFragContainerMain,
                enterAnimation = R.animator.slide_right_in,
                popExitAnimation = R.animator.slide_right_out
            )
        }
    }

    override fun onVisibilityClick(jobMediaList: JobMediaList, mType: Int) {
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = MediaVisibilityFragment.newInstance(jobMediaList, jobMediaList.kind, mType),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
    }

    override fun onBeforeAfterClick() {
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = BeforeAfterPhotoDetailFragment(),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
    }

    override fun onCommentsMediaClick(conversationList: ConversationList) {
        val jobMediaList = JobMediaList()
        jobMediaList.jobId = conversationList.jobId
        jobMediaList.id = conversationList.mediaId.id
        jobMediaList.medias = conversationList.mediaId.medias
        jobMediaList.kind = conversationList.mediaId.kind

        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = PhotoDetailFragment.newInstance(jobMediaList),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
    }

    override fun onTimelineClick(media: Media) {
        val jobMediaList = JobMediaList()
        jobMediaList.jobId = mJobId
        jobMediaList.id = media.id
        jobMediaList.medias = media.medias!!
        jobMediaList.kind = media.kind

        if (media.subKind == "dual") {
            (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                fragment = BeforeAfterPhotoDetailFragment.newInstance(jobMediaList, 0),
                containerViewId = R.id.flFragContainerMain,
                enterAnimation = R.animator.slide_right_in,
                popExitAnimation = R.animator.slide_right_out
            )
        } else {
            (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                fragment = PhotoDetailFragment.newInstance(jobMediaList),
                containerViewId = R.id.flFragContainerMain,
                enterAnimation = R.animator.slide_right_in,
                popExitAnimation = R.animator.slide_right_out
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Activity.RESULT_OK == resultCode && 1234 == requestCode) {
            when (intent?.getIntExtra(INTENT_SUBMIT, 0)) {
                AlertDialogFragment.VALUE_TRUE -> {
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mJobDetailViewModel.deleteJob(mJobId)
                    } else {
                        mJobDetailViewModel.deleteJobFromLocal(mLocalId, mJobId)
                    }
                }
            }

        } else if (Activity.RESULT_OK == resultCode && 2234 == requestCode) {
            mTimeLineRequestModel = intent?.getParcelableExtra(BUNDLE_FILTER_DATA)!!

            //Call api
            mJobDetailViewModel.getJobTimeLine(mJobId, mTimeLineRequestModel)
        }
    }

    private val mGetUpdateDataBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                //Call api
                try {
                    if (GeneralFunctions.isInternetConnected(requireContext())) {
                        mJobDetailViewModel.getJobMedia(mJobId, mLocalId, mJobKind, mSortBy)
                        mJobDetailViewModel.getJobDetail(mJobId, false)
                    } else {
                        mJobDetailViewModel.getMediaFromLocal(mLocalId, mSortBy, mJobKind, mJobId)
                        mJobDetailViewModel.getJobDetailFromLocal(mLocalId, mJobId)
                    }

                } catch (e: Exception) {
                }
            }
        }
    }

}