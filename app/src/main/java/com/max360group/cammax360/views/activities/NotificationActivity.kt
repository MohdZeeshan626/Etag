package com.max360group.cammax360.views.activities

import androidx.navigation.fragment.NavHostFragment
import com.max360group.cammax360.R
import com.max360group.cammax360.views.fragments.LoginFragment
import com.max360group.cammax360.views.fragments.NotificationFragment

class NotificationActivity : BaseAppCompactActivity() {

    override val layoutId: Int
        get() = R.layout.activity_main

    override val isMakeStatusBarTransparent: Boolean
        get() = false

    override fun init() {
        // Set Splash Screen
        doFragmentTransaction(
            fragment = NotificationFragment(),
            containerViewId = R.id.flFragContainerMain, isAddToBackStack = false)
    }

    override val navHostFragment: NavHostFragment?
        get() = null

    override fun onBackPressed() {
        closeActivity()
    }
}