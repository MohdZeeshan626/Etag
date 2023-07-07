package com.max360group.cammax360.repository.models

import android.os.Parcelable
import com.max360group.cammax360.repository.models.model.Users
import kotlinx.android.parcel.Parcelize

@Parcelize
class DocsModel(
    var docs:String="",
    var docsName:String="",
    var tags:List<String>?= mutableListOf(),
    var users:List<Users>?= mutableListOf(),
    var isUpdated:Boolean=false
):Parcelable