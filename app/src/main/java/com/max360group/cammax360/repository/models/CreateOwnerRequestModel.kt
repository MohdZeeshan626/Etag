package com.max360group.cammax360.repository.models

import android.os.Parcelable
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreateOwnerRequestModel(
    val addresses: List<OwnerAddressModel>? = mutableListOf(),
    val billingAddress: OwnerAddressModel? = OwnerAddressModel(),
    val createUser: Boolean = false,
    val email: String = "",
    val emails: List<EmailsModel>? = mutableListOf(),
    val firstName: String = "",
    val lastName: String = "",
    val comments: String = "",
    val taxId: String = "",
    val notes: List<String>? = mutableListOf(),
    val phoneNumbers: List<PhoneNumberModel>? = mutableListOf(),
    val primaryAddress: OwnerAddressModel? = OwnerAddressModel(),
    val pic: String = "",
    val properties: List<PropertyUnit>? = mutableListOf(),
    val rm: RmFields = RmFields(),
    val sendInvite: Boolean = false,
    var access: Access = Access()
) : Parcelable

@Parcelize
data class Access(
    val all: Boolean = false,
    val users: List<String>? = mutableListOf()
) : Parcelable

@Parcelize
data class PropertyUnit(
    var propertyId: String = "",
    var propertyUnits: ArrayList<String>? = ArrayList()
) : Parcelable

@Parcelize
data class RmFields(
    val LocationID: Int = -1,
    val OwnerID: Int = -1,
    val displayName: String = "",
    val enabled: Boolean = true
) : Parcelable


