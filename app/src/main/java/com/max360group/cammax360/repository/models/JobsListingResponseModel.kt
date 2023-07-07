package com.max360group.cammax360.repository.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devstory.generalhome.repository.room.DBConstants
import kotlinx.android.parcel.Parcelize
import java.util.concurrent.CopyOnWriteArrayList


@Parcelize
data class JobsListingResponseModel(
    var `data`: JobsData,
    var message: String,
    var statusCode: Int
) : Parcelable


@Parcelize
data class JobsData(
    var count: Int = 0,
    var mediasCount: MediaCount = MediaCount(),
    var list: List<Job>? = mutableListOf()
) : Parcelable

@Entity(tableName = DBConstants.TABLE_JOBS,indices = [Index(value = ["id","jobLocalId"], unique = true)])
@Parcelize
data class Job(
    @PrimaryKey(autoGenerate = true)
    var jobLocalId: Int = 0,
    @ColumnInfo(name = "id")  var id: String = "",
    var address: Address = Address(),
    var createdAt: String = "",
    var creatorId: String = "",
    var deletedMedias: List<String>? = mutableListOf(),
    var details: Details = Details(),
    var endDt: String = "",
    var isActive: Boolean = false,
    var isDeleted: Boolean = false,
    var isSavedInServer: Boolean = true,
    var isUpdateInLocal: Boolean = false,
    val medias: List<Media>? = mutableListOf(),
    var primaryUserId: String = "",
    var startDt: String = "",
    var title: String = "",
    var updatedAt: String = "",
    var count: String = "",
    var propertyId: String ?= "",
    var propertyUnitId: String?= "",
    var users: ArrayList<UserX>? = ArrayList()
) : Parcelable

@Parcelize
data class Address(
    var city: String = "",
    var country: String = "",
    var formatted: String = "",
    var line1: String = "",
    var line2: String = "",
    var location: Location = Location(),
    var state: String = "",
    var zipCode: String = "",
    var propertyId: String = "",
    var propertyUnitId: String = ""
) : Parcelable

@Parcelize
data class Details(
    var commentsCount: Int = 0,
    var contributionUpdatedAt: String = "",
    var conversationCount: Int = 0,
    var mediaDocsCount: Int = 0,
    var mediaPhotosCount: Int = 0,
    var mediaVideosCount: Int = 0,
    var notesCount: Int = 0
) : Parcelable

@Parcelize
data class Media(
    var id: String = "",
    var kind: String = "",
    var medias: ArrayList<JobMedia>? = ArrayList(),
    var name: String = "",
    var subKind: String = ""
) : Parcelable

@Parcelize
data class UserX(
    var createdAt: String = "",
    var details: DetailsX = DetailsX(),
    var invite: Invite = Invite(),
    var permissions: Jobs = Jobs(),
    var isAddInLocal: Boolean = false,
    var isPermissionUpdatedInLocal: Boolean = false,
    var primaryUserId: String = "",
    var updatedAt: String = "",
    var userId: UserId = UserId()
) : Parcelable

@Parcelize
data class Location(
    var coordinates: List<Double>? = mutableListOf(),
    var type: String = ""
) : Parcelable

@Parcelize
data class MediaX(
    var createdAt: String,
    var creatorId: String,
    var id: String,
    var media: String,
    var mediaURL: String,
    var thumbnailURL: String,
    var name: String,
    var subKind: String,
    var updatedAt: String,
    var users: List<JobsUser>
) : Parcelable

@Parcelize
data class JobsUser(
    var permissions: JobsPermissions,
    var primaryUserId: String,
    var userId: String
) : Parcelable

@Parcelize
data class JobsPermissions(
    var base: Int=1,
    var comments: Int=1,
    var members: Int=1
) : Parcelable

@Parcelize
data class DetailsX(
    var commentsCount: Int = 0,
    var contributionCount: Int = 0,
    var contributionUpdatedAt: String = "",
    var conversationCount: Int = 0,
    var mediaDocsCount: Int = 0,
    var mediaPhotosCount: Int = 0,
    var mediaVideosCount: Int = 0,
    var notesCount: Int = 0
) : Parcelable

@Parcelize
data class Invite(
    var createdAt: String = "",
    var status: String = "",
    var token: String = "",
    var updatedAt: String = ""
) : Parcelable

@Parcelize
data class PermissionsX(
    var base: Int,
    var comments: Int,
    var conversations: Int,
    var documents: Int,
    var mediaPhotos: Int,
    var mediaVideos: Int,
    var members: Int,
    var notes: Int
) : Parcelable

@Parcelize
data class UserId(
    var email: String = "",
    var firstName: String = "",
    var id: String = "",
    var lastName: String = ""
) : Parcelable
@Parcelize
data class MediaCount(
    val doc: Int=0,
    val photo: Int=0,
    val video: Int=0
) : Parcelable