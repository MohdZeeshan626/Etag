package com.max360group.cammax360.utils

import android.os.AsyncTask
import org.jsoup.Jsoup
import java.io.IOException

/**
 * Created by Gurpreet on 10/02/21.
 */
class VersionChecker : AsyncTask<String?, String?, String?>() {
    private var newVersion: String? = null

    override fun doInBackground(vararg p0: String?): String? {
        try {
            newVersion =
                Jsoup.connect("https://play.google.com/store/apps/details?id="
                        + "com.devstory.base" + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6)" +
                            "Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select(".hAyfc .htlgb")[7]
                    .ownText()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return newVersion
    }
}