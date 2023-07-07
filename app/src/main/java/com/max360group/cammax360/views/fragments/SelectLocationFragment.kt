package com.max360group.cammax360.views.fragments

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.pukka.views.adapters.JobSearchLocationAdapter
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.PropertyDetail
import com.max360group.cammax360.repository.models.PropertyLocationData
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.LocationListenerViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.activities.doFragmentTransaction
import com.max360group.cammax360.views.calender.MonthlyActivity
import com.max360group.cammax360.views.fragments.EditJobDetailFragment.Companion.BUNDLE_INTENT_EDIT_JOB
import com.max360group.cammax360.views.fragments.EditJobDetailFragment.Companion.BUNDLE_LATITUDE
import com.max360group.cammax360.views.fragments.EditJobDetailFragment.Companion.BUNDLE_LONGITUDE
import com.max360group.cammax360.views.fragments.EditJobDetailFragment.Companion.BUNDLE_PROPERTY_ID
import com.max360group.cammax360.views.fragments.EditJobDetailFragment.Companion.BUNDLE_PROPERTY_UNIT_ID
import kotlinx.android.synthetic.main.fragment_choose_location.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.IOException
import java.util.*


class SelectLocationFragment : BaseLocationFragment(), OnMapReadyCallback,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraIdleListener, LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    View.OnClickListener, JobSearchLocationAdapter.SearchListener {

    companion object {
        const val GOOGLE_PLACES_API_KEY = "AIzaSyDXtuHWBP0hJufjgDKfklVXb4f-7csKldw"
        const val BUNDLE_CREATE = 0
        const val BUNDLE_UPDATE = 1
        const val BUNDLE_FROM = "from"
        var propertyId = ""
        var propertyUnitId = ""
        var placeName = ""
        var state = ""
        var country = ""
        var city = ""
        var mLatitude = 0.0
        var mLongitude = 0.0

        fun newInstance(mFrom: Int): SelectLocationFragment {
            val mFragment = SelectLocationFragment()
            val mBundle = Bundle()
            mBundle.putInt(BUNDLE_FROM, mFrom)
            mFragment.arguments = mBundle
            return mFragment
        }
    }

    var mMap: GoogleMap? = null
    var mLocationName = ""
    var mFrom = 0
    var searchValue = ""
    lateinit var placesClient: PlacesClient


    private val mSearchLocationAdapter by lazy {
        JobSearchLocationAdapter(this)
    }

