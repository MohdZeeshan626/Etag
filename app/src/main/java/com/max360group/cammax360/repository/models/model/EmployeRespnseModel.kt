package com.example.mvvmnewdemo.WithApiExample.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import retrofit2.Response

class EmployeRespnseModel()  {


    @SerializedName("data")
    private var mData: List<EmployeData>? = null

    @SerializedName("status")
    var status: String? = null

    constructor(parcel: Parcel) : this() {
        status = parcel.readString()
    }


    fun getmData(): List<EmployeData>? {
        return mData
    }

    fun setmData(data: List<EmployeData>) {
        mData = data
    }



    companion object CREATOR : Parcelable.Creator<EmployeRespnseModel> {
        override fun createFromParcel(parcel: Parcel): EmployeRespnseModel {
            return EmployeRespnseModel(parcel)
        }

        override fun newArray(size: Int): Array<EmployeRespnseModel?> {
            return arrayOfNulls(size)
        }
    }

}
