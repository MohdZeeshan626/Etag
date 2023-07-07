package com.max360group.cammax360.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.preferences.UserPrefsManager
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.views.activities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.HomeActivity
import com.max360group.cammax360.views.activities.NotificationActivity


class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {

        // Push notification types
        const val PUSH_TYPE_JOB_CREATE = "JobCreate"
        const val PUSH_TYPE_JOB_INVITE = "JobInvite"
        const val PUSH_TYPE_JOB_EDIT = "JobDetailEdit"
        const val PUSH_TYPE_VIDEO_CREATED = "VideoCreate"
        const val PUSH_TYPE_PHOTO_CREATED = "PhotoCreate"
        const val PUSH_TYPE_DOC_CREATED = "DocCreate"
        const val PUSH_TYPE_COMMENT_CREATED = "CommentCreate"
        const val PUSH_TYPE_CONVERSATION_CREATE = "ConversationCreate"
        const val PUSH_TYPE_PHOTO_SUB_MEDIA_LINK_CREATE = "PhotoSubMediaLinkCreate"

        private const val PARAM_KEY_PUSH_TYPE = "kind"
        private const val PARAM_KEY_MESSAGE_TO_DISPLAY = "title"
        private const val PARAM_TYPE_CONTRACT_ID = "contract_id"
        private const val PARAM_KEY_USER = "user"
        private const val PARAM_KEY_POST = "post"
        private const val PARAM_KEY_CONVERSATION = "conversation"
        private const val PARAM_KEY_UNREAD_NOTIFICATIONS_COUNT = "unread_notifications_count"
        private const val PARAM_KEY_UNREAD_CONVERSATIONS_COUNT = "unread_conversations_count"


        // Notification channel data
        private const val PACKAGE_NAME = "com.orvitas.salsa"

        private const val CHANNEL_ID_NEW_OFFER_NOTIFICATIONS = "$PACKAGE_NAME.newOfferNotifications"
        private const val CHANNEL_NAME_NEW_OFFER_NOTIFICATIONS = "New Offer"

        private const val CHANNEL_ID_CONTRCT_REMINDER_NOTIFICATIONS =
            "$PACKAGE_NAME.contractReminderNotifications"
        private const val CHANNEL_NAME_CONTRCT_REMINDER_NOTIFICATIONS = "Contract Reminder"

