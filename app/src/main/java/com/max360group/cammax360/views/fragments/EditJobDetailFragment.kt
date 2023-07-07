package com.max360group.cammax360.views.fragments

import android.app.DatePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.JobDetail
import com.max360group.cammax360.repository.models.UpdateJobDescriptionRequestModel
import com.max360group.cammax360.services.CheckInternetService
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_DISPLAY
import com.max360group.cammax360.utils.Constants.DATE_FORMAT_SERVER
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.EditJobsDetailViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.dialgofragments.DatePickerDialogFragment
import com.max360group.cammax360.views.fragments.SelectLocationFragment.Companion.BUNDLE_CREATE
import com.max360group.cammax360.views.fragments.SelectLocationFragment.Companion.BUNDLE_UPDATE
import kotlinx.android.synthetic.main.fragment_create_new_job.*
import kotlinx.android.synthetic.main.fragment_create_new_job.etEndDate
import kotlinx.android.synthetic.main.fragment_create_new_job.etJobAddress
import kotlinx.android.synthetic.main.fragment_create_new_job.etJobState
import kotlinx.android.synthetic.main.fragment_create_new_job.etJobTitle
import kotlinx.android.synthetic.main.fragment_create_new_job.etStartDate
import kotlinx.android.synthetic.main.fragment_edit_job_detail.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*


class EditJobDetailFragment : BaseFragment(), View.OnClickListener {

    companion object {
        const val BUNDLE_JOB_DATA = "jobData"
        const val BUNDLE_INTENT_EDIT_JOB = "editJob"
        const val BUNDLE_LATITUDE = "latitude"
        const val BUNDLE_LONGITUDE = "longitude"
        const val BUNDLE_PROPERTY_ID = "propertyId"
        const val BUNDLE_PROPERTY_UNIT_ID = "propertyUnitId"

        fun newInstance(
            mJobDetail: JobDetail?,
        ): EditJobDetailFragment {
            val mFragment = EditJobDetailFragment()
            val mBundle = Bundle()
            mBundle.putParcelable(BUNDLE_JOB_DATA, mJobDetail!!)
            mFragment.arguments = mBundle
            return mFragment
        }
    }

    private val mEditJobsDetailViewModel by lazy {
        ViewModelProvider(this).get(EditJobsDetailViewModel::class.java)
    }

    private var mJobDetail: JobDetail? = null
    private var mServerStartDate = ""
    private var mServerEndDate = ""
    private var dobYear: Int = 0
    private var dobMonth: Int = 0
    private var dobDay: Int = 0
    private var mDateFrom = 0// 0 for start 1 for end date
    private var mLatitude = 0.0
    private var mLongitude = 0.0
    private var country = ""
    private var propertyId: String = ""
    private var propertyUnitId: String = ""

    override val layoutId: Int
        get() = R.layout.fragment_edit_job_detail

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

