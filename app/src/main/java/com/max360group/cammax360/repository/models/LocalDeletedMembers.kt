package com.max360group.cammax360.repository.models

class LocalDeletedMembers(
  var deletedUsers:ArrayList<MembersData>?=ArrayList()
)

class MembersData(
    var mJobId:String="",
    var membersId:String=""
)