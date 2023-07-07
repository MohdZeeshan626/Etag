package com.max360group.cammax360.repository.models

data class NotesHistoryResponseModel(
    val `data`: NotesData,
    val message: String,
    val statusCode: Int
)

data class NotesData(
    val records: List<Note>
)
