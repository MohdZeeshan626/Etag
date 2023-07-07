package com.max360group.cammax360.repository.models

data class UpdateMediaRequetsModel(
    val media: String,
    val name: String,
    val thumbnail: String="",
    val tags: List<String>
)