package com.max360group.cammax360.views.fragments

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Tab
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.adapters.TabsAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_base_tab_layout.*
import kotlinx.android.synthetic.main.toolbar.*


/**
 * Created by Mukesh on 16/5/18.
 */
abstract class BaseTabLayoutFragment : BaseFragment() {

    private val mLayoutInflater by lazy { LayoutInflater.from(activityContext) }

    override fun init(savedInstanceState: Bundle?) {
        if (null != toolbar) {
            // Remove Toolbar elevation
            if (GeneralFunctions.isAboveLollipopDevice) {
                toolbar.elevation = 0f
            }
        }
        initTabs(savedInstanceState)
    }

    fun setViewPager(toolbarTitle: String, tabsList: List<Tab>) {
        if (null != tvToolbarTitle) {
            tvToolbarTitle.text = toolbarTitle
        }

        val tabsAdapter = TabsAdapter(fragmentManager = childFragmentManager, tabsList = tabsList)
        viewPager.offscreenPageLimit = 1
        viewPager.adapter = tabsAdapter

        if (null != tabLayout) {
            // Attach TabLayout to ViewPager
            tabLayout.setupWithViewPager(viewPager)

            // Set custom tab view
            for (i in tabsList.indices) {
                val view = mLayoutInflater.inflate(
                    R.layout.fragment_base_tab_layout, null,
                    false
                ) as ImageView
                view.setImageResource(tabsList[i].tabIcon)
                tabLayout.getTabAt(i)?.customView = view

                // Disable Tab touch
                for (touchedView in tabLayout.touchables) {
                    touchedView.isEnabled = false
                }
            }

            // Change Tab text font
            if (tabsList[0].tabName.isNotEmpty()) {
                changeTabsFont(tabLayout)
            }

//            tabLayout.clearOnTabSelectedListeners()
        }
    }

    // Change Tab title font
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun changeTabsFont(tabLayout: TabLayout) {
        val vg = tabLayout.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup
            val tabChildsCount = vgTab.childCount
            for (i in 0 until tabChildsCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    tabViewChild.typeface = ResourcesCompat.getFont(
                        activityContext,
                        R.font.font_santral_medium
                    )
                }
            }
        }
    }

    abstract fun initTabs(savedInstanceState: Bundle?)

}