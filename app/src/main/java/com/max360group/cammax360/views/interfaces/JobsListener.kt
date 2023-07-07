package com.max360group.cammax360.views.interfaces

interface JobsListener {
    fun onItemClick(mJobId: String,mLocalId:String)
    fun onCameraClick(type: Int, position: Int, id: String, localId: Int)
    fun onApiCall()
    fun onMapCall(lat:Double,long:Double)

}