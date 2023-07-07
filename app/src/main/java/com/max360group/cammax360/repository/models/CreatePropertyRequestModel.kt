package com.max360group.cammax360.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreatePropertyRequestModel(
    val access: Access= Access(),
    val addresses: List<OwnerAddressModel>?= mutableListOf(),
    val billingAddress: OwnerAddressModel= OwnerAddressModel(),
    val chargeTypes: List<String>?= mutableListOf(),
    val comments: String="",
    val name: String="",
    val notes: List<String>?= mutableListOf(),
    val pic: String="",
    val primaryAddress: OwnerAddressModel= OwnerAddressModel(),
    val propertyTypes: List<String>?= mutableListOf(),
    val propertyUnits: List<String>?= mutableListOf(),
    val rm: RmFields= RmFields(),
    val shortName: String="",
    val squareFootage: String="",
    val taxId: String="",
    val userOwners: List<String>?= mutableListOf()
):Parcelable
