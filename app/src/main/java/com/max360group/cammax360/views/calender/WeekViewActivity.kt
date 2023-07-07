package com.max360group.cammax360.views.calender

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.NotifyActivity
import com.max360group.cammax360.views.calender.CalendarUtils.daysInWeekArray
import com.max360group.cammax360.views.calender.CalendarUtils.monthYearFromDate
import kotlinx.android.synthetic.main.activity_week_view.*
import kotlinx.android.synthetic.main.toolbar.*
import java.time.LocalDate

class WeekViewActivity : BaseAppCompactActivity(), CalenderAdapter.CalenderListener,
    View.OnClickListener, EventsListingAdapter.EventsListingListener {
    private val mCalenderAdapter by lazy {
        CalenderAdapter(this)
    }

    private val mEventsListingAdapter by lazy {
        EventsListingAdapter(this)
    }

    override val layoutId: Int
        get() = R.layout.activity_week_view

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
        ivToolbarRightIconBell.visibility=View.GONE
        ivToolbarRightIcon.visibility=View.GONE

        Glide
            .with(this)
            .load(ApplicationGlobal.organisationLogo)
            .placeholder(R.drawable.ic_mimi_logo)
            .into(ivToolbarLeftIcon)

        //Set adapter
        calendarRecyclerView.adapter = mCalenderAdapter
        rvEventList.adapter = mEventsListingAdapter

        //Set click listener
        btnOnBack.setOnClickListener(this)
        btnOnNext.setOnClickListener(this)
        btnMonth.setOnClickListener(this)
        btnDay.setOnClickListener(this)

        setWeekView()
    }

    override val navHostFragment: NavHostFragment?
        get() = null

    private fun setWeekView() {
        monthYearTV.text = monthYearFromDate(CalendarUtils.selectedDate!!)
        val days = daysInWeekArray(CalendarUtils.selectedDate!!)

        //Update adapter
        mCalenderAdapter.updateData(days, Event.eventsList)
        val layoutManager =
            GridLayoutManager(applicationContext, 7)
        calendarRecyclerView.layoutManager = layoutManager
        setEventAdapter()
    }

    private fun previousWeekAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.minusWeeks(1)
        setWeekView()
    }

    private fun nextWeekAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.plusWeeks(1)
        setWeekView()
    }


    override fun onResume() {
        super.onResume()
        setEventAdapter()
    }

    private fun setEventAdapter() {
        val dailyEvents = Event.eventsForDate(
            CalendarUtils.selectedDate!!
        )
        mEventsListingAdapter.updateData(dailyEvents)
    }

    override fun onItemClick(position: Int, date: LocalDate, size: Int) {
        CalendarUtils.selectedDate = date
        setWeekView()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnOnNext -> {
                nextWeekAction()
            }
            R.id.btnOnBack -> {
                previousWeekAction()
            }
            R.id.btnMonth -> {
                startActivity(Intent(this, MonthlyActivity::class.java))
                finish()
            }
            R.id.btnDay -> {
                startActivity(Intent(this, DailyCalendarActivity::class.java))
                finish()
            }
        }
    }

    override fun onItemClick(id: String) {
        val intent = Intent(this, NotifyActivity::class.java)
        intent.putExtra(NotifyActivity.BUNDLE_JOb_Id, id)
        startActivity(intent)
    }
}