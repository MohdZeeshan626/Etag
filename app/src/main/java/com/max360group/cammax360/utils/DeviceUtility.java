package com.max360group.cammax360.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.max360group.cammax360.repository.models.DeviceModel;

import java.util.TimeZone;


/**
 * Created by Gurpreet on 10/02/21.
 */
public class DeviceUtility {

    private Context mContext;

    //create instance of DeviceUtility class
    private static DeviceUtility instance;

    private String mBrand;

    private String mModel;

    private String mAndroidId;

    private String mAndroidToken;

    private String mAndroidTimeZone;

    private DeviceUtility(Context context) {
        mContext = context;
        getValues();
    }

    private void getValues() {
        mBrand = Build.BRAND;
        mModel = Build.MODEL;
        mAndroidId = generateAndroidID();
        mAndroidToken = generateAndroidToken();
        mAndroidTimeZone = generateAndroidTimeZone();
    }

    public static DeviceUtility getInstance(Context context) {
        if (instance == null) {
            instance = new DeviceUtility(context);
        }
        return instance;
    }

    public String getAndroidId() {
        return mAndroidId;
    }

    private String generateAndroidID() {
        try {
            return Settings.Secure.getString(mContext.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    private String generateAndroidToken() {
        try {
            //Todo un-comment while getting device Token
//            Log.e("Device_token", FirebaseInstanceId.getInstance().getToken());
//            return FirebaseInstanceId.getInstance().getToken();
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String generateAndroidTimeZone() {
        try {
            Log.e("Device_time_zone", TimeZone.getDefault().getID());
            return TimeZone.getDefault().getID();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public DeviceModel createNewDevice() {
        DeviceModel deviceModel = new DeviceModel();
        deviceModel.setBrand(mBrand);
        deviceModel.setModel(mModel);
        deviceModel.setTimeZone(TimeZone.getDefault().getDisplayName());
        deviceModel.setAndroidId(mAndroidId);
        deviceModel.setTimeZone(mAndroidTimeZone);
        deviceModel.setAndroidToken(mAndroidToken);
        deviceModel.setDevice_type("A");
        return deviceModel;
    }

}
