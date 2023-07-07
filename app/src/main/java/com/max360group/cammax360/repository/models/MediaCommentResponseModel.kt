package com.max360group.cammax360.repository.models

data class MediaCommentResponseModel(
    val `data`: MediaData,
    val message: String,
    val statusCode: Int
)

data class MediaData(
    val list: List<Comments>
)

data class Comments(
    val createdAt: String,
    val creatorId: CreatorId,
    val id: String,
    val isDeleted: Boolean,
    val jobId: String,
    val kind: String,
    val mediaId: String,
    val message: String,
    val primaryUserId: String,
    val subMediaId: String,
    val updatedAt: String
)

