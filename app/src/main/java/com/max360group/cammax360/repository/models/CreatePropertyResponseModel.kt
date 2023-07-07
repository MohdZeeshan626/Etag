package com.max360group.cammax360.repository.models

data class CreatePropertyResponseModel(
    val `data`: CreatePropertyData,
    val message: String,
    val statusCode: Int
)

data class CreatePropertyData(
    val record: PropertyDetail
)