package com.max360group.cammax360.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class JobDetailResponseModel(
    val `data`: JobDetail,
    val message: String,
    val statusCode: Int
)

@Parcelize
data class JobDetail(
    val job: Job
):Parcelable

