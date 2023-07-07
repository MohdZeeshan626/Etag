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
import com.max360group.cammax360.services.CheckInternetService
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.Constants.LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.AccountViewModel
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.calender.MonthlyActivity
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_profile.sdvUserImage
import kotlinx.android.synthetic.main.fragment_profile.tvEmail
import kotlinx.android.synthetic.main.fragment_profile.tvName
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File
import java.lang.Exception

class EditProfileFragment : BasePictureOptionsFragment(), View.OnClickListener {

    companion object{
        const val INTENT_PROFILE="profile"
    }

    private val mAccountViewModel by lazy {
        ViewModelProvider(this).get(AccountViewModel::class.java)
    }

    private var mImageFile=""

    override val layoutId: Int
        get() = R.layout.fragment_edit_profile

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

        //Set click listener
        ivCameraPicker.setOnClickListener(this)
        btnSaveChanges.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

        //Call api
        if (GeneralFunctions.isInternetConnected(requireContext())){
            mAccountViewModel.getProfile()
        }else{
            mAccountViewModel.getUserProfileFromLocal()
        }

    }

    override fun onGettingImageFile(file: File) {
        mImageFile=file.absolutePath
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
            if (it.picURL!!.startsWith("https://")) {
                sdvUserImage.setImageURI(it.picURL)
            } else {
                sdvUserImage.setImageURI(GeneralFunctions.getLocalImageFile(File(it.picURL)))
            }

            (it.firstName + " " + it.lastName).also { tvName.text = it }
            etFirstName.setText(it.firstName)
            etLastName.setText(it.lastName)
            etEmail.setText(it.email)
            tvEmail.text = it.email
            mImageFile=it.pic!!
        })

        mAccountViewModel.onProfileUpdate().observe(this, Observer {
            //Send broadcast to update photo detail
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(
                    Intent(INTENT_PROFILE)
                )
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivCameraPicker -> {
                showPictureOptionsBottomSheet(LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS)
            }
            R.id.btnSaveChanges -> {
                if (GeneralFunctions.isInternetConnected(requireContext())){
                    mAccountViewModel.updateProfile(etFirstName.text.toString().trim(),
                        etLastName.text.toString().trim(),mImageFile)
                }else{
                    mAccountViewModel.updateUserInLocal(etFirstName.text.toString().trim(),
                        etLastName.text.toString().trim(),mImageFile)
                }

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

    private val mUpdateLocalDataInServer = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                //Call api
                try {
                    mAccountViewModel.syncUserLocalToServer()

                } catch (e: Exception) {
                }
            }
        }
    }

}