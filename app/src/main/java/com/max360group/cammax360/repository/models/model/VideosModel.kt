package com.max360group.cammax360.repository.models.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class VideosModel(
    var video:String="",
    var videoName:String="",
    var thumbail:String="",
    var tags:List<String>?= mutableListOf(),
    var users:List<Users>?= mutableListOf(),
    var isUpdated:Boolean=false
):Parcelable