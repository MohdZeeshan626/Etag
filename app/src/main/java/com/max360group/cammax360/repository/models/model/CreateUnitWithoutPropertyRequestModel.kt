package com.max360group.cammax360.repository.models.model

import com.max360group.cammax360.repository.models.Access
import com.max360group.cammax360.repository.models.OwnerAddressModel
import com.max360group.cammax360.repository.models.RmFields

data class CreateUnitWithoutPropertyRequestModel(
    val access: Access= Access(),
    val addresses: List<OwnerAddressModel>?= mutableListOf(),
    val bathrooms: String="0",
    val bedrooms: String="0",
    val comments: String="",
    val name: String="",
    val notes: List<String>?= mutableListOf(),
    val primaryAddress: OwnerAddressModel= OwnerAddressModel(),
    val rm: RmFields= RmFields(),
    val squareFootage: String="0",
    val pic: String="",
    val unitTypes: List<String>?= mutableListOf()
)


