package com.max360group.cammax360.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.constraintlayout.widget.Constraints.TAG
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.max360group.cammax360.repository.models.AccountList
import com.max360group.cammax360.repository.models.model.BeforeAfterImageModel
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import java.text.SimpleDateFormat
import java.util.*


class ApplicationGlobal : Application() {

    companion object {
        var accessToken: String = ""
        var deviceLocale: String = ""
        var accountId: String = ""
        var inChatConversationId: String? = null
        var timeZone = ""
        var mJobUsersList = ArrayList<AccountList>()
        var mMediaList = ArrayList<BeforeAfterImageModel>()
        var currentDate = ""
        var beforeMonthDate = ""
        var organisationLogo = ""
        var context: Context? = null
        var mFcmToken = ""
        var mAndroidId = ""
    }


    override fun onCreate() {
        super.onCreate()
        // Initialize fresco
        Fresco.initialize(this)

        FirebaseApp.initializeApp(this)

        context = applicationContext

        // Get device locale
        deviceLocale = Locale.getDefault().language

        // Get session id
        accessToken = "Bearer " + UserPrefsManager(this).accessToken

        //Get account id
        if (UserPrefsManager(this).getAccount.isBlank()) {
            if (UserPrefsManager(this).isLogined) {
                accountId =
                    UserPrefsManager(this).loginedUser!!.accounts?.get(0)!!.primaryUserId!!.id!!
            }
        } else {
            accountId = UserPrefsManager(this).getAccount

        }

        // get timezone
        timeZone = TimeZone.getDefault().id

        //Get current date
        currentDate =
            SimpleDateFormat(Constants.DATE_FORMAT_SERVER_ISO, Locale.getDefault()).format(Date())

        TransferNetworkLossHandler.getInstance(applicationContext)

        //After one month date
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        val date = calendar.time
        val format = SimpleDateFormat(Constants.DATE_FORMAT_SERVER_ISO)
        beforeMonthDate = format.format(date)


        // Get fcmId
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            mFcmToken = task.result
            Log.e("kshkhxksahkx", "Fetching FCM registration token failed :  " + mFcmToken)

        })

        //Get android id
        mAndroidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.e("deviceId", "onCreate: "+ mAndroidId)

    }

}