    private val mLocationListenerViewModel by lazy {
        ViewModelProvider(this).get(LocationListenerViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_choose_location

    override fun init(savedInstanceState: Bundle?) {
        // Set toolbar
        toolbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_primary)
        ivToolbarUserImage.setImageURI(mUserPrefsManager.loginedUser!!.picURL)

        Glide
            .with(requireContext())
            .load(ApplicationGlobal.organisationLogo)
            .placeholder(R.drawable.ic_mimi_logo)
            .into(ivToolbarLeftIcon)


        // Register receiver for updating profile
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                mGetUpdateDataBroadcastReceiver,
                IntentFilter(OwnerPropertiesFragment.INTENT_OWNER_PROPERTY)
            )

        //Get arguments
        mFrom = arguments?.getInt(BUNDLE_FROM)!!

        //Initialize place picker
        Places.initialize(requireContext(), GOOGLE_PLACES_API_KEY)
        placesClient = Places.createClient(requireContext())

        //Set adapter
        rvLocationList.adapter = mSearchLocationAdapter

        // Set map view
        Handler().postDelayed({
            fgGoogleMap?.onCreate(savedInstanceState)
            fgGoogleMap?.getMapAsync(this)
            fgGoogleMap.onStart()

            // Allow permissions
            checkForLocationPermission()
        }, 300)

        // set click listener
        btnChooseLocation.setOnClickListener(this)
        ivToolbarUserImage.setOnClickListener(this)
        ivToolbarRightIcon.setOnClickListener(this)
        ivToolbarRightIconBell.setOnClickListener(this)

        //Search listener
        etSearchLocation.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    rvLocationList.visibility = View.GONE
                    pbBar.visibility = View.GONE
                } else {
                    val mSearch = s.toString()
                    pbBar.visibility = View.VISIBLE
                    searchValue = mSearch
                    mLocationListenerViewModel.getLocationPlaces(mSearch, true)
                }
            }
        })
    }

    override fun onAllLocationPermissionsGranted(isLocationPermissionGranted: Boolean) {

    }

    override fun onLocationUpdated(location: Location) {
        // Get Location
        mLatitude = location.latitude
        mLongitude = location.longitude

        // get Address
        mLocationName = GeneralFunctions.getAddress(mLatitude, mLongitude, requireContext())
//        tvLocationName.text = mLocationName

        // Animate to location
        if (mMap != null) {
            mapInit(mMap!!)
        }
    }

    override val viewModel: BaseViewModel?
        get() = mLocationListenerViewModel

    override fun observeProperties() {
        mLocationListenerViewModel.onGetPropertyLocation()
            .observe(this, androidx.lifecycle.Observer {
                if (it.isEmpty()) {
                    rvLocationList.visibility = View.GONE
                } else {
                    rvLocationList.visibility = View.VISIBLE
                }
                pbBar.visibility = View.GONE

                mSearchLocationAdapter.updateData(it)

            })


    }

    override fun onCameraMove() {
        // Clear map
        mMap!!.clear()
        // display imageView
        imgLocationPinUp?.visibility = View.VISIBLE

    }

    override fun onCameraIdle() {
        // Get the location on camera camera move
        imgLocationPinUp?.visibility = View.VISIBLE
        mLatitude = mMap!!.cameraPosition.target.latitude
        mLongitude = mMap!!.cameraPosition.target.longitude

        // get Address
        mLocationName = GeneralFunctions.getAddress(mLatitude, mLongitude, requireContext())
        tvLocationName.text = mLocationName
    }

    override fun onMapReady(mGoogleMap: GoogleMap?) {
        mMap = mGoogleMap
    }

    private fun mapInit(googleMap: GoogleMap) {
        googleMap.apply {
            val value = LatLng(mLatitude, mLongitude)
            addMarker(MarkerOptions().apply {
                position(value)
                draggable(false)
            })
            // setup zoom level
            animateCamera(CameraUpdateFactory.newLatLngZoom(value, 14f))
            // maps events we need to respond to
            setOnCameraMoveListener(this@SelectLocationFragment)
            setOnCameraIdleListener(this@SelectLocationFragment)
        }
    }

    override fun onLocationChanged(p0: Location) {

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onConnected(p0: Bundle?) {

    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnChooseLocation -> {
                if (mLatitude != 0.0 && mLongitude != 0.0) {
                    if (mFrom == BUNDLE_CREATE) {
                        (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                            fragment = AddMembersFragment.newInstance(mLatitude, mLongitude),
                            containerViewId = R.id.flFragContainerMain,
                            enterAnimation = R.animator.slide_right_in,
                            popExitAnimation = R.animator.slide_right_out
                        )
                    } else {
                        //Send broadcast to update photo detail
                        LocalBroadcastManager.getInstance(requireContext())
                            .sendBroadcast(
                                Intent(BUNDLE_INTENT_EDIT_JOB).putExtra(BUNDLE_LATITUDE, mLatitude)
                                    .putExtra(BUNDLE_LONGITUDE, mLongitude)
                                    .putExtra(BUNDLE_PROPERTY_ID, propertyId)
                                    .putExtra(BUNDLE_PROPERTY_UNIT_ID, propertyUnitId)
                            )
                        (requireContext() as BaseAppCompactActivity).onBackPressed()
                    }
                } else {
                    showMessage(null, getString(R.string.st_invalid_address))
                }
            }
            R.id.ivToolbarUserImage -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = AccountsFragment.newInstance(AccountsFragment.BUNDLE_NORMAl_VIEWS),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
            R.id.ivToolbarRightIcon -> {
                startActivity(Intent(requireContext(), MonthlyActivity::class.java))
            }
            R.id.ivToolbarRightIconBell -> {
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = NotificationFragment(),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )
            }
        }
    }

    override fun onLocationClick(placeId: String, address: String) {
        //Get latitude and longitude from place id
        rvLocationList.visibility = View.GONE
        val placeFields = listOf(Place.Field.ID, Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude
                etSearchLocation.setText("")
                // Animate to location
                if (mMap != null) {
                    mapInit(mMap!!)
                }
                hideSoftKeyboard()
            }.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    Log.e(TAG, "Place not found: ${exception.message}")
                    val statusCode = exception.statusCode
                }
            }
    }

    override fun onPropertyClick(mPropertyLocationData: PropertyLocationData) {
        propertyId = mPropertyLocationData.propertyId
        if (mPropertyLocationData.propertyUnits!!.isNotEmpty()) {
            propertyUnitId = mPropertyLocationData.propertyUnits!![0].id
        }
        if (mPropertyLocationData.propertyAddress.location.coordinates!!.isNotEmpty()) {
            placeName = mPropertyLocationData.propertyAddress.formatted
            city = mPropertyLocationData.propertyAddress.city
            state = mPropertyLocationData.propertyAddress.state!!
            country = mPropertyLocationData.propertyAddress.country

            //Check lat long is exist or not
            if (mPropertyLocationData.propertyAddress.location.coordinates!![0] == 0.0 &&
                mPropertyLocationData.propertyAddress.location.coordinates!![1] == 0.0
            ) {
                //Get reverse geocoding from location name
                if (GeneralFunctions.getReverseGeoCoding(
                        mPropertyLocationData.propertyAddress.formatted,
                        requireContext()
                    ).isEmpty()
                ) {
                    showMessage(null, getString(R.string.st_invalid_address))
                    mLatitude=0.0
                    mLongitude=0.0
                } else {
                    mLatitude = GeneralFunctions.getReverseGeoCoding(
                        mPropertyLocationData.propertyAddress.formatted,
                        requireContext()
                    )[0]
                    mLongitude = GeneralFunctions.getReverseGeoCoding(
                        mPropertyLocationData.propertyAddress.formatted,
                        requireContext()
                    )[1]
                }

            } else {
                mLatitude = mPropertyLocationData.propertyAddress.location.coordinates!![0]
                mLongitude = mPropertyLocationData.propertyAddress.location.coordinates!![1]

            }
        }

        tvLocationName.text = placeName
        etSearchLocation.setText("")
        // Animate to location
        if (mMap != null) {
            mapInit(mMap!!)
        }
        hideSoftKeyboard()
    }

    override fun onCreateProperty(placeId: String) {
        val placeFields = listOf(Place.Field.ID, Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude

                // Animate to location
                if (mMap != null) {
                    mapInit(mMap!!)
                }

                //Do fragment transaction
                (activityContext as BaseAppCompactActivity).doFragmentTransaction(
                    fragment = CreatePropertyFragment.newInstance(
                        "",
                        from = CreatePropertyFragment.BUNDLE_CREATE_JOB
                    ),
                    containerViewId = R.id.flFragContainerMain,
                    enterAnimation = R.animator.slide_right_in,
                    popExitAnimation = R.animator.slide_right_out
                )

                hideSoftKeyboard()
                rvLocationList.visibility = View.GONE
            }.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    Log.e(TAG, "Place not found: ${exception.message}")
                    val statusCode = exception.statusCode
                }
            }
    }

    private val mGetUpdateDataBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            context?.let {
                try {
                    if (p1?.getIntExtra(OwnerPropertiesFragment.INTENT_BROADCAST_TYPE, 0) == 2) {
                        val mData =
                            p1.getParcelableExtra<PropertyDetail>(OwnerPropertiesFragment.INTENT_PROPERTY)
                        propertyId = mData!!.id
                        placeName = mData.primaryAddress.formatted
                        city = mData.primaryAddress.city
                        state = mData.primaryAddress.state!!
                        country = mData.primaryAddress.country
                        if (mData.propertyUnits!!.isNotEmpty()) {
                            propertyUnitId = mData.propertyUnits!![0].id
                        }
                    }

                } catch (e: Exception) {

                }

            }
        }
    }

}
