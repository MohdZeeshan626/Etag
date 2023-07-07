package com.max360group.cammax360.views.fragments

import android.content.ContentValues
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.pukka.views.adapters.SearchLocationAdapter
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.OwnerAddressModel
import com.max360group.cammax360.repository.models.PropertyLocationData
import com.max360group.cammax360.utils.ApplicationGlobal
import com.max360group.cammax360.utils.GeneralFunctions
import com.max360group.cammax360.viewmodels.BaseViewModel
import com.max360group.cammax360.viewmodels.LocationListenerViewModel
import com.max360group.cammax360.views.activities.BaseAppCompactActivity
import com.max360group.cammax360.views.fragments.OwnerGeneralInfoFragment.Companion.INTENT_GENERAL_INFO
import com.max360group.cammax360.views.fragments.SelectLocationFragment.Companion.GOOGLE_PLACES_API_KEY
import kotlinx.android.synthetic.main.fragment_add_address.*
import kotlinx.android.synthetic.main.fragment_choose_location.fgGoogleMap
import kotlinx.android.synthetic.main.fragment_choose_location.rvLocationList
import kotlinx.android.synthetic.main.fragment_choose_location.pbBar
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*
import kotlin.collections.ArrayList

class AddAddressFragment : BaseLocationFragment(), OnMapReadyCallback,
    SearchLocationAdapter.SearchListener, View.OnClickListener {

    companion object {
        const val INTENT_ADDRESS_DATA = "addressData"

        fun newInstance(mOwnerAddressModel: OwnerAddressModel): AddAddressFragment {
            val mFragment = AddAddressFragment()
            val bundle = Bundle()
            bundle.putParcelable(INTENT_ADDRESS_DATA, mOwnerAddressModel)
            mFragment.arguments = bundle
            return mFragment
        }
    }

    var mMap: GoogleMap? = null
    var mLatitude = 0.0
    var mLongitude = 0.0
    var mOwnerAddressModel = OwnerAddressModel()
    lateinit var placesClient: PlacesClient
    private var isAddressSelected=false

    private val mSearchLocationAdapter by lazy {
        SearchLocationAdapter(this)
    }

    private val mLocationListenerViewModel by lazy {
        ViewModelProvider(this).get(LocationListenerViewModel::class.java)
    }

    override val layoutId: Int
        get() = R.layout.fragment_add_address

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

        //Get arguments
        mOwnerAddressModel= arguments?.getParcelable<OwnerAddressModel>(INTENT_ADDRESS_DATA)!!
        etLocation.setText(mOwnerAddressModel.formatted)
        etAddressName.setText(mOwnerAddressModel.name)
        etBuilding.setText(mOwnerAddressModel.line1)
        etStreetAddress.setText(mOwnerAddressModel.line2)
        etPostalZipCode.setText(mOwnerAddressModel.zipCode)
        etCity.setText(mOwnerAddressModel.city)
        etState.setText(mOwnerAddressModel.state)

        if (mOwnerAddressModel.location.coordinates!!.isNotEmpty()){
            mLatitude=mOwnerAddressModel.location.coordinates!![0]
            mLongitude=mOwnerAddressModel.location.coordinates!![1]
        }

        //Initialize map
        fgGoogleMap?.onCreate(savedInstanceState)
        fgGoogleMap?.getMapAsync(this)
        fgGoogleMap.onStart()

        //Initialize place picker
        Places.initialize(requireContext(), GOOGLE_PLACES_API_KEY)
        placesClient = Places.createClient(requireContext())

        // Allow permissions
        checkForLocationPermission()

        //Set adapter
        rvLocationList.adapter = mSearchLocationAdapter

        //Set on click listener
        btnNewJobs.setOnClickListener(this)
        clRootView.setOnClickListener(this)

        //Search listener
        etLocation.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    rvLocationList.visibility = View.GONE
                } else {
                    val mSearch = s.toString()
                    if (!isAddressSelected){
                        pbBar.visibility=View.VISIBLE
                        mLocationListenerViewModel.getLocationPlaces(mSearch)
                    } }
            }
        })
    }

    override val viewModel: BaseViewModel?
        get() = mLocationListenerViewModel

    override fun observeProperties() {
        mLocationListenerViewModel.onGetLocation().observe(this, androidx.lifecycle.Observer {
            if (it.isEmpty()){
                rvLocationList.visibility=View.GONE
            }else{
                rvLocationList.visibility=View.VISIBLE
            }
            pbBar.visibility=View.GONE

            mSearchLocationAdapter.updateData(it)
        })
    }

    override fun onMapReady(mGoogleMap: GoogleMap?) {
        mMap = mGoogleMap
    }

    override fun onAllLocationPermissionsGranted(isLocationPermissionGranted: Boolean) {
    }

    override fun onLocationUpdated(location: Location) {
        // Get Location
        mLatitude = location.latitude
        mLongitude = location.longitude

        // Animate to location
        if (mMap != null) {
            mapInit(mMap!!)
        }
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
        }
    }

    private fun initAddressDetail(mLatitude: Double, mLongitude: Double) {
        try {
            val gcd = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> = gcd.getFromLocation(mLatitude, mLongitude, 1)
            if (addresses.isNotEmpty()) {
                etStreetAddress.setText(
                    GeneralFunctions.getAddress(
                        mLatitude,
                        mLongitude,
                        requireContext()
                    )
                )
                etState.setText(addresses[0].adminArea)
                if (addresses[0].locality!=null) {
                    etCity.setText(addresses[0].locality)
                }else{
                    etCity.setText(addresses[0].adminArea)
                }
                mOwnerAddressModel.country = addresses[0].countryName
            } else {
                // do your stuff
            }
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onLocationClick(placeId: String, address: String) {
        //Get latitude and longitude from place id
        showProgressLoader()
        val placeFields = listOf(Place.Field.ID, Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        rvLocationList.visibility = View.GONE
        isAddressSelected=true
        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude
                etLocation.setText(address)
                initAddressDetail(mLatitude, mLongitude)
                etAddressName.requestFocus()
                android.os.Handler().postDelayed({
                    isAddressSelected=false
                    hideProgressLoader()
                },300)

                // Animate to location
                if (mMap != null) {
                    mapInit(mMap!!)
                }
                hideSoftKeyboard()
            }.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    Log.e(ContentValues.TAG, "Place not found: ${exception.message}")
                    val statusCode = exception.statusCode
                }
            }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnNewJobs -> {
                val coordinates= mutableListOf<Double>()
                coordinates.add(mLatitude)
                coordinates.add(mLongitude)
                mOwnerAddressModel.name = etAddressName.text.toString().trim()
                mOwnerAddressModel.line1 = etBuilding.text.toString().trim()
                mOwnerAddressModel.line2 = etStreetAddress.text.toString().trim()
                mOwnerAddressModel.zipCode = etPostalZipCode.text.toString().trim()
                mOwnerAddressModel.city = etCity.text.toString().trim()
                mOwnerAddressModel.state = etState.text.toString().trim()
                mOwnerAddressModel.formatted = etLocation.text.toString().trim()
                mOwnerAddressModel.location.coordinates=coordinates

                //Send broadcast to update photo detail
                LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(
                        Intent(INTENT_GENERAL_INFO).putExtra(
                            INTENT_ADDRESS_DATA,
                            mOwnerAddressModel
                        )
                    )
                (requireContext() as BaseAppCompactActivity).onBackPressed()
            }
            R.id.clRootView -> {
                rvLocationList.visibility=View.GONE
            }
        }
    }
}