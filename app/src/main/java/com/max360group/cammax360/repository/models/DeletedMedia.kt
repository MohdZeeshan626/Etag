package com.max360group.cammax360.repository.models

class DeletedMedia(
    var deletedMedia:ArrayList<MediaIds>?= ArrayList()
)
class MediaIds(
    var mJobId:String="",
    var kind:String="",
    var idsList:ArrayList<String>?=ArrayList<String>()
)