package com.max360group.cammax360.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.max360group.cammax360.repository.models.Tab


class TabsAdapter(fragmentManager: FragmentManager, behavior: Int = 0, private val tabsList: List<Tab>) :
    FragmentPagerAdapter(fragmentManager, behavior) {

    override fun getItem(position: Int): Fragment = tabsList[position].tabFragment!!

    override fun getCount(): Int = tabsList.size

    override fun getPageTitle(position: Int): CharSequence? {
        return tabsList[position].tabName
    }

    fun getCurrentVisibleFragment(currentPageIndex: Int): Fragment =
        tabsList[currentPageIndex].tabFragment!!
}
