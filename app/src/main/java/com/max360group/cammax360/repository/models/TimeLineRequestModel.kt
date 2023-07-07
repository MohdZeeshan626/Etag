package com.max360group.cammax360.repository.models

import android.os.Parcelable
import com.max360group.cammax360.utils.ApplicationGlobal
import kotlinx.android.parcel.Parcelize


@Parcelize
data class TimeLineRequestModel(
    var endDt: String=ApplicationGlobal.currentDate,
    var itemFilters: ItemFilters=ItemFilters(),
    var sortOrder: String="asc",
    var startDt: String= ApplicationGlobal.beforeMonthDate,
    var userFilters: ArrayList<String>?= ArrayList(),
    var userUpdateFilters: UserUpdateFilters=UserUpdateFilters()
):Parcelable

@Parcelize
data class ItemFilters(
    var comments: Boolean=true,
    var conversations: Boolean=true,
    var documents: Boolean=true,
    var mediaPhotos: Boolean=true,
    var mediaVideos: Boolean=true,
    var notes: Boolean=true
):Parcelable

@Parcelize
data class UserUpdateFilters(
    var invitation: Boolean=true,
    var permissions: Boolean=true,
    var termination: Boolean=true
):Parcelable