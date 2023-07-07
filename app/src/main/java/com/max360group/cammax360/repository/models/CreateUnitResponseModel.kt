package com.max360group.cammax360.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreateUnitResponseModel(
    val `data`: UnitData,
    val message: String,
    val statusCode: Int
):Parcelable

@Parcelize
data class UnitData(
    val record: UnitRecord= UnitRecord()
):Parcelable

@Parcelize
data class UnitRecord(
    val access: Access=Access(),
    val addresses: List<OwnerAddressModel>?= mutableListOf(),
    val bathrooms: Int=0,
    val bedrooms: Int=0,
    val comments: String="",
    val createdAt: String="",
    val creatorId: String="",
    val id: String="",
    val isActive: Boolean=false,
    val isDefault: Boolean=false,
    val isDeleted: Boolean=false,
    val name: String="",
    val pic: String="",
    val picURL: String="",
    val primaryAddress: OwnerAddressModel=OwnerAddressModel(),
    val primaryUserId: String="",
    val propertyId: String="",
    val rm: Rm=Rm(),
    val squareFootage: Int=0,
    val unitTypes: List<String>?= mutableListOf(),
    val updatedAt: String="",
    var isChecked: Boolean=false,
):Parcelable

