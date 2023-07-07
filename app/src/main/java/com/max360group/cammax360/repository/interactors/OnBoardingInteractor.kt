package com.max360group.cammax360.repository.interactors

import android.util.Log
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RestClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class OnBoardingInteractor {

    fun register(
        firstName:String,
        lastname:String,
        email:String,
        password:String,
        deviceId:String,
        fcmId:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().register(firstName,lastname,email,password,fcmId,deviceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onNext(response: Response<*>) {
                    Log.e("Intrector", "data" + "yes")

                    networkRequestCallbacks.onSuccess(response)
                }

                override fun onError(t: Throwable) {
                    networkRequestCallbacks.onError(t)

                    Log.e("EmployeModel", "data" + "No")

                }

                override fun onComplete() {

                }
            })
    }

    fun login(
        email:String,
        password:String,
        deviceId:String,
        fcmId:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().login(email,password,fcmId,deviceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onNext(response: Response<*>) {
                    Log.e("Intrector", "data" + "yes")

                    networkRequestCallbacks.onSuccess(response)
                }

                override fun onError(t: Throwable) {
                    networkRequestCallbacks.onError(t)

                    Log.e("EmployeModel", "data" + "No")

                }

                override fun onComplete() {

                }
            })
    }

    fun forgotPassword(
        email:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().forgotPassword(email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onNext(response: Response<*>) {
                    Log.e("Intrector", "data" + "yes")

                    networkRequestCallbacks.onSuccess(response)
                }

                override fun onError(t: Throwable) {
                    networkRequestCallbacks.onError(t)

                    Log.e("EmployeModel", "data" + "No")

                }

                override fun onComplete() {

                }
            })
    }




}

