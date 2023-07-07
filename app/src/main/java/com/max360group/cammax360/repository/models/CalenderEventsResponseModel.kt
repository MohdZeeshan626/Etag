package com.max360group.cammax360.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CalenderEventsResponseModel(
    val `data`: EventData,
    val message: String,
    val statusCode: Int
):Parcelable

@Parcelize
data class EventData(
    val records: List<Events>
):Parcelable

@Parcelize
data class Events(
    val address: Address,
    val endDt: String,
    val id: String,
    val jobId: String,
    val kind: String,
    val startDt: String,
    val title: String
):Parcelable