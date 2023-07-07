package com.max360group.cammax360.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.interactors.LocationInteractor
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.repository.networkrequests.API
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RetrofitRequest
import com.max360group.cammax360.views.fragments.SelectLocationFragment
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

class LocationListenerViewModel(application: Application) : BaseViewModel(application) {

    private var mLocationPropertyList = MutableLiveData<List<PropertyLocationData>>()
    private var mGetLocation = MutableLiveData<List<Prediction>>()
    private var mSearchList = ArrayList<PropertyLocationData>()
    private val mLocationInteractor by lazy { LocationInteractor() }

    fun getLocationPlaces(
        input: String,
        searchProperty: Boolean = false
    ) {
        val request = ServerBuilder.buildService(API::class.java)
        val call: Call<AutoCompleteListener> = request.getPlaces(
            SelectLocationFragment.GOOGLE_PLACES_API_KEY, input
        )
        call.enqueue(object : retrofit2.Callback<AutoCompleteListener> {
            override fun onResponse(
                call: Call<AutoCompleteListener>,
                response: Response<AutoCompleteListener>
            ) {
                if (response.isSuccessful) {
                    isShowLoader.value = false
                    Log.e("data", response.toString())


                    if (searchProperty) {
                        try {
                            mSearchList.clear()
                            for (i in response.body()!!.predictions.indices) {
                                val mPropertyLocationData = PropertyLocationData()
                                mPropertyLocationData.isLocation = true
                                mPropertyLocationData.locationDesc =
                                    response.body()!!.predictions[i].description
                                mPropertyLocationData.secondary_text =
                                    response.body()!!.predictions[i].structured_formatting.secondary_text
                                mPropertyLocationData.placeId =
                                    response.body()!!.predictions[i].place_id
                                mSearchList.add(mPropertyLocationData)
                            }

                            //Call api get properties
                            getProperties(input, mSearchList)
                        } catch (E: Exception) {
                        }

                    } else {
                        mGetLocation.value = response.body()!!.predictions
                    }
                }
            }

            override fun onFailure(call: Call<AutoCompleteListener>, t: Throwable) {
                isShowLoader.value = false
                Log.d("error", "in Failure Portion")
                Log.d("error", t.message.toString())
            }
        })
    }

    fun getProperties(search: String, mSearchList: ArrayList<PropertyLocationData>) {
        mCompositeDisposable.add(
            mLocationInteractor.getProperties(search,
                object :
                    NetworkRequestCallbacks {
                    override fun onSuccess(response: Response<*>) {
                        try {
                            isShowSwipeRefreshLayout.value = false
                            val pojoNetworkResponse =
                                RetrofitRequest.checkForResponseCode(response.code())
                            when {
                                pojoNetworkResponse.isSuccess && null != response.body() -> {
                                    val mResponse =
                                        response.body() as PropertiesListResponseModel

                                    for (i in mResponse.data.records!!.indices) {
                                        val mPropertyLocationData = PropertyLocationData()
                                        mPropertyLocationData.propertyImage =
                                            mResponse.data.records!![i].picURL
                                        mPropertyLocationData.propertyName =
                                            mResponse.data.records!![i].name
                                        mPropertyLocationData.propertyAddress =
                                            mResponse.data.records!![i].primaryAddress
                                        mPropertyLocationData.propertyUnits =
                                            mResponse.data.records!![i].propertyUnits
                                        mPropertyLocationData.propertyId =
                                            mResponse.data.records!![i].id
                                        mSearchList.add(mPropertyLocationData)
                                    }
                                    mLocationPropertyList.value = mSearchList
                                }

                                pojoNetworkResponse.isSessionExpired -> {
                                    isSessionExpired.value = true
                                }


                                else -> {
                                    retrofitErrorMessage
                                        .postValue(
                                            RetrofitErrorMessage(
                                                errorMessage =
                                                RetrofitRequest.getErrorMessage(
                                                    response.errorBody()!!
                                                )
                                            )
                                        )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            retrofitErrorMessage
                                .postValue(
                                    RetrofitErrorMessage(
                                        errorResId =
                                        R.string.retrofit_failure
                                    )
                                )
                        }
                    }

                    override fun onError(t: Throwable) {
                        t.printStackTrace()
                        isShowSwipeRefreshLayout.value = false
                        retrofitErrorMessage
                            .postValue(
                                RetrofitErrorMessage(
                                    errorResId =
                                    RetrofitRequest.getRetrofitError(t)
                                )
                            )
                    }

                })
        )
    }

    object ServerBuilder {
        private val client = OkHttpClient.Builder().build()
        private val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        fun <T> buildService(service: Class<T>): T {
            return retrofit.create(service)
        }
    }

    fun onGetPropertyLocation() = mLocationPropertyList
    fun onGetLocation() = mGetLocation
}