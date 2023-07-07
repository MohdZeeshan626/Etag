package com.max360group.cammax360.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.max360group.cammax360.repository.models.ImagePreview
import com.max360group.cammax360.views.fragments.ImageFragment

/**
 * Created by Mukesh on 24/05/2016.
 */
class ImagePreviewAdatper(
    fragmentManager: FragmentManager, behaviour: Int = 0,
    private val imagesList: ArrayList<ImagePreview>
) :
    FragmentStatePagerAdapter(fragmentManager, behaviour) {

    override fun getItem(position: Int): Fragment =
        ImageFragment.newInstance(imagesList[position])

    override fun getCount(): Int = imagesList.size
}
