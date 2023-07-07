package com.max360group.cammax360.repository.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devstory.generalhome.repository.room.DBConstants.TABLE_MEMBERS
import kotlinx.android.parcel.Parcelize


@Parcelize
data class GetUserModuleWiseResponseMode(
    val `data`: ModuleData = ModuleData(),
    val message: String,
    val statusCode: Int
) : Parcelable

@Parcelize
data class ModuleData(
    val `data`: List<AccountList>? = mutableListOf()
) : Parcelable


@Entity(tableName = TABLE_MEMBERS)
@Parcelize
data class AccountList(
    var accounts: ArrayList<AccountDetail>? = ArrayList(),
    var email: String = "",
    var firstName: String = "",
    @PrimaryKey
    var id: String = "",
    var lastName: String = "",
    val profilePic: String = "",
    val userType: Int = 0,
    var isChecked: Boolean = false
) : Parcelable


@Parcelize
data class AccountDetail(
    val createdAt: String = "",
    val invite: Invite = Invite(),
    val isBlocked: Boolean = false,
    val isDefault: Boolean = false,
    val permissions: Permissions = Permissions(),
    val primaryUserId: String = "",
    val updatedAt: String = "",
    val userType: Int = 0
) : Parcelable

