package com.max360group.cammax360.repository.models

data class UpdateAccountRequestModel(
    val address: OwnerAddressModel,
    val logo: String,
    val name: String,
    val phone: String,
    val primaryEmail: String,
    val theme: Theme
)