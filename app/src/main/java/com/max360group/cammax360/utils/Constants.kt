package com.max360group.cammax360.utils

import android.os.Environment

/**
 * Created by Mukesh on 20/7/18.
 */
object Constants {

    private const val APP_NAME = "Base Dev Story"

    // Date Time Format Constants
    const val DATE_FORMAT_SERVER = "yyyy-MM-dd"
    const val DATE_FORMAT_SERVER_GMT = "EEE MMM dd HH:mm:ss zzz yyyy"
    const val DATE_FORMAT_SERVER_ISO = "yyyy-MM-dd'T'HH:mm:ss"
    const val DATE_DISPLAY = "dd MMM"
    const val TIME_FORMAT_SERVER = "HH:mm"
    const val TIME_LOCAL = "hh:mm:ss"
    const val TIME_FORMAT_DISPLAY = "hh:mma"
    const val DATE_FORMAT_DISPLAY = "d MMM, yyyy"
    const val DATE_FORMAT_DISPLAY1 = "d MMM, yyyy hh:mma"
    const val DATE_FORMAT= "yyyy-MM-dd HH:mm"
    const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id="
    const val PLAY_STORE_MARKET_URL = "market://details?id="

    // Media Constants
    private val LOCAL_STORAGE_BASE_PATH_FOR_MEDIA = Environment
        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + APP_NAME
    val LOCAL_STORAGE_BASE_PATH_FOR_USER_PHOTOS = "$LOCAL_STORAGE_BASE_PATH_FOR_MEDIA/Users/Photos/"
    val LOCAL_STORAGE_BASE_PATH_FOR_QUESTION_PHOTOS =
        "$LOCAL_STORAGE_BASE_PATH_FOR_MEDIA/Questions/Media"
    val LOCAL_STORAGE_BASE_PATH_FOR_CHAT_MEDIA = "$LOCAL_STORAGE_BASE_PATH_FOR_MEDIA/Chats/"
    const val IMAGES_FOLDER = "Images/"
    const val VIDEOS_FOLDER = "Videos/"
    const val VIDEOS_THUMBS_FOLDER = "Thumbs/"
    const val DOCS_URL = "https://docs.google.com/viewer?url="
    const val PLACE_HOLDER_URL = "FieldMax360/development/623aa74abaadb23b955ede2b/job_623aa75fbaadb23b955ede3c/jobPhoto/Placeholder_1648117468725.jpg"

}