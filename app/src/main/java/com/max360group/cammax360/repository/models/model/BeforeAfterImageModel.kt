package com.max360group.cammax360.repository.models.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class BeforeAfterImageModel(
    var simpleImage:String="",
    var beforeImage:String="",
    var afterImage:String="",
    var simpleImageName:String="",
    var beforeImageName:String="",
    var afterImageName:String="",
    var tags:List<String>?= mutableListOf(),
    var tagsAfter:List<String>?= mutableListOf(),
    var tagsBefore:List<String>?= mutableListOf(),
    var users:List<Users>?= mutableListOf(),
    var usersAfter:List<Users>?= mutableListOf(),
    var usersbefore:List<Users>?= mutableListOf(),
    var isPermissionUpdate:Boolean=false
):Parcelable

@Parcelize
data class Users(
    var permissions: Permissions=Permissions(),
    var primaryUserId: String="",
    var userId: String="",
    var name: String="",
    var isChecked:Boolean=true,
    var email:String=""
):Parcelable

@Parcelize
data class Permissions(
    var base: Int=0,
    var comments: Int=0,
    var members: Int=0
):Parcelable