package com.max360group.cammax360.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.max360group.cammax360.R
import com.max360group.cammax360.utils.GeneralFunctions


class CheckInternetService : Service() {
    companion object {
        internal const val INTENT_SYNC_DATA = "syncData"
        var isConnected=false
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Get data from intent
        if (intent != null) {
            if (GeneralFunctions.isInternetConnected(this)) {
                // Send broadcast to sync data
                LocalBroadcastManager.getInstance(application)
                    .sendBroadcast(
                        Intent(
                            INTENT_SYNC_DATA
                        )
                    )
                if (!isConnected){
                    showToast(getString(R.string.st_online))
                    isConnected=true
                }

            } else {
                if (isConnected){
                    showToast(getString(R.string.st_offline))
                    isConnected=false
                }

            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun showToast(string: String) {
        val inflate = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflate.inflate(R.layout.custom_toast_layout, null)
        val tv = view.findViewById<View>(R.id.textView) as TextView
        tv.text = string
        val toast = Toast(this)
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
    }

}
