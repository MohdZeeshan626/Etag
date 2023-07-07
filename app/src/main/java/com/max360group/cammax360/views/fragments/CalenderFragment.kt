package com.max360group.cammax360.views.fragments


import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.github.tibolte.agendacalendarview.CalendarPickerController
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent
import com.github.tibolte.agendacalendarview.models.CalendarEvent
import com.github.tibolte.agendacalendarview.models.DayItem
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Events
import com.max360group.cammax360.repository.room.CamMaxRoomDatabase
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_SERVER_ISO
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.CalenderViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import kotlinx.android.synthetic.main.fragment_calender.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class CalenderFragment : BaseFragment(), CalendarPickerController {
    companion object {
        const val BUNDLE_FROM = "mFROM"
        const val BUNDLE_NAVIGATION = 0
        const val BUNDLE_NORMAl_VIEWS = 1

        fun newInstance(mFrom: Int = 1): CalenderFragment {
            val takePhotosFragment = CalenderFragment()
            val bundle = Bundle()
            bundle.putInt(BUNDLE_FROM, mFrom)
            takePhotosFragment.arguments = bundle
            return takePhotosFragment
        }
    }

    private val mCalenderViewModel by lazy {
        ViewModelProvider(this).get(CalenderViewModel::class.java)
    }

    private var mFrom = 0
    private var mEventList = ArrayList<Events>()

    override val layoutId: Int
        get() = R.layout.fragment_calender


    override fun init(savedInstanceState: Bundle?) {
        //Get arguments
        mFrom = arguments?.getInt(AccountsFragment.BUNDLE_FROM, 1)!!

        if (mFrom == AccountsFragment.BUNDLE_NAVIGATION) {
            toolbar.visibility = View.GONE

        } else {
            // Set toolbar
            toolbar.navigationIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)
            ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)
        }

        //Call api
        mCalenderViewModel.getEvents()

    }

    override val viewModel: BaseViewModel?
        get() = mCalenderViewModel

    override fun observeProperties() {
        mCalenderViewModel.onLoadEvent().observe(this, androidx.lifecycle.Observer {
            if (it) {
                pbBar.visibility = View.VISIBLE
            } else {
                pbBar.visibility = View.GONE
            }
        })
        mCalenderViewModel.onGetEventsData().observe(this, androidx.lifecycle.Observer {
            if (it.isNotEmpty()) {
                mEventList.clear()
                mEventList.addAll(it)
                getAllEvents(it)
            }

        })
    }

    private fun getAllEvents(mEvent: List<Events>) {
        val minDate = Calendar.getInstance()
        val maxDate = Calendar.getInstance()

        //Set calender min max date
        minDate.add(Calendar.YEAR, -2)
        maxDate.add(Calendar.YEAR, 5)

        val eventList = ArrayList<CalendarEvent>()
        mockList(eventList, mEvent)
        agenda_calendar_view.init(eventList, minDate, maxDate, Locale.getDefault(), this)

    }

    private fun mockList(calenderEventList: ArrayList<CalendarEvent>, mEventList: List<Events>) {
        for (mEvent in mEventList) {
            val mStartYear =
                GeneralFunctions.changeDateFormat(
                    mEvent.startDt, DATE_FORMAT_SERVER_ISO, "yyyy"
                )

            val mStartMonth =
                GeneralFunctions.changeDateFormat(
                    mEvent.startDt, DATE_FORMAT_SERVER_ISO, "M"
                )
            val mStartDate =
                GeneralFunctions.changeDateFormat(
                    mEvent.startDt, DATE_FORMAT_SERVER_ISO, "d"
                )

            val mEndYear =
                GeneralFunctions.changeDateFormat(
                    mEvent.endDt, DATE_FORMAT_SERVER_ISO, "yyyy"
                )
            val mEndMonth =
                GeneralFunctions.changeDateFormat(
                    mEvent.endDt, DATE_FORMAT_SERVER_ISO, "M"
                )
            val mEndDate =
                GeneralFunctions.changeDateFormat(
                    mEvent.endDt, DATE_FORMAT_SERVER_ISO, "d"
                )

            //Set start and end events time in calender
            val startEventDate = Calendar.getInstance()
            val endEventDate = Calendar.getInstance()
            startEventDate.set(mStartYear.toInt(), mStartMonth.toInt(), mStartDate.toInt())
            endEventDate.set(mEndYear.toInt(), mEndMonth.toInt(), mEndDate.toInt())

            //Get random color for events
            val typedValue = TypedValue()
            (requireContext() as BaseAppCompactActivity).theme.resolveAttribute(
                R.attr.primaryTextColor,
                typedValue,
                true
            )
            val color = typedValue.data

            val eventCalender = BaseCalendarEvent(
                mEvent.title, mEvent.id, mEvent.title,
                color, startEventDate, endEventDate, true
            )
            calenderEventList.add(eventCalender)
        }

    }


    override fun onDaySelected(dayItem: DayItem?) {
    }

    override fun onEventSelected(event: CalendarEvent?) {
        (event as BaseCalendarEvent).description
        val mData = CamMaxRoomDatabase.getDatabase(requireContext()).jobsDao()
            .getSingleJobByServerId(event.description)

        if (mData!=null){
            (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                fragment = JobDetailsFragment.newInstance(
                    event!!.description,
                    mData.jobLocalId.toString()
                ),
                containerViewId = R.id.flFragContainerMain,
                enterAnimation = R.animator.slide_right_in,
                popExitAnimation = R.animator.slide_right_out
            )
        }
    }

    override fun onScrollToDate(calendar: Calendar?) {

    }
}