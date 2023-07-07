package com.max360group.cammax360.repository.models

import kotlinx.android.parcel.Parcelize

data class UpdateJobDescriptionRequestModel(
    val address: AddressRequest,
    val endDt: String,
    val startDt: String,
    val title: String,
    val propertyId: String? =null,
    val propertyUnitId: String
)
