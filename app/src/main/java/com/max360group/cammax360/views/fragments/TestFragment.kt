package com.max360group.cammax360.views.fragments

import android.os.Bundle
import com.alamkanak.weekview.DateTimeInterpreter
import com.max360group.cammax360.R
import com.max360group.cammax360.viewmodels.BaseViewModel
import kotlinx.android.synthetic.main.test_fragment.*
import java.text.SimpleDateFormat
import java.util.*
class TestFragment : BaseFragment() {
    override val layoutId: Int
        get() = R.layout.test_fragment

    override fun init(savedInstanceState: Bundle?) {
        // the week view. This is optional.
        setupDateTimeInterpreter(false)
    }

    override val viewModel: BaseViewModel?
        get() =null

    override fun observeProperties() {

    }

    private fun setupDateTimeInterpreter(shortDate: Boolean) {
        weekView.dateTimeInterpreter = object : DateTimeInterpreter {
            override fun interpretDate(date: Calendar): String? {
                val weekdayNameFormat = SimpleDateFormat("EEE", Locale.getDefault())
                var weekday: String = weekdayNameFormat.format(date.getTime())
                val format = SimpleDateFormat(" M/d", Locale.getDefault())

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate) weekday = weekday[0].toString()
                return weekday.toUpperCase() + format.format(date.getTime())
            }

            override fun interpretTime(hour: Int): String {
                return if (hour > 11) (hour - 12).toString() + " PM" else if (hour == 0) "12 AM" else "$hour AM"
            }
        }
    }
}