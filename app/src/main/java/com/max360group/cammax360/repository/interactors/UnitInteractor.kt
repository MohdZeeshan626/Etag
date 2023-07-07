package com.max360group.cammax360.repository.interactors

import com.max360group.cammax360.repository.models.CreateUnitWithRequestModel
import com.max360group.cammax360.repository.models.model.CreateUnitWithoutPropertyRequestModel
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RestClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


/**
 * Created by Mukesh on 20/7/18.
 */
class UnitInteractor {

    fun createUnit(
        mCreateUnitRequestModel: CreateUnitWithRequestModel,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().createUnit(mCreateUnitRequestModel)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onNext(response: Response<*>) {
                    networkRequestCallbacks.onSuccess(response)
                }

                override fun onError(t: Throwable) {
                    networkRequestCallbacks.onError(t)
                }

                override fun onComplete() {

                }
            })
    }

    fun createUnitWithoutProperty(
        mCreateUnitRequestModel: CreateUnitWithoutPropertyRequestModel,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().createUnitWithoutProperty(mCreateUnitRequestModel)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onNext(response: Response<*>) {
                    networkRequestCallbacks.onSuccess(response)
                }

                override fun onError(t: Throwable) {
                    networkRequestCallbacks.onError(t)
                }

                override fun onComplete() {

                }
            })
    }

    fun getUnitDetail(
        id:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getUnitDetail(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onNext(response: Response<*>) {
                    networkRequestCallbacks.onSuccess(response)
                }

                override fun onError(t: Throwable) {
                    networkRequestCallbacks.onError(t)
                }

                override fun onComplete() {

                }
            })
    }

    fun editUnit(
        id:String,
        mCreateUnitRequestModel: CreateUnitWithRequestModel,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().editUnit(id,mCreateUnitRequestModel)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<*>>() {
                override fun onNext(response: Response<*>) {
                    networkRequestCallbacks.onSuccess(response)
                }

                override fun onError(t: Throwable) {
                    networkRequestCallbacks.onError(t)
                }

                override fun onComplete() {

                }
            })
    }


}