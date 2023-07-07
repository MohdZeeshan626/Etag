package com.max360group.cammax360.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class OwnerAddressModel(
    var name: String = "",
    var city: String = "",
    var country: String = "",
    var formatted: String = "",
    var line1: String? = "",
    var line2: String? = "",
    var state: String? = "",
    var zipCode: String? = "",
    var location: Coordinate = Coordinate()
):Parcelable

@Parcelize
data class Coordinate(
    var coordinates: List<Double>? = mutableListOf(),
    var type: String = "Point"
):Parcelable
