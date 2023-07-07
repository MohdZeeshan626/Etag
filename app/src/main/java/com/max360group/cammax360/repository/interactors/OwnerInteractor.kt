package com.max360group.cammax360.repository.interactors

import com.max360group.cammax360.repository.models.CreateOwnerRequestModel
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
class OwnerInteractor {

    fun createNote(
        mOwnerCreateNoteRequestModel: OwnerCreateNoteRequestModel,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().createNote(mOwnerCreateNoteRequestModel)
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

    fun propertiesList(
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().propertiesList()
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

    fun createOwner(
        mCreateOwnerRequestModel: CreateOwnerRequestModel,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().createOwner(mCreateOwnerRequestModel)
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

    fun getOwners(
        skip:Int,
        limit:Int,
        search:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getOwners(skip,limit,search)
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

    fun deleteOwner(
        id:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().deleteOwner(id)
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

    fun blockOwner(
        id:String,
        status:Boolean,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().blockOwner(id,status)
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

    fun sendInvite(
        id:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().sendInvite(id)
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

    fun getOwnerDetails(
        id:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getUserOwners(id)
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

    fun getNotesList(
        hashMap: HashMap<String,String>,
        search:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getNotesList(hashMap,search)
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

    fun editOwner(
        id:String,
        mCreateOwnerRequestModel: CreateOwnerRequestModel,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().editOwner(id,mCreateOwnerRequestModel)
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