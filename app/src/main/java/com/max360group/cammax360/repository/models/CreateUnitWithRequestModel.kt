package com.max360group.cammax360.repository.models

data class CreateUnitWithRequestModel(
    val access: Access= Access(),
    val addresses: List<OwnerAddressModel>?= mutableListOf(),
    val bathrooms: String="",
    val bedrooms: String="",
    val comments: String="",
    val name: String="",
    val notes: List<String>?= mutableListOf(),
    val primaryAddress: OwnerAddressModel= OwnerAddressModel(),
    val propertyId: String?=null,
    val rm: RmFields= RmFields(),
    val squareFootage: String="",
    val pic: String="",
    val unitTypes: List<String>?= mutableListOf()
)


