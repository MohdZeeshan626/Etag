package com.max360group.cammax360.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.max360group.cammax360.R
import com.max360group.cammax360.repository.models.DashBoardMenuModel

class DashboardViewModel(application: Application) : BaseViewModel(application) {
    private var mMenuList=ArrayList<DashBoardMenuModel>()
   private var isMenuList=MutableLiveData<List<DashBoardMenuModel>>()
    private var context=application

    fun getMenu() {
        mMenuList.add(DashBoardMenuModel(name = context.getString(R.string.st_dashboard),icon = R.drawable.ic_dashboard))
        mMenuList.add(DashBoardMenuModel(name = context.getString(R.string.st_account),icon = R.drawable.ic_google))
        mMenuList.add(DashBoardMenuModel(name = context.getString(R.string.st_users),icon = R.drawable.ic_user))
        mMenuList.add(DashBoardMenuModel(name = context.getString(R.string.st_owners),icon = R.drawable.ic_resident))
        mMenuList.add(DashBoardMenuModel(name = context.getString(R.string.st_residents),icon = R.drawable.ic_owner))
        mMenuList.add(DashBoardMenuModel(name = context.getString(R.string.st_properties),icon = R.drawable.ic_insurance))
        mMenuList.add(DashBoardMenuModel(name = context.getString(R.string.st_jobs),icon = R.drawable.ic_bag))
        isMenuList.value=mMenuList
    }

    fun onGetMenu() = isMenuList

}