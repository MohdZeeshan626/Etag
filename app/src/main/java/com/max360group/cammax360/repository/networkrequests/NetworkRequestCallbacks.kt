package com.max360group.cammax360.repository.networkrequests

import retrofit2.Response


interface NetworkRequestCallbacks {

    fun onSuccess(response: Response<*>)

    fun onError(t: Throwable)

}