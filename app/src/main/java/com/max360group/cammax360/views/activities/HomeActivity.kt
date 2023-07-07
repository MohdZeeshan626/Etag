package com.max360group.cammax360.views.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.max360group.cammax360.R
import com.max360group.cammax360.services.CheckInternetService
import com.max360group.cammax360.utils.ColorTheme
import com.max360group.cammax360.utils.ConnectivityChangeReceiver
import com.max360group.cammax360.viewmodels.*
import com.max360group.cammax360.views.fragments.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.logging.Handler


open class HomeActivity : BaseAppCompactActivity() {

    open var version: String? = null
    private val mNetworkReceiver = ConnectivityChangeReceiver()

    private val mJobMediaViewModel by lazy {
        ViewModelProvider(this).get(JobMediaViewModel::class.java)
    }

    private val mMediaDetailViewModel by lazy {
        ViewModelProvider(this).get(MediaDetailViewModel::class.java)
    }

    private val mJobDetailViewModel by lazy {
        ViewModelProvider(this).get(JobDetailViewModel::class.java)
    }

    private val mJobsViewModel by lazy {
        ViewModelProvider(this).get(JobsViewModel::class.java)
    }

    private val mEditJobsDetailViewModel by lazy {
        ViewModelProvider(this).get(EditJobsDetailViewModel::class.java)
    }

    private val mOwnerViewModel by lazy {
        ViewModelProvider(this).get(OwnerViewModel::class.java)
    }

    private val mPropertyViewModel by lazy {
        ViewModelProvider(this).get(PropertyViewModel::class.java)
    }

    private var isSync = false


    override val layoutId: Int
        get() = R.layout.activity_main

    override val isMakeStatusBarTransparent: Boolean
        get() = false

    override fun init() {
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(mNetworkReceiver, filter)

        // Register receiver for sync data
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                mUpdateLocalDataInServer,
                IntentFilter(CheckInternetService.INTENT_SYNC_DATA)
            )

        // Set Splash Screen
        doFragmentTransaction(
            fragment = HomeFragment(),
            containerViewId = R.id.flFragContainerMain, isAddToBackStack = false
        )

        //Observer
        initObserver()
    }

    override val navHostFragment: NavHostFragment?
        get() = null


    private fun initObserver() {
        mJobsViewModel.onMediaSyncingStart().observe(this, Observer {
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                mJobMediaViewModel.syncVideoLocalToServer()
            }, 20000)

        })
    }

    private val mUpdateLocalDataInServer = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                try {
                    //Syncing data
                    if (!isSync) {
                        mJobsViewModel.uploadJobsLocalToServer()
                        mOwnerViewModel.uploadOwnerFromLocalToServer()
                        mOwnerViewModel.deleteOwnersFromLocalToServer()
                        mOwnerViewModel.updateOwnerFromLocalToServer()
                        mOwnerViewModel.updateStateLocalToServer()
                        mPropertyViewModel.uploadPropertyFromLocalToServer()
                        mPropertyViewModel.deletePropertyFromLocalToServer()
                        mPropertyViewModel.updatePropertyStateLocalToServer()
                        mPropertyViewModel.updatePropertyLocalToServer()
                        mMediaDetailViewModel.deleteMediaLocalToServer()
                        mMediaDetailViewModel.updateMediaLocalToServer()
                        mJobDetailViewModel.deleteJobsFromLocalToServer()
                        mEditJobsDetailViewModel.syncJobMemberPermissionLocalToServer()
                        mEditJobsDetailViewModel.syncJobMemberMembersLocalToServer()
                        mEditJobsDetailViewModel.deleteJobMembersLocalToServer()
                        isSync = true
                        android.os.Handler(Looper.getMainLooper()).postDelayed({
                            isSync = false
                        }, 5000)
                    }


                } catch (e: java.lang.Exception) {
                }
            }
        }
    }
}
