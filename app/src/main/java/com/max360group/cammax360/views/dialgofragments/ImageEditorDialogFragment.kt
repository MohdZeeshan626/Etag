package com.max360group.cammax360.views.dialgofragments

import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.max360group.cammax360.R
import com.max360group.cammax360.viewmodels.BaseViewModel


class ImageEditorDialogFragment : BaseDialogFragment() {

    override val isFullScreenDialog: Boolean
        get() = true

    override val layoutId: Int
        get() = R.layout.fragment_image_editor

    override fun init() {

    }

    override val viewModel: BaseViewModel?
        get() = null

    override fun observeProperties() {

    }
}