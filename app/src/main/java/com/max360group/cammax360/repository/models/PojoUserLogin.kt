package com.max360group.cammax360.repository.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devstory.generalhome.repository.room.DBConstants
import kotlinx.android.parcel.Parcelize

/**
 * Created by Mukesh on 19/7/18.
 */

@Parcelize
data class PojoUserLogin(
    var `data`: UserData?=UserData(),
    var message: String?="",
    var statusCode: Int?=0
) : Parcelable

@Parcelize
data class UserData(
    var auth: Auth?= Auth(),
    var user: User?=User()
) : Parcelable

@Parcelize
data class Auth(
    var expiry: Int?=0,
    var token: String?=""
) : Parcelable


@Entity(tableName = DBConstants.TABLE_USERS)
@Parcelize
data class User(
    @PrimaryKey  var id: String="",
    var accounts: List<Account>?= mutableListOf(),
    var createdAt: String?="",
    var accessToken: String?="",
    var password: String?="",
    var creatorId: String?="",
    var email: String?="",
    var firstName: String?="",
    var isActive: Boolean?=false,
    var isDeleted: Boolean?=false,
    var isLoggedIn: Boolean?=false,
    var lastName: String?="",
    var primaryUserId: String?="",
    var serviceType: Int?=0,
    var updatedAt: String?="",
    var userType: Int?=0,
    var isLocalUpdate: Boolean?=false,
    var picURL: String?="",
    var pic: String?="",
    var theme: Theme?=Theme()
) : Parcelable

@Parcelize
data class Account(
    var createdAt: String?="",
    var invite: AccountsInvite?=AccountsInvite(),
    var isBlocked: Boolean?=false,
    var isDefault: Boolean?=false,
    var isUpdateInLocal: Boolean?=false,
    var permissions: Permissions?= Permissions(),
    var primaryUserId: PrimaryUserId?= PrimaryUserId(),
    var updatedAt: String?="",
    var userType: Int?=0
) : Parcelable

@Parcelize
data class AccountsInvite(
    var createdAt: String?="",
    var status: String?="",
    var token: String?="",
    var updatedAt: String?=""
) : Parcelable

@Parcelize
data class Permissions(
    var account: AccountX?= AccountX(),
    var jobs: Jobs?= Jobs(),
    var products: Products?= Products(),
    var properties: Properties?= Properties(),
    var roles: Roles?= Roles(),
    var users: Users?= Users()
) : Parcelable

@Parcelize
data class PrimaryUserId(
    var account: AccountXX?= AccountXX(),
    var id: String?=""
) : Parcelable

@Parcelize
data class AccountX(
    var base: Int?=0
) : Parcelable

@Parcelize
data class Jobs(
    var base: Int=1,
    var comments: Int=1,
    var conversations: Int=1,
    var documents: Int=1,
    var mediaPhotos: Int=1,
    var mediaVideos: Int=1,
    var members: Int=1,
    var notes: Int=1
) : Parcelable

@Parcelize
data class Products(
    var base: Int?=0
) : Parcelable

@Parcelize
data class Properties(
    var base: Int?=0,
    var units: Int?=0
) : Parcelable

@Parcelize
data class Roles(
    var base: Int?=0
) : Parcelable

@Parcelize
data class Users(
    var base: Int?=0
) : Parcelable

@Parcelize
data class AccountXX(
    var address: OwnerAddressModel?=OwnerAddressModel(),
    var logo: String?="",
    var logoURL: String?="",
    var name: String?="",
    var phone: String?="",
    var primaryEmail: String?=""
) : Parcelable

@Parcelize
data class Theme(
    var primary: String?="#4FD44C",
    var primaryLight: String?="#5f4cd44d"
) : Parcelable
