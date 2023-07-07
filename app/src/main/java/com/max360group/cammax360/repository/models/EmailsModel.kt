package com.max360group.cammax360.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class EmailsModel(
    var type:String="",
    var email:String="",
    ):Parcelable