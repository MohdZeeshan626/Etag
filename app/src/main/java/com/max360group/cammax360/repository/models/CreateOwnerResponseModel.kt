package com.max360group.cammax360.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CreateOwnerResponseModel(
    val `data`: OwnerCreateData,
    val message: String,
    val statusCode: Int
):Parcelable


@Parcelize
data class OwnerCreateData(
    val record: UserOwner
):Parcelable

