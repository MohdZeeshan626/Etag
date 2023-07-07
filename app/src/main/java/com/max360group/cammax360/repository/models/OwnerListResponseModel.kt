package com.max360group.cammax360.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class OwnerListResponseModel(
    val `data`: OwnersData,
    val message: String,
    val statusCode: Int
):Parcelable

@Parcelize
data class OwnersData(
    val count: Int=0,
    val records: List<UserOwner>?= mutableListOf()
):Parcelable

@Parcelize
data class Owners(
    val createdAt: String="",
    val email: String="",
    val firstName: String="",
    val id: String="",
    var isActive: Boolean=false,
    val lastName: String="",
    val pic: String="",
    val picURL: String="",
    val taxId: String="",
    val comment: String="",
    val userId: String=""
):Parcelable