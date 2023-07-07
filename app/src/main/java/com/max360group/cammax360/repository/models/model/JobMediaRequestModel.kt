package com.max360group.cammax360.repository.models.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class JobMediaRequestModel(
    var kind: String="",
    var medias: ArrayList<Media>?= ArrayList()
):Parcelable

@Parcelize
data class Media(
    var name: String="",
    var subKind: String="",
    var subMedias: ArrayList<SubMedia>?= ArrayList(),
    var tags: ArrayList<String>?= ArrayList()
):Parcelable

@Parcelize
data class SubMedia(
    var media: String="",
    var name: String="",
    var subKind: String="",
    var tags: ArrayList<String>?= ArrayList(),
    var users: ArrayList<User>?= ArrayList()
):Parcelable

@Parcelize
data class User(
    var permissions: Permissions=Permissions(),
    var primaryUserId: String="",
    var userId: String=""
):Parcelable

@Parcelize
data class Permissions1(
    var base: Int=1,
    var comments: Int=1,
    var members: Int=1
):Parcelable