        private const val CHANNEL_ID_ADMIN_NOTIFICATIONS = "$PACKAGE_NAME.adminNotifications"
        private const val CHANNEL_NAME_ADMIN_NOTIFICATIONS = "Admin"

    }


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token)
    }

    private var mNotificationManager: NotificationManager? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("remoteData", "onMessageReceived: " + remoteMessage.data)

        val mapData = remoteMessage.data
        if (null != mapData && mapData.isNotEmpty()) {

            if (null == mNotificationManager) {
                mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager
            }

            if (mapData.containsKey(PARAM_KEY_PUSH_TYPE) &&
                mapData.containsKey(PARAM_KEY_MESSAGE_TO_DISPLAY)
            ) {
                if (UserPrefsManager(this).isLogined) {
                    when (mapData[PARAM_KEY_PUSH_TYPE]) {
                        PUSH_TYPE_JOB_CREATE -> {
                            sendNotification(
                                mapData[PARAM_TYPE_CONTRACT_ID] ?: "0",
                                mapData[PARAM_KEY_MESSAGE_TO_DISPLAY] ?: "",
                                CHANNEL_ID_NEW_OFFER_NOTIFICATIONS,
                                CHANNEL_NAME_NEW_OFFER_NOTIFICATIONS
                            )
                        }
                        PUSH_TYPE_JOB_INVITE -> {
                            sendNotification(
                                mapData[PARAM_TYPE_CONTRACT_ID] ?: "0",
                                mapData[PARAM_KEY_MESSAGE_TO_DISPLAY] ?: "",
                                CHANNEL_ID_CONTRCT_REMINDER_NOTIFICATIONS,
                                CHANNEL_NAME_CONTRCT_REMINDER_NOTIFICATIONS
                            )
                        }
                        PUSH_TYPE_JOB_EDIT -> {
                            sendNotification(
                                mapData[PARAM_TYPE_CONTRACT_ID] ?: "0",
                                mapData[PARAM_KEY_MESSAGE_TO_DISPLAY] ?: "",
                                CHANNEL_ID_CONTRCT_REMINDER_NOTIFICATIONS,
                                CHANNEL_NAME_CONTRCT_REMINDER_NOTIFICATIONS
                            )
                        }
                        PUSH_TYPE_VIDEO_CREATED -> {
                            sendNotification(
                                mapData[PARAM_TYPE_CONTRACT_ID] ?: "0",
                                mapData[PARAM_KEY_MESSAGE_TO_DISPLAY] ?: "",
                                CHANNEL_ID_CONTRCT_REMINDER_NOTIFICATIONS,
                                CHANNEL_NAME_CONTRCT_REMINDER_NOTIFICATIONS
                            )
                        }
                        PUSH_TYPE_PHOTO_CREATED -> {
                            sendNotification(
                                mapData[PARAM_TYPE_CONTRACT_ID] ?: "0",
                                mapData[PARAM_KEY_MESSAGE_TO_DISPLAY] ?: "",
                                CHANNEL_ID_CONTRCT_REMINDER_NOTIFICATIONS,
                                CHANNEL_NAME_CONTRCT_REMINDER_NOTIFICATIONS
                            )
                        }
                        PUSH_TYPE_DOC_CREATED -> {
                            sendNotification(
                                mapData[PARAM_TYPE_CONTRACT_ID] ?: "0",
                                mapData[PARAM_KEY_MESSAGE_TO_DISPLAY] ?: "",
                                CHANNEL_ID_CONTRCT_REMINDER_NOTIFICATIONS,
                                CHANNEL_NAME_CONTRCT_REMINDER_NOTIFICATIONS
                            )
                        }
                        PUSH_TYPE_COMMENT_CREATED -> {
                            sendNotification(
                                mapData[PARAM_TYPE_CONTRACT_ID] ?: "0",
                                mapData[PARAM_KEY_MESSAGE_TO_DISPLAY] ?: "",
                                CHANNEL_ID_CONTRCT_REMINDER_NOTIFICATIONS,
                                CHANNEL_NAME_CONTRCT_REMINDER_NOTIFICATIONS
                            )
                        }
                        PUSH_TYPE_CONVERSATION_CREATE -> {
                            sendNotification(
                                mapData[PARAM_TYPE_CONTRACT_ID] ?: "0",
                                mapData[PARAM_KEY_MESSAGE_TO_DISPLAY] ?: "",
                                CHANNEL_ID_CONTRCT_REMINDER_NOTIFICATIONS,
                                CHANNEL_NAME_CONTRCT_REMINDER_NOTIFICATIONS
                            )
                        }
                        else -> {
                            sendGeneralNotification(
                                mapData[PARAM_KEY_MESSAGE_TO_DISPLAY] ?: "",
                                CHANNEL_ID_ADMIN_NOTIFICATIONS,
                                CHANNEL_NAME_ADMIN_NOTIFICATIONS
                            )
                        }
                    }
                } else {
                    sendGeneralNotification(
                        mapData[PARAM_KEY_MESSAGE_TO_DISPLAY] ?: "",
                        CHANNEL_ID_ADMIN_NOTIFICATIONS,
                        CHANNEL_NAME_ADMIN_NOTIFICATIONS
                    )
                }
            }
        }
    }

    private fun sendNotification(
        contractId: String, messageToDisplay: String,
        channelId: String, channelName: String
    ) {
        val intent = Intent(this@MyFirebaseMessagingService, NotificationActivity::class.java)
        intent.putExtra(BaseAppCompactActivity.INTENT_EXTRAS_IS_FROM_NOTIFICATION,true)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        mNotificationManager?.notify(
            contractId.toInt(), getNotification(
                contentMessage = messageToDisplay,
                pendingIntent = PendingIntent
                    .getActivity(
                        this@MyFirebaseMessagingService, contractId.toInt(),
                        intent, PendingIntent.FLAG_UPDATE_CURRENT
                    ),
                channelId = channelId,
                channelName = channelName
            )
        )
    }

