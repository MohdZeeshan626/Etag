package com.max360group.cammax360.views.dialgofragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Events
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_SERVER
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_SERVER_ISO
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.NotifyActivity
import com.max360group.cammax360.views.activities.NotifyActivity.Companion.BUNDLE_JOb_Id
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.CalenderEventListAdapter
import com.max360group.cammax360.views.calender.WeekViewActivity
import com.max360group.cammax360.views.fragments.JobDetailsFragment
import kotlinx.android.synthetic.main.dialog_calender_event_list.*
import java.util.*
import kotlin.collections.ArrayList

class CalenderListDialogFragment : BaseDialogFragment(), View.OnClickListener,
    CalenderEventListAdapter.CalenderEventListener {

    companion object {
        const val BUNDLE_DATE = "date"
        const val BUNDLE_EVENT_LIST = "eventList"

        fun newInstance(mEventList: ArrayList<Events>, date: String): CalenderListDialogFragment {
            val takePhotosFragment = CalenderListDialogFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(BUNDLE_EVENT_LIST, mEventList)
            bundle.putString(BUNDLE_DATE, date)
            takePhotosFragment.arguments = bundle
            return takePhotosFragment
        }
    }

    private var mEventList = ArrayList<Events>()
    private var mFilterEventList = ArrayList<Events>()
    private var mDate = ""

    private val mCalenderEventListAdapter by lazy {
        CalenderEventListAdapter(this)
    }

    override val isFullScreenDialog: Boolean
        get() = false

    override val layoutId: Int
        get() = R.layout.dialog_calender_event_list

    override fun init() {
        //Get arguments
        mEventList =
            arguments?.getParcelableArrayList<Events>(BUNDLE_EVENT_LIST) as ArrayList<Events>
        mDate = arguments?.getString(BUNDLE_DATE).toString()

        for (i in mEventList.indices) {

            //event start date
            val mStartDate = GeneralFunctions.changeUtcToLocal(
                mEventList[i].startDt,
                DATE_FORMAT_SERVER_ISO,
                DATE_FORMAT_SERVER
            )
            //event end date
            val mEndDate =
                GeneralFunctions.changeUtcToLocal(
                    mEventList[i].endDt,
                    DATE_FORMAT_SERVER_ISO,
                    DATE_FORMAT_SERVER
                )

            //Check selected date is between the start and end date to add in list
            if (between(
                    GeneralFunctions.stringToDate(mDate, DATE_FORMAT_SERVER),
                    GeneralFunctions.stringToDate(
                        mStartDate,
                        DATE_FORMAT_SERVER
                    ),
                    GeneralFunctions.stringToDate(mEndDate, DATE_FORMAT_SERVER)
                )
            ) {
                mFilterEventList.add(mEventList[i])
            }
        }

        //update adapter
        if (mFilterEventList.isEmpty()) {
            tvNoData.visibility = View.VISIBLE
        }
        mCalenderEventListAdapter.updateFunction(mFilterEventList)

        //Set adapter
        rvEvents.adapter = mCalenderEventListAdapter

        //Set click listener
        ivCancel.setOnClickListener(this)
    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {

    }

    private fun between(date: Date?, dateStart: Date?, dateEnd: Date?): Boolean {
        return if (date != null && dateStart != null && dateEnd != null) {
            (date.after(dateStart) && date.before(dateEnd)) || (date == dateStart) || (date == dateEnd)
        } else false
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivCancel -> {
                dismiss()
            }
        }
    }

    override fun onItemClick(id: String) {
        val intent = Intent(requireContext(), NotifyActivity::class.java)
        intent.putExtra(BUNDLE_JOb_Id,id)
        startActivity(intent)
        dismiss()
    }
}