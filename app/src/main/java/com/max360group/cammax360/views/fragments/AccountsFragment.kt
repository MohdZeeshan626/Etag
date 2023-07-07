package com.max360group.cammax360.views.fragments

import android.content.*
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.room.CamMaxRoomDatabase
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.adapters.OrganisationAdapter
import com.max360group.cammax360.views.calender.MonthlyActivity
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.toolbar.*


class AccountsFragment : BaseFragment(), View.OnClickListener,
    OrganisationAdapter.OrganisationListener {

    companion object {
        const val BUNDLE_FROM = "mFROM"
        const val BUNDLE_NAVIGATION = 0
        const val BUNDLE_NORMAl_VIEWS = 1

        fun newInstance(mFrom: Int = 1): AccountsFragment {
            val takePhotosFragment = AccountsFragment()
            val bundle = Bundle()
            bundle.putInt(BUNDLE_FROM, mFrom)
            takePhotosFragment.arguments = bundle

            return takePhotosFragment
        }
    }


    private val mOrganisationAdapter by lazy {
        OrganisationAdapter(this)
    }

    private var mFrom = 0

    override val layoutId: Int
        get() = R.layout.fragment_account

    override fun init(savedInstanceState: Bundle?) {
        //Get arguments
        mFrom = arguments?.getInt(BUNDLE_FROM, 1)!!

        if (mFrom == BUNDLE_NAVIGATION) {
            toolbar.visibility = View.GONE

        } else {
            // Set toolbar
            toolbar.navigationIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)
            ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)
        }

        //Initialize local broadcast to refreshFragment
        LocalBroadcastManager.getInstance(activityContext)
            .registerReceiver(
                mUpdateProfileReceiver,
                IntentFilter(EditProfileFragment.INTENT_PROFILE)
            )

        //Set adapter
        rvList.adapter = mOrganisationAdapter

        //Set click listener
        btnViewProfile.setOnClickListener(this)
        tvLogout.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

        //Set data
        iniSetDetail()

        //Update adapter
        mOrganisationAdapter.updateData(mUserPrefsManager.loginedUser!!.accounts)
    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnViewProfile -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = ProfileFragment(),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
            R.id.tvLogout -> {
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setIcon(R.drawable.ic_logout)
                    .setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.are_you_sure_you_want_to_logout))
                    .setPositiveButton(getString(R.string.yes),
                        DialogInterface.OnClickListener { dialog, which ->
                            navigateToMainActivity()
                            mUserPrefsManager.clearUserPrefs()
                            CamMaxRoomDatabase.getDatabase(requireContext()).clearAllTables()
                        })
                    .setNegativeButton(getString(R.string.no), null)
                    .show()
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

    private fun iniSetDetail() {
        (mUserPrefsManager.loginedUser!!.firstName + " " + mUserPrefsManager.loginedUser!!.lastName).also {
            tvUserName.text = it
        }
        tvEmail.text = mUserPrefsManager.loginedUser!!.email
        sdvUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)
    }

    private val mUpdateProfileReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            // Get data
            try {
                iniSetDetail()
            } catch (e: Exception) {

            }

        }
    }

    override fun switchAccount(id: String) {
        mUserPrefsManager.setAccount(id)
        //Send broadcast to update photo detail
        LocalBroadcastManager.getInstance(requireContext())
            .sendBroadcast(
                Intent(EditProfileFragment.INTENT_PROFILE)
            )
    }
}