package com.max360group.cammax360.views.calender

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Events
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.NotifyActivity
import com.max360group.cammax360.views.calender.CalendarUtils.selectedDate
import com.max360group.cammax360.views.dialgofragments.CalenderListDialogFragment
import kotlinx.android.synthetic.main.activity_daily_calendar.*
import kotlinx.android.synthetic.main.toolbar.*
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.*

class DailyCalendarActivity : BaseAppCompactActivity(), View.OnClickListener,
    DayEventListAdapter.HoursListener {

    private val mHoursAdapter by lazy {
        HoursAdapter(this)
    }

    override val layoutId: Int
        get() = R.layout.activity_daily_calendar

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
        hourListView.adapter = mHoursAdapter

        //Set click listener
        btnOnBack.setOnClickListener(this)
        btnOnNext.setOnClickListener(this)
        btnWeekly.setOnClickListener(this)
        btnMonth.setOnClickListener(this)
    }

    override val navHostFragment: NavHostFragment?
        get() = null

    override fun onResume() {
        super.onResume()
        setDayView()
    }

    private fun setDayView() {
        monthYearTV.text = CalendarUtils.monthDayFromDate(selectedDate!!)
        val dayOfWeek: String =
            selectedDate!!.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        dayOfWeekTV.text = dayOfWeek
        setHourAdapter()
    }

    private fun setHourAdapter() {
        mHoursAdapter.updateData(hourEventList())
    }

    private fun hourEventList(): ArrayList<HourEvent> {
        val list: ArrayList<HourEvent> = ArrayList<HourEvent>()
        for (hour in 0..23) {
            val time = LocalTime.of(hour, 0)
            val events = Event.eventsForDateAndTime(selectedDate!!, time)
            val hourEvent = HourEvent(time, events)
            list.add(hourEvent)
        }
        return list
    }

    private fun previousDayAction() {
        selectedDate = selectedDate!!.minusDays(1)
        setDayView()
    }

    private fun nextDayAction() {
        selectedDate = selectedDate!!.plusDays(1)
        setDayView()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnOnBack -> {
                previousDayAction()
            }
            R.id.btnOnNext -> {
                nextDayAction()
            }
            R.id.btnWeekly -> {
                startActivity(Intent(this, WeekViewActivity::class.java))
                finish()
            }
            R.id.btnMonth -> {
                startActivity(Intent(this, MonthlyActivity::class.java))
                finish()
            }
        }
    }

    override fun onEventClick(id: String) {
        val intent = Intent(this, NotifyActivity::class.java)
        intent.putExtra(NotifyActivity.BUNDLE_JOb_Id,id)
        startActivity(intent)
    }
}