package com.max360group.cammax360.repository.networkrequests

import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.Error
import com.max360group.cammax360.repository.models.PojoNetworkResponse
import okhttp3.ResponseBody
import java.io.IOException

/**
 * Created by Mukesh on 20/7/18.
 */
object RetrofitRequest {

    fun checkForResponseCode(code: Int): PojoNetworkResponse {
        return when (code) {
            200 -> PojoNetworkResponse(isSuccess = true, isSessionExpired = false)
            201 -> PojoNetworkResponse(isSuccess = true, isSessionExpired = false)
            401 -> PojoNetworkResponse(isSuccess = false, isSessionExpired = true)
            else -> PojoNetworkResponse(isSuccess = false, isSessionExpired = false)
        }
    }

    fun getErrorMessage(responseBody: ResponseBody): String {
        val errorMessage = ""
        try {
            val errorConverter = RestClient.retrofitInstance!!
                .responseBodyConverter<Error>(Error::class.java, arrayOfNulls<Annotation>(0))
            return errorConverter.convert(responseBody)?.message ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return errorMessage
    }

    fun getRetrofitError(t: Throwable): Int {
        return if (t is IOException) {
            R.string.no_internet
        } else {
            R.string.retrofit_failure
        }
    }

}