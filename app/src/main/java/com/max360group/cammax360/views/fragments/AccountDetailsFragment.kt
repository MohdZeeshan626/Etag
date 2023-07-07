package com.max360group.cammax360.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Account
import com.max360group.cammax360.repository.models.OwnerAddressModel
import com.max360group.cammax360.services.CheckInternetService
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.ColorTheme
import com.max360group.cammax360.utils.Constants.LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.AccountViewModel
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.HomeActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.ColorsAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import kotlinx.android.synthetic.main.fragment_accounts_detail.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File
import java.lang.Exception

class AccountDetailsFragment : BasePictureOptionsFragment(), ColorsAdapter.AccountDetailsListener,
    View.OnClickListener {

    companion object {
        const val BUNDLE_ACCOUNT_DATA = "accountData"

        fun newInstance(mAccount: Account): AccountDetailsFragment {
            val mAccountDetailsFragment = AccountDetailsFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_ACCOUNT_DATA, mAccount)
            mAccountDetailsFragment.arguments = bundle

            return mAccountDetailsFragment
        }
    }

    private val mColorsAdapter by lazy {
        ColorsAdapter(this)
    }

    private val mAccountViewModel by lazy {
        ViewModelProvider(this).get(AccountViewModel::class.java)
    }

    private var mAccount = Account()
    private var mAddress = OwnerAddressModel()
    private var mPrimaryColor = ""
    private var mPrimaryLight = ""
    private var mAccountImage = ""

    override val layoutId: Int
        get() = R.layout.fragment_accounts_detail

    override fun setData(savedInstanceState: Bundle?) {
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

        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mGetUpdateDataBroadcastReceiver,
                IntentFilter(OwnerGeneralInfoFragment.INTENT_GENERAL_INFO)
            )

        //Get arguments
        mAccount = arguments?.getParcelable<Account>(BUNDLE_ACCOUNT_DATA)!!

        //Set click listener
        btnSaveChanges.setOnClickListener(this)
        ivCameraPicker.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)
        ivEdit.setOnClickListener(this)

        //Set detail
        initDetail(mAccount)

        //Set adapter
        rvColors.adapter = mColorsAdapter
    }

    override fun onGettingImageFile(file: File) {
        mAccountImage = file.absolutePath
        sdvUserImage.setImageURI(GeneralFunctions.getLocalImageFile(file))
    }

    override fun onGettingMultipleImages(list: List<String>) {
    }

    override fun onStartCameraDialogFragment(isOpen: Boolean) {
    }

    override val viewModel: BaseViewModel?
        get() = mAccountViewModel

    override fun observeProperties() {
        mAccountViewModel.onGetProfile().observe(this, Observer {
            navigateToMainActivity(HomeActivity())
        })

        mAccountViewModel.onAccountUpdated().observe(this, Observer {
            navigateToMainActivity(HomeActivity())
        })

    }

    private fun initDetail(mdata: Account) {
        if (mdata.primaryUserId!!.account!!.logoURL!!.startsWith("https://")) {
            sdvUserImage.setImageURI(mdata.primaryUserId!!.account!!.logoURL)
        } else {
            sdvUserImage.setImageURI(GeneralFunctions.getLocalImageFile(File(mdata.primaryUserId!!.account!!.logoURL!!)))
        }
        tvName.text = mdata.primaryUserId!!.account!!.name
        etOrganisationName.setText(mdata.primaryUserId!!.account!!.name)
        tvEmail.text = mdata.primaryUserId!!.account!!.primaryEmail
        etEmail.setText(mdata.primaryUserId!!.account!!.primaryEmail)
        etPhoneNumber.setText(mdata.primaryUserId!!.account!!.phone)
        tvAddress.text = mdata.primaryUserId!!.account!!.address!!.formatted
        mAccountImage = mdata.primaryUserId!!.account!!.logo.toString()
        mPrimaryColor = mUserPrefsManager.loginedUser!!.theme!!.primary.toString()
        mPrimaryLight = mUserPrefsManager.loginedUser!!.theme!!.primaryLight.toString()
        if (mdata.primaryUserId!!.account!!.address!!.location.coordinates!!.isNotEmpty()) {
            ivMap.setImageURI(
                GeneralFunctions.getStaticMap(
                    mdata.primaryUserId!!.account!!.address!!.location.coordinates!![0],
                    mdata.primaryUserId!!.account!!.address!!.location.coordinates!![1]
                )
            )
        } else {
            ivMap.setImageURI(
                GeneralFunctions.getStaticMap(
                    0.0,
                    0.0
                )
            )
        }
        //Update address model
        mAddress = mdata.primaryUserId!!.account!!.address!!

        //Update the position
        mColorsAdapter.updateData(mUserPrefsManager.loginedUser!!.theme!!.primary)

    }

    override fun onThemeClick(position: Int, colorPrimary: String, colorLight: String) {
        mPrimaryColor = colorPrimary
        mPrimaryLight = colorLight
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSaveChanges -> {
                //Set default color
                if (mPrimaryColor.isBlank()) {
                    mPrimaryColor = ColorTheme.getThemeColor()[0].primary.toString()
                    mPrimaryLight = ColorTheme.getThemeColor()[0].primaryLight.toString()
                }

                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mAccountViewModel.updateAccount(
                        mAccount.primaryUserId!!.id!!,
                        etOrganisationName.text.toString().trim(),
                        etEmail.text.toString().trim(),
                        etPhoneNumber.text.toString().trim(),
                        mAddress,
                        mAccountImage,
                        mPrimaryColor,
                        mPrimaryLight
                    )
                } else {
                    mAccountViewModel.updateAccountInLocal(
                        mAccount.primaryUserId!!.id!!,
                        etOrganisationName.text.toString().trim(),
                        etEmail.text.toString().trim(),
                        etPhoneNumber.text.toString().trim(),
                        mAddress,
                        mAccountImage,
                        mPrimaryColor,
                        mPrimaryLight
                    )
                }
            }

            R.id.ivCameraPicker -> {
                showPictureOptionsBottomSheet(LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS)
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

            R.id.ivEdit -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = AddAddressFragment.newInstance(mAddress),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.scale_fade_out
                )
            }
        }
    }

    private val mGetUpdateDataBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                //Call api
                try {
                    mAddress =
                        p1?.getParcelableExtra<OwnerAddressModel>(AddAddressFragment.INTENT_ADDRESS_DATA)!!
                    tvAddress.text = mAddress!!.formatted
                    if (mAddress.location.coordinates!!.isNotEmpty()) {
                        ivMap.setImageURI(
                            GeneralFunctions.getStaticMap(
                                mAddress.location.coordinates!![0],
                                mAddress.location.coordinates!![1]
                            )
                        )
                    } else {
                        ivMap.setImageURI(
                            GeneralFunctions.getStaticMap(
                                0.0,
                                0.0
                            )
                        )
                    }

                } catch (e: Exception) {

                }
            }
        }
    }

    private val mUpdateLocalDataInServer = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                //Call api
                try {
                    mAccountViewModel.syncUserAccountLocalToServer()

                } catch (e: Exception) {
                }
            }
        }
    }
}