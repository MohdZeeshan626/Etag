package com.max360group.cammax360.views.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.AccountList
import com.max360group.cammax360.repository.models.Jobs
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.JobsViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.HomeActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.NewJobsAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.dialgofragments.DatePickerDialogFragment
import com.max360group.cammax360.views.interfaces.CreateNewJobListener
import kotlinx.android.synthetic.main.fragment_create_new_job.*
import kotlinx.android.synthetic.main.time_line_dialog.*
import kotlinx.android.synthetic.main.toolbar.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet


class CreateNewJobFragment : BaseFragment(), View.OnClickListener, CreateNewJobListener {

    companion object {
        const val BUNDLE_LAT = "latitude"
        const val BUNDLE_LONGITUDE = "longitude"
        const val BUNDLE_USERS = "usersList"
        const val BUNDLE_ADD = "add"
        const val BUNDLE_INVITE = "invite"
        const val BUNDLE_FROM = "from"

        fun newInstance(
            latitude: Double,
            longitude: Double,
            from: String
        ): CreateNewJobFragment {
            val mFragment = CreateNewJobFragment()
            val mBundle = Bundle()
            mBundle.putDouble(BUNDLE_LAT, latitude)
            mBundle.putDouble(BUNDLE_LONGITUDE, longitude)
            mBundle.putString(BUNDLE_FROM, from)
            mFragment.arguments = mBundle
            return mFragment
        }
    }

    private val mJobsViewModel by lazy {
        ViewModelProvider(this).get(JobsViewModel::class.java)
    }

    private val mNewJobsAdapter by lazy {
        NewJobsAdapter(this)
    }

    private var mUsersList = HashSet<AccountList>()
    private var mLatitude = 0.0
    private var mLongitude = 0.0
    private var mDateFrom = 0// 0 for start 1 for end date
    private var mServerStartDate = ""
    private var mServerEndDate = ""
    private var dobYear: Int = 0
    private var dobMonth: Int = 0
    private var dobDay: Int = 0
    private var country = ""
    private var mFrom = ""

    override val layoutId: Int
        get() = R.layout.fragment_create_new_job

