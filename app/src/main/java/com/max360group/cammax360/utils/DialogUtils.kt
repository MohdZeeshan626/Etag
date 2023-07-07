package com.max360group.cammax360.utils

import android.content.Context
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
import com.max360group.cammax360.R

/**
 * Created by Gurpreet on 10/2/21.
 */
object DialogUtils {


    /*    public static MaterialDialog getNetworkErrorDialogs(Context context) {
            return new MaterialDialog.Builder(context)
                    .title(context.getResources().getString(R.string.st_dialog_title_network_error))
                    .content(context.getResources().getString(R.string.st_dialog_message_network_error))
                    .positiveText(context.getResources().getString(android.R.string.ok))
                    .onPositive(((dialog, which) -> dialog.dismiss()))
                    .autoDismiss(true)
                    .build();
        }*/
    fun getConfirmationDialogs(
        context: Context,
        title: String?,
        message: String?,
        callback: SingleButtonCallback?
    ): MaterialDialog {
        return MaterialDialog.Builder(context)
            .title(title!!)
            .content(message!!)
            .positiveText(context.resources.getString(android.R.string.yes))
            .negativeText(context.resources.getString(android.R.string.no))
            .onNegative { dialog: MaterialDialog, which: DialogAction? -> dialog.dismiss() }
            .onPositive(callback!!)
            .autoDismiss(true)
            .build()
    }

    fun updateAvialableDialog(
        context: Context,
        callback: SingleButtonCallback?, callbackNagetive: SingleButtonCallback
    ): MaterialDialog {
        return MaterialDialog.Builder(context)
            .title(context.getString(R.string.new_update_avilable))
            .content(context.getString(R.string.update_message))
            .positiveText(context.resources.getString(R.string.update))
            .negativeText(context.resources.getString(R.string.no_thanks))
            .onNegative(callbackNagetive)
            .onPositive(callback!!)
            .cancelable(false)
            .autoDismiss(true)
            .build()
    }

    fun getInformationDialogs(
        context: Context,
        title: String?,
        message: String?,
        callback: SingleButtonCallback?
    ): MaterialDialog {
        return MaterialDialog.Builder(context) //                .title(title)
            .content(message!!)
            .positiveText(
                context.resources.getString(android.R.string.ok)
            ) //                .negativeText(context.getResources().getString(android.R.string.no))
            //                .onNegative(((dialog, which) -> dialog.dismiss()))
            .onPositive(callback!!)
            .autoDismiss(true)
            .build()
    }

    /*   public static MaterialDialog getLogoutDialogs(Context context, MaterialDialog.SingleButtonCallback inputCallback) {
        return new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.st_Logout))
                .content(context.getResources().getString(R.string.st_logout_desc))
                .positiveText(context.getResources().getString(R.string.st_Logout))
                .negativeText(context.getResources().getString(R.string.action_cancel))
                .onNegative(((dialog, which) -> dialog.dismiss()))
                .onPositive((inputCallback))
                .autoDismiss(false)
                .build();
    }*/
}