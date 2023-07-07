package com.max360group.cammax360.repository.models

data class UpdateVideoRequestModel(
    val media: String,
    val name: String,
    val tags: List<String>,
    val thumbnail:String= ""
)