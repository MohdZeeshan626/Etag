package com.max360group.cammax360.repository.networkrequests

/**
 * Created by Mukesh on 20/7/18.
 */
object RetrofitUtils {

    private const val MULTIPART_FORM_DATA = "multipart/form-data"

//    fun stringToRequestBody(string: String): RequestBody {
//        return RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), string)
//    }
//
//    fun imageToRequestBody(file: File): RequestBody {
//        return RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file)
//    }
//
//    fun createPartFromFile(partName: String, file: File): MultipartBody.Part {
//        // create RequestBody instance from file
//        val requestFile = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file)
//
//        // MultipartBody.Part is used to send also the actual file name
//        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
//    }

}