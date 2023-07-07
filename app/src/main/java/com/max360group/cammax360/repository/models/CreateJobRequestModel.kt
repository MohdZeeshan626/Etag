package com.max360group.cammax360.repository.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devstory.generalhome.repository.room.DBConstants.TABLE_JOBS
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreateJobRequestModel(
    var propertyId:String="",
    var propertyUnitId:String="",
    val address: AddressRequest= AddressRequest(),
    val endDt: String="",
    val startDt: String="",
    val title: String="",
    var users: List<UserDetail>?= mutableListOf()
) : Parcelable


@Parcelize
data class UserDetail(
    var email: String="",
    var name: String="",
    var permissions: Jobs=Jobs()
) : Parcelable

@Parcelize
data class AddressRequest(
    val city: String="",
    val country: String="",
    val formatted: String="",
    val latitude: Double?=0.0,
    val line1: String?="",
    val line2: String?="",
    val longitude: Double?=0.0,
    val state: String?="",
    val zipCode: String?="",
    val location:Coordinates=Coordinates()
) : Parcelable

@Parcelize
data class Coordinates(
   val coordinates:List<Double>?= mutableListOf(),
   val type:String="Point"
) : Parcelable