package com.max360group.cammax360.repository.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.devstory.generalhome.repository.room.DBConstants
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OwnerDetailResponseModel(
    var `data`: OwnerData,
    var message: String,
    var statusCode: Int
):Parcelable

@Parcelize
data class OwnerData(
    var record: UserOwner=UserOwner()
):Parcelable


@Entity(tableName = DBConstants.TABLE_OWNERS,indices = [Index(value = ["id","ownerLocalId"], unique = true)])
@Parcelize
data class UserOwner(
    @PrimaryKey(autoGenerate = true)
    var ownerLocalId: Int = 0,
    @ColumnInfo(name = "id")  var id: String = "",
    var access: Access=Access(),
    var addresses: List<OwnerAddressModel>?= mutableListOf(),
    var billingAddress: OwnerAddressModel=OwnerAddressModel(),
    var comments: String="",
    var createdAt: String="",
    var creatorId: String="",
    var email: String?="",
    var emails: List<EmailsModel>?= mutableListOf(),
    var firstName: String="",
    var invite: Invite= Invite(),
    var isActive: Boolean=false,
    var isDeleted: Boolean=false,
    var lastName: String="",
    var notes: List<String>?= mutableListOf(),
    var phoneNumbers: List<PhoneNumberModel>?= mutableListOf(),
    var primaryAddress: OwnerAddressModel= OwnerAddressModel(),
    var primaryUserId: String="",
    var pic: String="",
    var picURL: String="",
    var rm: Rm=Rm(),
    var isSyncServer: Boolean=true,
    var isUpdateInLocal: Boolean=false,
    var isUpdateActiveInLocal: Boolean=false,
    var properties: List<OwnerProperty>?= mutableListOf(),
    var taxId: String="",
    var updatedAt: String="",
    var userId: String="",
    var isSendInvite: Boolean=false
):Parcelable

@Parcelize
data class Rm(
    var LocationID: Int=0,
    var OwnerID: Int=0,
    var displayName: String="",
    var enabled: Boolean=false,
):Parcelable

@Parcelize
data class OwnerProperty(
    var propertyId: String="",
    var propertyUnits: List<String>?= mutableListOf()
):Parcelable
