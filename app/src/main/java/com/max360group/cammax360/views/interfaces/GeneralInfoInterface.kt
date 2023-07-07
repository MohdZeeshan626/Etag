package com.max360group.cammax360.views.interfaces

import com.max360group.cammax360.repository.models.OwnerAddressModel

interface GeneralInfoInterface {
    fun onDeleteEmail(position:Int)
    fun onDeletePhone(position:Int)
    fun onDeleteAddress(position:Int)
    fun onEditAddress(position:Int,address:OwnerAddressModel)
}