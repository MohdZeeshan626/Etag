package com.max360group.cammax360.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PhoneNumberModel(
    var name:String="",
    var phoneNumber:String="",
    var extension:String="",
    var default:Boolean=false,
    var textMessage:Boolean=false
):Parcelable