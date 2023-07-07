package com.max360group.cammax360.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class PropertyDetailResponseModel(
    val `data`: PropertyData= PropertyData(),
    val message: String="",
    val statusCode: Int=0
)

@Parcelize
data class PropertyData(
    val record: PropertyDetail= PropertyDetail()
):Parcelable

data class PropertyRecord(
    val access: Access=Access(),
    val addresses: List<OwnerAddressModel>?= mutableListOf(),
    val billingAddress: OwnerAddressModel=OwnerAddressModel(),
    val chargeTypes: List<String>?= mutableListOf(),
    val comments: String="",
    val createdAt: String="",
    val creatorId: String="",
    val id: String="",
    val isActive: Boolean=false,
    val isDeleted: Boolean=false,
    val name: String="",
    val pic: String="",
    val picURL: String="",
    val primaryAddress: OwnerAddressModel= OwnerAddressModel(),
    val primaryUserId: String="",
    val propertyTypes: List<String>?= mutableListOf(),
    val propertyUnits: List<UnitRecord>?= mutableListOf(),
    val rm: Rm= Rm(),
    val shortName: String="",
    val squareFootage: Int=0,
    val taxId: String="",
    val updatedAt: String="",
    val userOwners: List<UserOwner>?= mutableListOf()
)