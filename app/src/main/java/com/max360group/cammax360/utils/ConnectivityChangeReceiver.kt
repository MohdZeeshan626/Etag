package com.max360group.cammax360.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.max360group.cammax360.services.CheckInternetService


class ConnectivityChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Explicitly specify that which service class will handle the intent.
        context.startService(Intent(context, CheckInternetService::class.java))
    }


}