package com.max360group.cammax360.repository.models

data class ConversationResponseModel(
    val `data`: ConversationData,
    val message: String,
    val statusCode: Int
)

data class ConversationData(
    val list: ArrayList<ConversationList>
)

data class ConversationList(
    val createdAt: String,
    val creatorId: CreatorId,
    val id: String,
    val isDeleted: Boolean,
    val jobId: String,
    val kind: String,
    val message: String,
    val primaryUserId: String,
    val updatedAt: String,
    val mediaId: Media
)



