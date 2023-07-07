package com.max360group.cammax360.repository.models

data class IntegrationAllResponseModel(
    val `data`: IntegrationAllData,
    val message: String,
    val statusCode: Int
)

data class IntegrationAllData(
    val list: IntegrationList
)

data class IntegrationList(
    val addressTypes: List<IntegrationData>?= mutableListOf(),
    var chargeTypes: List<IntegrationData>?= mutableListOf(),
    var propertyTypes: List<IntegrationData>?= mutableListOf(),
    val unitTypes: List<IntegrationData>?= mutableListOf()
)