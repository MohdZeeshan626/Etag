package com.max360group.cammax360.views.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.navigation.fragment.NavHostFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.max360group.cammax360.R
import com.max360group.cammax360.utils.Constants
import com.max360group.cammax360.utils.DialogUtils
import com.max360group.cammax360.utils.VersionChecker
import com.max360group.cammax360.views.fragments.HomeFragment
import com.max360group.cammax360.views.fragments.JobDetailsFragment
import com.max360group.cammax360.views.fragments.LoginFragment
import java.util.concurrent.ExecutionException

class NotifyActivity : BaseAppCompactActivity() {
    companion object{
        const val BUNDLE_JOb_Id="jobId"
    }

    var mJobId=""
    override val layoutId: Int
        get() = R.layout.activity_main

    override val isMakeStatusBarTransparent: Boolean
        get() = false

    override fun init() {
        //Get intent
        mJobId= intent.getStringExtra(BUNDLE_JOb_Id).toString()

        doFragmentTransaction(
            fragment = JobDetailsFragment.newInstance(mJobId,""),
            containerViewId = R.id.flFragContainerMain, isAddToBackStack = false
        )


    }

    override val navHostFragment: NavHostFragment?
        get() = null
    
}
