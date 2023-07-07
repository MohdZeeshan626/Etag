package com.max360group.cammax360.views.calender

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Events
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_DISPLAY
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_SERVER
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_SERVER_ISO
import com.max360group.cammax360.utils.Constants.TIME_LOCAL
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.utils.MyCustomLoader
import com.max360group.cammax360.viewmodels.CalenderViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.calender.CalendarUtils.daysInMonthArray
import com.max360group.cammax360.views.calender.CalendarUtils.monthYearFromDate
import com.max360group.cammax360.views.dialgofragments.CalenderListDialogFragment
import kotlinx.android.synthetic.main.activity_month_calender.*
import kotlinx.android.synthetic.main.toolbar.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class MonthlyActivity : BaseAppCompactActivity(), CalenderAdapter.CalenderListener,
    View.OnClickListener {
    private val mMyCustomLoader: MyCustomLoader by lazy { MyCustomLoader(this) }
    private val mCalenderAdapter by lazy {
        CalenderAdapter(this)
    }

    private val mCalenderViewModel by lazy {
        ViewModelProvider(this).get(CalenderViewModel::class.java)
    }

    private var mEventList = ArrayList<Events>()

    override val layoutId: Int
        get() = R.layout.activity_month_calender

    override val isMakeStatusBarTransparent: Boolean
        get() = false

    override fun init() {
        // Set Toolbar
        if (null != toolbar) {
            toolbar.setNavigationIcon(R.drawable.ic_back_white)
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }

        // Set toolbar
        toolbar.navigationIcon =
            ContextCompat.getDrawable(this, R.drawable.ic_back_primary)
        ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)
        ivToolbarRightIcon.visibility=View.GONE
        ivToolbarRightIconBell.visibility=View.GONE

        Glide
            .with(this)
            .load(ApplicationGlobal.organisationLogo)
            .placeholder(R.drawable.ic_mimi_logo)
            .into(ivToolbarLeftIcon)

        //Set adapter
        calendarRecyclerView.adapter = mCalenderAdapter
        CalendarUtils.selectedDate = LocalDate.now()

        //Set click listener
        btnOnNext.setOnClickListener(this)
        btnOnBack.setOnClickListener(this)
        btnWeekly.setOnClickListener(this)
        btnDay.setOnClickListener(this)
        btnWeekly.setOnClickListener(this)

        //Call api
        Event.eventsList.clear()
        Event.jobsList.clear()
        mCalenderViewModel.getEvents()
        mCalenderViewModel.onGetEventsData().observe(this, androidx.lifecycle.Observer {
            if (it.isNotEmpty()) {
                mEventList.clear()
                Event.jobsList.clear()
                Event.jobsList.addAll(it)
                mEventList.addAll(it)
                getAllEvents(it)
            }
        })

        mCalenderViewModel.onLoadEvent().observe(this, androidx.lifecycle.Observer {
            if (it) {
                mMyCustomLoader.showProgressDialog()
            } else {
                mMyCustomLoader.dismissProgressDialog()
            }
        })
    }

    override val navHostFragment: NavHostFragment?
        get() = null

    private fun getAllEvents(mEvent: List<Events>) {
        Event.eventsList.clear()
        for (mEvent in mEventList) {

            for (list in getDatesBetween(mEvent.startDt, mEvent.endDt)) {
                val mDate =
                    GeneralFunctions.changeUtcToLocal(
                        list, DATE_FORMAT_SERVER_ISO, DATE_FORMAT_SERVER
                    )

                val mTime =
                    GeneralFunctions.changeUtcToLocal(
                        list, DATE_FORMAT_SERVER_ISO, TIME_LOCAL
                    )

                //Add event
                val localDate = LocalDate.parse(mDate)
                val localTime = LocalTime.parse(mTime)
                val newEvent = Event(
                    mEvent.title, localDate, localTime, mEvent.id,
                    GeneralFunctions.changeDateFormat(
                        mEvent.startDt,
                        DATE_FORMAT_SERVER_ISO,
                        DATE_FORMAT_DISPLAY
                    ) + "-"
                            + GeneralFunctions.changeDateFormat(
                        mEvent.endDt,
                        DATE_FORMAT_SERVER_ISO,
                        DATE_FORMAT_DISPLAY
                    )
                )
                Event.eventsList.add(newEvent)
            }
        }
        setMonthView()
    }

    private fun setMonthView() {
        monthYearTV!!.text = monthYearFromDate(
            CalendarUtils.selectedDate!!
        )
        val daysInMonth = daysInMonthArray()
        //Update adapter
        mCalenderAdapter.updateData(daysInMonth, Event.eventsList)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 7)
        calendarRecyclerView.layoutManager = layoutManager
    }

    private fun previousMonthAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.minusMonths(1)
        setMonthView()
    }

    private fun nextMonthAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.plusMonths(1)
        setMonthView()
    }

    override fun onResume() {
        super.onResume()
        setMonthView()
    }

    override fun onItemClick(position: Int, date: LocalDate, events: Int) {
        if (date != null) {
            CalendarUtils.selectedDate = date
            setMonthView()
        }
        if (events != 0) {
            CalenderListDialogFragment.newInstance(mEventList, date.toString())
                .show(supportFragmentManager, "")
        }
    }

    private fun getDatesBetween(dateString1: String, dateString2: String): List<String> {
        val dates = ArrayList<String>()
        val input = SimpleDateFormat(DATE_FORMAT_SERVER_ISO, Locale.getDefault())
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = input.parse(dateString1)
            date2 = input.parse(dateString2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        while (!cal1.after(cal2)) {
            val output = SimpleDateFormat(DATE_FORMAT_SERVER_ISO, Locale.getDefault())
            dates.add(output.format(cal1.time))
            cal1.add(Calendar.DATE, 1)
        }
        return dates
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnOnBack -> {
                previousMonthAction()
            }
            R.id.btnOnNext -> {
                nextMonthAction()
            }
            R.id.btnWeekly -> {
                val intent = Intent(this, WeekViewActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.btnDay -> {
                val intent = Intent(this, DailyCalendarActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}