    override fun init(savedInstanceState: Bundle?) {
        //Set toolbar
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)
        ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)

        Glide
            .with(requireContext())
            .load(ApplicationGlobal.organisationLogo)
            .placeholder(R.drawable.ic_mimi_logo)
            .into(ivToolbarLeftIcon)

        //Get arguments
        arguments?.let { bundle ->
            mLatitude = bundle.getDouble(BUNDLE_LAT, 0.0)
            mLongitude = bundle.getDouble(BUNDLE_LONGITUDE, 0.0)
            mFrom = bundle.getString(BUNDLE_FROM).toString()
        }

        //Set selected property address
        etJobTitle.setText(SelectLocationFragment.placeName)
        etJobAddress.setText(SelectLocationFragment.placeName)
        etJobState.setText(SelectLocationFragment.state)
        etJobCity.setText(SelectLocationFragment.city)

        if (SelectLocationFragment.placeName.isBlank()) {
            //Get Address detail
            initAddressDetail(mLatitude, mLongitude)
        }

        //Set adapter
        rvJobsList.adapter = mNewJobsAdapter
        //Remove duplicate values
        mUsersList.addAll(ApplicationGlobal.mJobUsersList)

        ApplicationGlobal.mJobUsersList.clear()
        ApplicationGlobal.mJobUsersList.addAll(mUsersList)
        //Update adapter
        mNewJobsAdapter.updateData(ApplicationGlobal.mJobUsersList)

        //Set default date from start to end
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, +7)
        val date = calendar.time
        val format = SimpleDateFormat(Constants.DATE_FORMAT_SERVER_ISO)
        mServerEndDate = format.format(date)
        mServerStartDate = ApplicationGlobal.currentDate

        etStartDate.text = GeneralFunctions.changeDateFormat(
            ApplicationGlobal.currentDate, Constants.DATE_FORMAT_SERVER_ISO,
            Constants.DATE_FORMAT_SERVER
        )
        etEndDate.text = GeneralFunctions.changeDateFormat(
            format.format(date), Constants.DATE_FORMAT_SERVER_ISO,
            Constants.DATE_FORMAT_SERVER
        )

        //Set click listener
        btnAddMembers.setOnClickListener(this)
        btnInviteNew.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        etStartDate.setOnClickListener(this)
        etEndDate.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)
    }

    override val viewModel: BaseViewModel?
        get() = mJobsViewModel

    override fun observeProperties() {
        mJobsViewModel.onJobCreated().observe(this, androidx.lifecycle.Observer {
            val intent = Intent(requireContext(), HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        })
    }

    private fun initAddressDetail(mLatitude: Double, mLongitude: Double) {
        try {
            val gcd = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> = gcd.getFromLocation(mLatitude, mLongitude, 1)
            if (addresses.isNotEmpty()) {
                etJobAddress.setText(
                    GeneralFunctions.getAddress(
                        mLatitude,
                        mLongitude,
                        requireContext()
                    )
                )
                etJobTitle.setText(
                    GeneralFunctions.getAddress(
                        mLatitude,
                        mLongitude,
                        requireContext()
                    )
                )
                etJobState.setText(addresses[0].adminArea)
                if (addresses[0].locality != null) {
                    etJobCity.setText(addresses[0].locality)
                } else {
                    etJobCity.setText(addresses[0].adminArea)
                }

                country = addresses[0].countryName
            } else {
                // do your stuff
            }
        } catch (e: Exception) {

        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnAddMembers -> {
                if (mFrom == BUNDLE_INVITE) {
                    (activityContext as BaseAppCompactActivity).onBackPressed()
                    (activityContext as BaseAppCompactActivity).onBackPressed()
                } else {
                    (activityContext as BaseAppCompactActivity).onBackPressed()
                }
            }

            R.id.btnInviteNew -> {
                if (mFrom == BUNDLE_INVITE) {
                    (activityContext as BaseAppCompactActivity).onBackPressed()
                } else {
                    (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                        fragment = InviteMembersPermissionsFragment.newInstance(
                            mLatitude,
                            mLongitude
                        ),
                        containerViewId = R.id.flFragContainerMain,
                        enterAnimation = R.animator.slide_right_in,
                        popExitAnimation = R.animator.slide_right_out
                    )
                }
            }
            R.id.btnSave -> {
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mJobsViewModel.createJob(
                        etJobTitle.text.toString().trim(),
                        etJobAddress.text.toString().trim(),
                        etJobState.text.toString().trim(),
                        etJobCity.text.toString().trim(),
                        mLatitude,
                        mLongitude,
                        mServerStartDate,
                        mServerEndDate,
                        ApplicationGlobal.mJobUsersList,
                        country,
                        SelectLocationFragment.propertyId,
                        SelectLocationFragment.propertyUnitId
                    )
                } else {
                    mJobsViewModel.createJobInLocal(
                        etJobTitle.text.toString().trim(),
                        etJobAddress.text.toString().trim(),
                        etJobState.text.toString().trim(),
                        etJobCity.text.toString().trim(),
                        mLatitude,
                        mLongitude,
                        mServerStartDate,
                        mServerEndDate,
                        ApplicationGlobal.mJobUsersList,
                        country,
                        SelectLocationFragment.propertyId,
                        SelectLocationFragment.propertyUnitId
                    )
                }

            }
            R.id.etStartDate -> {
                mDateFrom = 0
                val c = Calendar.getInstance()
                val mYear = c[Calendar.YEAR]
                val mMonth = c[Calendar.MONTH]
                val mDay = c[Calendar.DAY_OF_MONTH]
                val calendarMinDate = Calendar.getInstance()
                calendarMinDate.set(mYear, mMonth, mDay)

                val calendarMaxDate = Calendar.getInstance()
                calendarMaxDate.add(Calendar.YEAR, 24)

                showDatePicker(
                    dobYear, dobMonth, dobDay, calendarMinDate.timeInMillis,
                    calendarMaxDate.timeInMillis
                )

            }
            R.id.etEndDate -> {
                mDateFrom = 1
                if (mServerStartDate.isBlank()) {
                    showMessage(null, getString(R.string.st_select_start_date))
                } else {
                    val c = Calendar.getInstance()
                    val calendarMinDate = Calendar.getInstance()
                    calendarMinDate.set(dobYear, dobMonth, dobDay + 1)

                    val calendarMaxDate = Calendar.getInstance()
                    calendarMaxDate.add(Calendar.YEAR, 24)

                    showDatePicker(
                        dobYear, dobMonth, dobDay, calendarMinDate.timeInMillis,
                        calendarMaxDate.timeInMillis
                    )

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

    private fun showDatePicker(
        year: Int, month: Int, day: Int, calenderMinDate: Long,
        calendarMaxDate: Long
    ) {
        val datePickerDialogFragment = DatePickerDialogFragment
            .newInstance(year, month, day, calenderMinDate, calendarMaxDate)
        datePickerDialogFragment.setCallBack(onDateSetListener)
        datePickerDialogFragment.show(childFragmentManager, "datePicker")
    }

    private val onDateSetListener: DatePickerDialog.OnDateSetListener = DatePickerDialog
        .OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            dobYear = selectedYear
            dobMonth = selectedMonth
            dobDay = selectedDay
            initStartTime(mDateFrom)
        }

    private fun initStartTime(mDateFrom: Int) {
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText(getString(R.string.st_job_start_time))
                .setInputMode(INPUT_MODE_CLOCK)
                .build()

        picker.show(childFragmentManager, "MainActivity")

        picker.addOnPositiveButtonClickListener {
            var mHour = picker.hour.toString()
            var mMin = picker.minute.toString()
            if (mHour.toInt() < 10) {
                mHour = "0$mHour"
            }
            if (mMin.toInt() < 10) {
                mMin = "0$mMin"
            }
            if (mDateFrom == 0) {
                "$dobDay-${(dobMonth + 1)}-$dobYear, $mHour:$mMin".also { etStartDate.text = it }
                mServerStartDate = GeneralFunctions
                    .changeDateFormat(
                        "$dobYear-${(dobMonth + 1)}-$dobDay $mHour:$mMin",
                        Constants.DATE_FORMAT, Constants.DATE_FORMAT_SERVER_ISO
                    )
            } else {
                "$dobDay-${(dobMonth + 1)}-$dobYear, $mHour:$mMin".also { etEndDate.text = it }
                mServerEndDate = GeneralFunctions
                    .changeDateFormat(
                        "$dobYear-${(dobMonth + 1)}-$dobDay $mHour:$mMin",
                        Constants.DATE_FORMAT, Constants.DATE_FORMAT_SERVER_ISO
                    )
            }


        }

    }

    override fun onEditPermissions(mJobs: Jobs, userName: String, position: Int) {
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = EditMembersPermissionsFragment.newInstance(
                mJobs,
                userName,
                EditMembersPermissionsFragment.BUNDLE_ADD
            ),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
    }

}