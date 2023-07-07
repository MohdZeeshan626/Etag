package com.max360group.cammax360.repository.models

import com.max360group.cammax360.repository.models.model.User

data class JobDocsRequestModel(
    var kind: String="",
    var medias: List<MediaDocs>?= ArrayList()
)

data class MediaDocs(
    var name: String="",
    var subMedias: ArrayList<SubMediaDocs>?= ArrayList(),
    var tags: ArrayList<String>?= ArrayList()
)

data class SubMediaDocs(
    var media: String="",
    var name: String="",
    var tags: ArrayList<String>?= ArrayList(),
    var thumbnail: String="",
    var users: ArrayList<User>?= ArrayList()
)
