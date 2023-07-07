package com.max360group.cammax360.views.interfaces

import com.max360group.cammax360.repository.models.ConversationList
import com.max360group.cammax360.repository.models.JobMediaList
import com.max360group.cammax360.repository.models.Media

interface JobsLDetailListener {
    fun onItemClick(jobMediaList: JobMediaList, mType: Int)
    fun onVisibilityClick(jobMediaList: JobMediaList, mType: Int)
    fun onBeforeAfterClick()
    fun onCommentsMediaClick(conversationList: ConversationList)
    fun onTimelineClick(media: Media)

}