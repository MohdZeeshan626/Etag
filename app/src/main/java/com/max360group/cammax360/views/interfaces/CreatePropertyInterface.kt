package com.max360group.cammax360.views.interfaces

import com.max360group.cammax360.repository.models.IntegrationData

interface CreatePropertyInterface {
    fun onLoadMore()
    fun onIntegrationTypeClick(integrationData: IntegrationData)
}