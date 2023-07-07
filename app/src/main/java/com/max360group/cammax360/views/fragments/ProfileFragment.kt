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
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.AccountViewModel
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.AccountsAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.dialgofragments.ChangePasswordDialogFragment
import com.max360group.cammax360.views.fragments.EditProfileFragment.Companion.INTENT_PROFILE
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File

class ProfileFragment : BasePictureOptionsFragment(), AccountsAdapter.AccountListener,
    View.OnClickListener {

    private val mAccountViewModel by lazy {
        ViewModelProvider(this).get(AccountViewModel::class.java)
    }

    private val mAccountsAdapter by lazy {
        AccountsAdapter(this)
    }
    override val layoutId: Int
        get() = R.layout.fragment_profile


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

        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mUpdateProfileReceiver,
                IntentFilter(INTENT_PROFILE)
            )

        //Set adapter
        rvAccounts.adapter = mAccountsAdapter

        //Set click listener
        btnEditPofile.setOnClickListener(this)
        btnChangePassword.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

        //Call api
        if (GeneralFunctions.isInternetConnected(requireContext())) {
            mAccountViewModel.getProfile()
        } else {
            mAccountViewModel.getUserProfileFromLocal()
        }

    }

    override fun onGettingImageFile(file: File) {
        sdvUserImage.setImageURI(GeneralFunctions.getLocalImageFile(file))
    }

    override fun onGettingMultipleImages(list: List<String>) {

    }

    override fun onStartCameraDialogFragment(isOpen: Boolean) {

    }

    override val viewModel: BaseViewModel
        get() = mAccountViewModel

    override fun observeProperties() {
        mAccountViewModel.onGetProfile().observe(this, Observer {

            if (it.picURL!!.startsWith("https://")) {
                sdvUserImage.setImageURI(it.picURL)
            } else {
                sdvUserImage.setImageURI(GeneralFunctions.getLocalImageFile(File(it.picURL!!)))
            }
            (it.firstName + " " + it.lastName).also { tvName.text = it }
            tvEmail.text = it.email

            //Update adapter
            mAccountsAdapter.updateData(it.accounts!!)
        })
    }

    override fun onItemClick(account: Account) {
        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
            fragment = AccountDetailsFragment.newInstance(account),
            containerViewId = R.id.flFragContainerMain,
            enterAnimation = R.animator.slide_right_in,
            popExitAnimation = R.animator.slide_right_out
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnEditPofile -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = EditProfileFragment(),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }

            R.id.btnChangePassword -> {
                ChangePasswordDialogFragment().show(childFragmentManager, "")
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

    private val mUpdateProfileReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                if (GeneralFunctions.isInternetConnected(requireContext())) {
                    mAccountViewModel.getProfile(false)
                } else {
                    mAccountViewModel.getUserProfileFromLocal()
                }
            } catch (e: Exception) {

            }

        }
    }
}