package com.max360group.cammax360.repository.models

data class CreateNotesResponseModel(
    val `data`: OwnerNotesData=OwnerNotesData(),
    val message: String="",
    val statusCode: Int=0
)

data class OwnerNotesData(
    val record: Note=Note()
)

data class Note(
    val createdAt: String="",
    val id: String="",
    val isDeleted: Boolean=false,
    val kind: String="",
    val medias: List<NotesMedia>?= mutableListOf(),
    val note: String="",
    val primaryUserId: String="",
    val updatedAt: String=""
)

data class NotesMedia(
    val id: String="",
    val kind: String="",
    val media: String="",
    val mediaURL: String="",
    val name: String="",
    val thumbnail: String="",
    val thumbnailURL: String=""
)