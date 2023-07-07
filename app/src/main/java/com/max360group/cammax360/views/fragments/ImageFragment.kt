package com.max360group.cammax360.views.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.max360group.cammax360.R
import com.facebook.drawee.backends.pipeline.Fresco
import com.max360group.cammax360.repository.models.ImagePreview
import com.max360group.cammax360.utils.AmazonS3
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import kotlinx.android.synthetic.main.fragment_image.*
import java.io.File


/**
 * Created by Mukesh on 03/06/2016.
 */
class ImageFragment : BaseFragment() {

    companion object {

        internal const val IMAGE_TYPE_USER_PROFILE = 1
        internal const val IMAGE_TYPE_USER_QUESTIONS = 2

        const val BUNDLE_EXTRAS_IMAGE = "image"

        fun newInstance(image: ImagePreview): ImageFragment {
            val imageFragment = ImageFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_EXTRAS_IMAGE, image)
            imageFragment.arguments = bundle
            return imageFragment
        }
    }

    override val layoutId: Int
        get() = R.layout.fragment_image

    override fun init(savedInstanceState: Bundle?) {
        // Get image from arguments
        val imagePreview = arguments?.getParcelable(BUNDLE_EXTRAS_IMAGE) ?: ImagePreview()

        // check if image is a local file or url
        with(imagePreview) {
            if (GeneralFunctions.isRemoteImage(image)) {
                when (imageType) {
                    IMAGE_TYPE_USER_PROFILE -> {
                        setImage(AmazonS3.S3_BUCKET_BASE_URL_FOR_USER_PHOTOS + image)
                    }
                    IMAGE_TYPE_USER_QUESTIONS -> {
                        setImage(AmazonS3.S3_BUCKET_BASE_URL_FOR_USER_QUESTIONS + image)
                    }
                }
            } else {
                val imageFile = File(image)
                if (imageFile.exists()) {
                    setImage(GeneralFunctions.getLocalImageFile(imageFile))
                }
            }

            if (caption.isNotEmpty()) {
                tvCaption.visibility = View.VISIBLE
                tvCaption.text = caption
            }
        }
    }

    private fun setImage(image: String) {
        zdvImage.setAllowTouchInterceptionWhileZoomed(true)
        val controller = Fresco.newDraweeControllerBuilder()
            .setUri(Uri.parse(image))
            .build()
        zdvImage.controller = controller
    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {
    }

}
