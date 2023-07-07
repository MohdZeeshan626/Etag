package com.max360group.cammax360.repository.models

data class CreateJobResponseModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
)

data class Data(
    val job: JobCreated
)

data class JobCreated(
    val id: String,
)