//    private fun sendMessageNotification(messageJSONString: String, messageToDisplay: String,
//                                        unreadConvCount: String, channelId: String,
//                                        channelName: String) {
//
//        Gson().fromJson(messageJSONString, Conversation::class.java)?.let { conversation ->
//            with(conversation) {
//                if (id != ApplicationGlobal.inChatConversationId) {
//
//                    val intent = Intent(this@MyFirebaseMessagingService,
//                            ChatActivity::class.java)
//                            .putExtra(ChatActivity.INTENT_EXTRAS_CONVERSATION, conversation)
//                            .putExtra(BaseAppCompactActivity.INTENT_EXTRAS_IS_FROM_NOTIFICATION, true)
//                    intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//
//                    mNotificationManager?.notify(id, getNotification(contentMessage = messageToDisplay,
//                            pendingIntent = PendingIntent
//                                    .getActivity(this@MyFirebaseMessagingService,
//                                            id, intent, PendingIntent.FLAG_UPDATE_CURRENT),
//                            channelId = channelId,
//                            channelName = channelName))
//
////                // Send broadcast to update unread conversations count
////                val unreadConvCount = data[PARAM_KEY_UNREAD_CONVERSATIONS_COUNT] ?: "0"
////                LocalBroadcastManager.getInstance(this).sendBroadcast(
////                        Intent(EditProfileFragment.INTENT_FILTER_UPDATE_PROFILE)
////                                .putExtra(ProfileFragment.INTENT_EXTRAS_UNREAD_CONV_COUNT,
////                                        if (unreadConvCount.isEmpty()) 0 else unreadConvCount.toInt()))
//
//                }
//            }
//        }
//    }

    private fun sendGeneralNotification(
        messageToDisplay: String,
        channelId: String, channelName: String
    ) {

        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        mNotificationManager?.notify(
            0, getNotification(
                contentMessage = messageToDisplay,
                pendingIntent = PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                ),
                channelId = channelId,
                channelName = channelName
            )
        )
    }

    @SuppressLint("NewApi")
    private fun getNotification(
        contentTitle: String = getString(R.string.app_name),
        contentMessage: String, pendingIntent: PendingIntent,
        channelId: String, channelName: String
    ): Notification {

//        val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
//                packageName + "/" + R.raw.notification_sound)

        if (isOreoDevice && null == mNotificationManager?.getNotificationChannel(channelId)) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName, NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.setShowBadge(true)
//            notificationChannel.setSound(sound, AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_NOTIFICATION).build())
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            mNotificationManager?.createNotificationChannel(notificationChannel)
        }
        return NotificationCompat.Builder(
            this, channelId
        )
            .setContentTitle(contentTitle)
            .setContentText(contentMessage)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(contentMessage)
            )
            .setSmallIcon(getNotificationIcon())
            .setTicker(contentTitle)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                .setSound(sound)
            .setDefaults(Notification.DEFAULT_LIGHTS and Notification.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setWhen(System.currentTimeMillis())
            .setContentIntent(pendingIntent)
            .setAutoCancel(true).build()
    }

    private val isOreoDevice: Boolean
        get() = android.os.Build.VERSION_CODES.O <= android.os.Build.VERSION.SDK_INT

    private fun getNotificationIcon(): Int {
        return if (GeneralFunctions.isAboveLollipopDevice)
            R.mipmap.ic_launcher
        else
            R.mipmap.ic_launcher
    }
}