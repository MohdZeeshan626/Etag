package com.max360group.cammax360.repository.models

data class OwnerCreateNoteRequestModel(
    val kind: String="userOwners",
    val userOwnerId: String?=null,
    val propertyId: String?=null,
    val propertyUnitId: String?=null,
    val medias: List<NoteMedia>?= mutableListOf(),
    val note: String=""
)

data class NoteMedia(
    val kind: String="",
    var media: String="",
    var thumbnail: String="",
    var name: String=""
)