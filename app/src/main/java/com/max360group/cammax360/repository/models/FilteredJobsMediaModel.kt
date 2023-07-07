package com.max360group.cammax360.repository.models

class FilteredJobsMediaModel(
    var date:String?="",
    var isChecked:Boolean=false,
    var mJobMediaList:ArrayList<JobMediaList>?=ArrayList<JobMediaList>()

)