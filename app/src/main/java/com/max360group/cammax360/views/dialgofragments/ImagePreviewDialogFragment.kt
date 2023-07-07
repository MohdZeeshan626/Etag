package com.max360group.cammax360.views.dialgofragments

import android.os.Bundle
import android.view.View
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.ImagePreview
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.views.adapters.ImagePreviewAdatper
import kotlinx.android.synthetic.main.dialogfragment_image_preview.*


/**
 * Created by Mukesh on 03-06-2016.
 */
class ImagePreviewDialogFragment : BaseDialogFragment() {

    companion object {

        const val BUNDLE_EXTRA_IMAGES_LIST = "imagesList"
        const val BUNDLE_EXTRA_IMAGE_POSITION = "imagePosition"

        fun newInstance(
            imagesList: ArrayList<ImagePreview>,
            position: Int
        ): ImagePreviewDialogFragment {
            val imagePreviewDialogFragment = ImagePreviewDialogFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(BUNDLE_EXTRA_IMAGES_LIST, imagesList)
            bundle.putInt(BUNDLE_EXTRA_IMAGE_POSITION, position)
            imagePreviewDialogFragment.arguments = bundle
            return imagePreviewDialogFragment
        }
    }

    override val layoutId: Int
        get() = R.layout.dialogfragment_image_preview

    override fun init() {
        // Get bundle data from arguments
        if (null != arguments) {
            val imagesList = requireArguments()
                .getParcelableArrayList<ImagePreview>(BUNDLE_EXTRA_IMAGES_LIST)

            if (null != imagesList) {
                viewPager.adapter = ImagePreviewAdatper(
                    fragmentManager = childFragmentManager,
                    imagesList = imagesList
                )

                if (1 == imagesList.size) {
                    circlePagerIndicator!!.visibility = View.GONE
                } else {
                    circlePagerIndicator!!.visibility = View.VISIBLE
                    circlePagerIndicator!!.setViewPager(viewPager!!)
                    viewPager!!.currentItem =
                        requireArguments().getInt(BUNDLE_EXTRA_IMAGE_POSITION, 0)
                }
            }
        }
    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {

    }

    override val isFullScreenDialog: Boolean
        get() = true

}
