package com.max360group.cammax360.views.dialgofragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.TimeLineItems
import com.max360group.cammax360.repository.models.TimeLineRequestModel
import com.max360group.cammax360.repository.models.UserX
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.*
import com.max360group.cammax360.views.adapters.TimelineFilterAdapter
import kotlinx.android.synthetic.main.forgot_dialog.btnSubmit
import kotlinx.android.synthetic.main.save_role_dialog.ivCancel
import kotlinx.android.synthetic.main.time_line_dialog.*

class TimeLineFilterDialogFragment : BaseDialogFragment(), View.OnClickListener {

    companion object {
        const val BUNDLE_USERS = "users"
        const val BUNDLE_FILTER_DATA = "filterData"

        fun newInstance(mJobMemberList: ArrayList<UserX>): TimeLineFilterDialogFragment {
            val takePhotosFragment = TimeLineFilterDialogFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(BUNDLE_USERS, mJobMemberList)
            takePhotosFragment.arguments = bundle

            return takePhotosFragment
        }
    }

    private val mTimelineViewModel by lazy {
        ViewModelProvider(this).get(TimelineViewModel::class.java)
    }

    private val mTimelineItems by lazy {
        TimelineFilterAdapter(this)
    }

    private val mTimelineUserUpdates by lazy {
        TimelineFilterAdapter(this)
    }

    private val mTimelineUser by lazy {
        TimelineFilterAdapter(this)
    }

    private var mJobMemberList = ArrayList<UserX>()
    private var mMemberAdapterList = ArrayList<TimeLineItems>()
    private var mStartDate = ""
    private var mEndDate = ""

    override val isFullScreenDialog: Boolean
        get() = true

    override val layoutId: Int
        get() = R.layout.time_line_dialog

    override fun init() {
        //Get arguments
        mJobMemberList = arguments?.getParcelableArrayList<UserX>(BUNDLE_USERS) as ArrayList<UserX>

        //Add all type in list
        mMemberAdapterList.add(
            TimeLineItems(
                getString(R.string.st_all),
                true
            )
        )

        //add users in adapter list
        for (i in mJobMemberList.indices) {
            mMemberAdapterList.add(
                TimeLineItems(
                    mJobMemberList[i].userId.firstName,
                    true,
                    mJobMemberList[i].userId.id
                )
            )
        }

        //Set default date
        (GeneralFunctions.changeDateFormat(
            ApplicationGlobal.beforeMonthDate, Constants.DATE_FORMAT_SERVER_ISO,
            Constants.DATE_FORMAT_DISPLAY
        ) + "-" + GeneralFunctions.changeDateFormat(
            ApplicationGlobal.currentDate, Constants.DATE_FORMAT_SERVER_ISO,
            Constants.DATE_FORMAT_DISPLAY
        )).also { etDateRange.text = it }

        mStartDate = ApplicationGlobal.beforeMonthDate
        mEndDate = ApplicationGlobal.currentDate

        //Update users
        mTimelineUser.updateData(mMemberAdapterList)

        //Set click listener
        btnSubmit.setOnClickListener(this)
        ivCancel.setOnClickListener(this)
        etDateRange.setOnClickListener(this)

        //Set adapter
        rvItems.adapter = mTimelineItems
        rvUserUpdates.adapter = mTimelineUserUpdates
        rvUsers.adapter = mTimelineUser

        mTimelineViewModel.getItems()
        mTimelineViewModel.getUserUpdates()
    }

    override val viewModel: BaseViewModel?
        get() = mTimelineViewModel

    override fun observeProperties() {
        mTimelineViewModel.onGetItems().observe(this, Observer {
            mTimelineItems.updateData(it)
        })

        mTimelineViewModel.onGetUserUpdates().observe(this, Observer {
            mTimelineUserUpdates.updateData(it)
        })
    }

    private fun showDateRangePicker() {
        // Date Range Picker
        val picker = MaterialDatePicker.Builder.dateRangePicker().build()
        picker.show(requireActivity().supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            (GeneralFunctions.getDateFromMillis(it.first) + "-" +
                    GeneralFunctions.getDateFromMillis(it.second)).also { etDateRange.text = it }
            mStartDate =
                GeneralFunctions.getDateFromMillis(it.first, Constants.DATE_FORMAT_SERVER_ISO)
            mEndDate =
                GeneralFunctions.getDateFromMillis(it.second, Constants.DATE_FORMAT_SERVER_ISO)
        }
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSubmit -> {
                var mTimeLineRequestModel = TimeLineRequestModel()
                mTimeLineRequestModel.startDt = mStartDate
                mTimeLineRequestModel.endDt = mEndDate

                //Set items
                mTimeLineRequestModel.itemFilters.mediaPhotos =
                    mTimelineItems.getSelectedItems()[1].isChecked
                mTimeLineRequestModel.itemFilters.mediaVideos =
                    mTimelineItems.getSelectedItems()[1].isChecked
                mTimeLineRequestModel.itemFilters.documents =
                    mTimelineItems.getSelectedItems()[2].isChecked
                mTimeLineRequestModel.itemFilters.conversations =
                    mTimelineItems.getSelectedItems()[3].isChecked
                mTimeLineRequestModel.itemFilters.notes =
                    mTimelineItems.getSelectedItems()[4].isChecked
                mTimeLineRequestModel.itemFilters.comments =
                    mTimelineItems.getSelectedItems()[5].isChecked

                //Set user updates
                mTimeLineRequestModel.userUpdateFilters.permissions =
                    mTimelineUserUpdates.getSelectedItems()[1].isChecked
                mTimeLineRequestModel.userUpdateFilters.invitation =
                    mTimelineUserUpdates.getSelectedItems()[2].isChecked
                mTimeLineRequestModel.userUpdateFilters.termination =
                    mTimelineUserUpdates.getSelectedItems()[3].isChecked

                //Set users
                for (i in mTimelineUser.getSelectedItems().indices) {
                    //Skip the static item "all"
                    if (i != 0) {
                        if (mTimelineUser.getSelectedItems()[i].isChecked) {
                            mTimeLineRequestModel.userFilters!!.add(mTimelineUser.getSelectedItems()[i].id)
                        }
                    }
                }

                if (targetFragment != null) {
                    targetFragment!!.onActivityResult(
                        targetRequestCode,
                        Activity.RESULT_OK,
                        Intent().putExtra(
                            BUNDLE_FILTER_DATA, mTimeLineRequestModel
                        )
                    )
                }
                dismiss()

            }

            R.id.ivCancel -> {
                dismiss()
            }

            R.id.etDateRange -> {
                showDateRangePicker()
            }
        }
    }
}