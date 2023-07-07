package com.max360group.cammax360.repository.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devstory.generalhome.repository.room.DBConstants
import kotlinx.android.parcel.Parcelize


@Parcelize
data class JobMediaResponseModel(
    var `data`: JobMediaData,
    var message: String,
    var statusCode: Int):Parcelable

@Parcelize
data class JobMediaData(
    var list: List<JobMediaList>):Parcelable


@Entity(tableName = DBConstants.TABLE_MEDIA,indices = [Index(value = ["id","mediaLocalId"], unique = true)])
@Parcelize
data class JobMediaList(
    @PrimaryKey(autoGenerate = true)
    var mediaLocalId:Int=0,
    @ColumnInfo(name = "id")  var id: String = "",
    var createdAt: String="",
    var creatorId: CreatorId= CreatorId(),
    var isActive: Boolean=false,
    var isDeleted: Boolean=false,
    var jobId: String="",
    var jobLocalId: Int = 0,
    var kind: String="",
    var isSavedInServer: Boolean=true,
    var medias: ArrayList<JobMedia>?= ArrayList<JobMedia>(),
    var name: String="",
    var primaryUserId: String="",
    var subKind: String="",
    var tags: List<String>?= mutableListOf(),
    var updatedAt: String=""):Parcelable

@Parcelize
data class CreatorId(
    var email: String="",
    var firstName: String="",
    var id: String="",
    var lastName: String="",
    var profilePic: String="",
    var picURL: String="",
):Parcelable

@Parcelize
data class JobMedia(
    var createdAt: String="",
    var creatorId: String="",
    var id: String="",
    var media: String="",
    var mediaURL: String="",
    var name: String="",
    var subKind: String="",
    var thumbnail: String="",
    var thumbnailURL: String="",
    var isUpdateInLocal: Boolean=false,
    var isEditUrl: Boolean=false,
    var updatedAt: String="",
    var tags: List<String>?= mutableListOf(),
    var users: ArrayList<JobUser>?= ArrayList<JobUser>()):Parcelable

@Parcelize
data class JobUser(
    var permissions: JobPermissions= JobPermissions(),
    var primaryUserId: String="",
    var userId: String=""):Parcelable

@Parcelize
data class JobPermissions(
    var base: Int=1,
    var comments: Int=1,
    var members: Int=1):Parcelable