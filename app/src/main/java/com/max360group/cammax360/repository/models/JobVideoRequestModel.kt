package com.max360group.cammax360.repository.models

import com.max360group.cammax360.repository.models.model.User

data class JobVideoRequestModel(
    var kind: String="",
    var medias: List<MediaVideo>?= ArrayList()
)

data class MediaVideo(
    var name: String="",
    var subMedias: ArrayList<SubMediaVideo>?= ArrayList(),
    var tags: ArrayList<String>?= ArrayList()
)

data class SubMediaVideo(
    var media: String="",
    var name: String="",
    var tags: ArrayList<String>?= ArrayList(),
    var thumbnail: String="",
    var users: ArrayList<User>?= ArrayList()
)
