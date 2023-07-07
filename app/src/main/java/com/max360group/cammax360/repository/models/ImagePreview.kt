package com.max360group.cammax360.repository.models

import android.os.Parcelable
import com.max360group.cammax360.views.fragments.ImageFragment
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImagePreview(
    val image: String = "",
    val caption: String = "",
    val imageType: Int = ImageFragment.IMAGE_TYPE_USER_PROFILE
) : Parcelable