        // Register receiver for sync data
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mUpdateLocalDataInServer,
                IntentFilter(CheckInternetService.INTENT_SYNC_DATA)
            )


        //Get arguments
        mJobDetail = arguments?.getParcelable(BUNDLE_JOB_DATA)

        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mUpdateDialogReceiver,
                IntentFilter(BUNDLE_INTENT_EDIT_JOB)
            )

        initSetData(mJobDetail)

        //Set click listener
        etStartDate.setOnClickListener(this)
        etEndDate.setOnClickListener(this)
        btnMap.setOnClickListener(this)
        btnSaveJob.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

    }

    override val viewModel: BaseViewModel?
        get() = mEditJobsDetailViewModel

    override fun observeProperties() {
        mEditJobsDetailViewModel.onUpdateJob().observe(this, androidx.lifecycle.Observer {
            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(JobDetailsFragment.BUNDLE_JOB_DETAIL_INTENT)
                )
        })
    }

    private fun initSetData(mJobDetail: JobDetail?) {
        etJobTitle.setText(mJobDetail!!.job.title)
        etJobAddress.setText(mJobDetail.job.address.formatted)
        etJobState.setText(mJobDetail.job.address.state)
        etCity.setText(mJobDetail.job.address.city)
        etStartDate.text = GeneralFunctions.changeDateFormat(
            mJobDetail.job.startDt,
            Constants.DATE_FORMAT_SERVER_ISO,
            DATE_FORMAT_DISPLAY
        )
        etEndDate.text = GeneralFunctions.changeDateFormat(
            mJobDetail.job.endDt,
            Constants.DATE_FORMAT_SERVER_ISO,
            DATE_FORMAT_DISPLAY
        )

        mServerStartDate = mJobDetail.job.startDt
        mServerEndDate = mJobDetail.job.endDt

        if (mJobDetail.job.propertyId != null && mJobDetail.job.propertyUnitId != null) {
            propertyId = mJobDetail.job.propertyId!!.toString()
            propertyUnitId = mJobDetail.job.propertyUnitId!!.toString()
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
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
                    calendarMinDate.set(dobYear, dobMonth, dobDay)

                    val calendarMaxDate = Calendar.getInstance()
                    calendarMaxDate.add(Calendar.YEAR, 24)

                    showDatePicker(
                        dobYear, dobMonth, dobDay, calendarMinDate.timeInMillis,
                        calendarMaxDate.timeInMillis
                    )

                }
            }
            R.id.btnSaveJob -> {
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mEditJobsDetailViewModel.updateJob(
                        mJobDetail!!.job.id,
                        etJobTitle.text.toString().trim(),
                        etJobAddress.text.toString().trim(),
                        etJobState.text.toString().trim(),
                        etCity.text.toString().trim(),
                        mLatitude,
                        mLongitude,
                        mServerStartDate,
                        mServerEndDate,
                        country,
                        propertyId,
                        propertyUnitId
                    )
                } else {
                    mEditJobsDetailViewModel.updateJobDetailInLocal(
                        mJobDetail!!.job.jobLocalId.toString(),
                        etJobTitle.text.toString().trim(),
                        etJobAddress.text.toString().trim(),
                        etJobState.text.toString().trim(),
                        etCity.text.toString().trim(),
                        mLatitude,
                        mLongitude,
                        mServerStartDate,
                        mServerEndDate,
                        country,
                        propertyId,
                        propertyUnitId
                    )
                }

            }

            R.id.btnMap -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = SelectLocationFragment.newInstance(BUNDLE_UPDATE),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
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

    private fun initAddressDetail(mLatitude: Double, mLongitude: Double) {
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

            etJobState.setText(addresses[0].adminArea)
            etCity.setText(addresses[0].subLocality)
            country = addresses[0].countryName
        } else {
            // do your stuff
        }
    }


    private fun showDatePicker(
        year: Int, month: Int, day: Int, calenderMinDate: Long,
        calendarMaxDate: Long
    ) {
        val datePickerDialogFragment = DatePickerDialogFragment
            .newInstance(year, month, day, calenderMinDate, calendarMaxDate)
        /**
         * Set Call back to capture selected date
         */
        datePickerDialogFragment.setCallBack(onDateSetListener)
        datePickerDialogFragment.show(childFragmentManager, "datePicker")
    }

    private val onDateSetListener: DatePickerDialog.OnDateSetListener = DatePickerDialog
        .OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            // Format date
            val displayDate = GeneralFunctions
                .changeDateFormat(
                    "$selectedYear-${(selectedMonth + 1)}-$selectedDay",
                    Constants.DATE_FORMAT_SERVER, Constants.DATE_FORMAT_DISPLAY
                )
            if (mDateFrom == 0) {
                etStartDate.text = displayDate
                mServerStartDate = GeneralFunctions
                    .changeDateFormat(
                        "$selectedYear-${(selectedMonth + 1)}-$selectedDay",
                        Constants.DATE_FORMAT_SERVER, Constants.DATE_FORMAT_SERVER_ISO
                    )
                dobYear = selectedYear
                dobMonth = selectedMonth
                dobDay = selectedDay
            } else {
                etEndDate.text = displayDate
                mServerEndDate = GeneralFunctions
                    .changeDateFormat(
                        "$selectedYear-${(selectedMonth + 1)}-$selectedDay",
                        Constants.DATE_FORMAT_SERVER, Constants.DATE_FORMAT_SERVER_ISO
                    )
            }
        }

    private val mUpdateDialogReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                mLatitude = p1?.getDoubleExtra(BUNDLE_LATITUDE, 0.0)!!
                mLongitude = p1.getDoubleExtra(BUNDLE_LONGITUDE, 0.0)
                propertyId = p1.getStringExtra(BUNDLE_PROPERTY_ID).toString()
                propertyUnitId = p1.getStringExtra(BUNDLE_PROPERTY_UNIT_ID).toString()
                initAddressDetail(mLatitude, mLongitude)
            } catch (e: Exception) {

            }
        }
    }

    private val mUpdateLocalDataInServer = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                //Call api
                try {
                    mEditJobsDetailViewModel.syncUpdateJobInfoLocalToServer()

                } catch (e: java.lang.Exception) {
                }
            }
        }
    }
}