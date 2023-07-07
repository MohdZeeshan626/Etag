package com.max360group.cammax360.repository.interactors

import android.util.Log
import com.max360group.cammax360.repository.models.CreateJobRequestModel
import com.max360group.cammax360.repository.models.CreateRoleRequestModel
import com.max360group.cammax360.repository.networkrequests.NetworkRequestCallbacks
import com.max360group.cammax360.repository.networkrequests.RestClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class JobsInteractor {

    fun getJobs(
        skip:Int,
        limit:Int,
        sort:String,
        search:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getJobs(skip,limit,sort,search)
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

    fun getModuleUsers(
        moduleKind:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getUserModuleWise(moduleKind)
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

    fun createJob(
        mCreateJobRequestModel: CreateJobRequestModel,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().createJob(mCreateJobRequestModel)
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

    fun saveAsRole(
        mCreateRoleRequestModel: CreateRoleRequestModel,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().saveAsRole(mCreateRoleRequestModel)
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

    fun getJobRole(
        roleKind:String,
        networkRequestCallbacks: NetworkRequestCallbacks
    ): Disposable {
        return RestClient.get().getJobRoles(roleKind)
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

