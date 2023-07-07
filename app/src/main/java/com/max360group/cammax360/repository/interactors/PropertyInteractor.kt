package com.max360group.cammax360.repository.interactors

import com.max360group.cammax360.repository.models.CreateOwnerRequestModel
import com.max360group.cammax360.repository.models.CreatePropertyRequestModel
import com.max360group.cammax360.repository.models.OwnerCreateNoteRequestModel
import com.max360group.cammax360.repository.models.UpdateAccountRequestModel
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
class PropertyInteractor {

    fun getIntegrationType(
        kind:String,
        skip:String,
        limit:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getIntegrationType(kind,skip,limit)
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

    fun getIntegrationAll(
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getIntegrationAll()
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

    fun createProperty(
        mCreatePropertyRequestModel: CreatePropertyRequestModel,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().createProperty(mCreatePropertyRequestModel)
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

    fun updateProperty(
        id:String,
        mCreatePropertyRequestModel: CreatePropertyRequestModel,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().updateProperty(id,mCreatePropertyRequestModel)
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

    fun getAllOwners(
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getOwnersAll()
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

    fun getProperties(
        skip:Int,
        limit:Int,
        search:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getProperties(skip,limit,search)
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

    fun blockUnblockProperty(
        id:String,
        block:Boolean,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().blockUnblockProperty(id,block)
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

    fun deleteProperty(
        id:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().deleteProperty(id)
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

    fun getPropertyDetail(
        id:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getPropertiesDetail(id)
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

    fun deletePropertyUnit(
        id:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().deletePropertyUnit(id)
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