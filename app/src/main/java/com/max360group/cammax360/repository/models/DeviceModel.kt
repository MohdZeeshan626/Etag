package com.max360group.cammax360.repository.models

/**
 * Created by RaghuVirk on 15/3/18.
 */
class DeviceModel {
    var androidId: String? = null
    var androidToken: String? = null
    var model: String? = null
    var brand: String? = null
    var timeZone: String? = null
    var device_type: String? = null

    override fun equals(obj: Any?): Boolean {
        val deviceModel = obj as DeviceModel?
        return androidId == deviceModel!!.androidId
    }
}