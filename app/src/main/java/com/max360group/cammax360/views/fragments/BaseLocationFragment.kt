package com.max360group.cammax360.views.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import com.max360group.cammax360.utils.MarshMallowPermissions
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import java.lang.Exception


abstract class BaseLocationFragment : BaseFragment() {

    companion object {
        private const val RQ_LOCATION_SETTINGS = 328
        private const val UPDATE_INTERVAL = 60 * 1000L  /* 60 secs */
        private const val FASTEST_INTERVAL = 30 * 1000L /* 30 secs */
    }

    private val mMarshmallowPermissions: MarshMallowPermissions by lazy {
        MarshMallowPermissions(this)
    }

    private val mLocationRequest: LocationRequest by lazy {
        LocationRequest.create().apply {
            interval = UPDATE_INTERVAL
            fastestInterval = FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private lateinit var mLocationCallback: LocationCallback
    private val mFusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(activityContext)
    }

//    override fun init(savedInstanceState: Bundle?) {
//        // Ask for runtime permission
//        checkForLocationPermission()
//
//        setData(savedInstanceState)
//    }

    protected fun checkForLocationPermission() {
        if (mMarshmallowPermissions.isPermissionGrantedForLocation) {
            // Open dialog to check if device location settings are turned on
            checkForUserLocationSettings()
        } else {
            mMarshmallowPermissions.requestPermissionForLocation(
                DialogInterface.OnClickListener { _, _ ->
                    onAllLocationPermissionsGranted(false)
                })
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        // Get last known location
        mFusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            // Got last known location. In some rare situations this can be null.
            if (null != location) {
                onLocationUpdated(location)
            } else {
                // Initialize Location Callback
                mLocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        super.onLocationResult(locationResult)
                        locationResult?.let { result ->

                            onLocationUpdated(result.lastLocation)

                            // Remove Location updates
                            mFusedLocationProviderClient
                                .removeLocationUpdates(mLocationCallback)
                        }
                    }
                }

                // Request for location updates
                mFusedLocationProviderClient.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper()
                )
            }
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        try {
            if (requestCode == MarshMallowPermissions.RQ_LOCATION_PERMISSION) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onAllLocationPermissionsGranted(true)
                    getLocation()
                } else {
                    onAllLocationPermissionsGranted(false)
//                showMessage(R.string.enable_location_permission, null,
//                        false)
                }
            }
        }catch (E:Exception){

        }

    }

    private fun checkForUserLocationSettings() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest)
            .setNeedBle(true)
            .setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(activityContext as BaseAppCompactActivity)
            .checkLocationSettings(builder.build())

        result.addOnSuccessListener {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            getLocation()
        }
        result.addOnFailureListener {
            if (it is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    startIntentSenderForResult(
                        it.resolution.intentSender,
                        RQ_LOCATION_SETTINGS, null, 0, 0, 0, null
                    )
//                    it.startResolutionForResult(activityContext as BaseAppCompactActivity,
//                            RQ_LOCATION_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RQ_LOCATION_SETTINGS) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    getLocation()
                }
                Activity.RESULT_CANCELED -> {
                    onAllLocationPermissionsGranted(false)
                }
            }
        }

    }


    abstract fun onAllLocationPermissionsGranted(isLocationPermissionGranted: Boolean)
    abstract fun onLocationUpdated(location: Location)
}