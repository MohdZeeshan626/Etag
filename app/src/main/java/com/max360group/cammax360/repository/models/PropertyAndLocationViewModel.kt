package com.max360group.cammax360.repository.models

class PropertyAndLocationViewModel(
  var record:List<PropertyLocationData>?= mutableListOf()
)

data class PropertyLocationData(
    var locationDesc:String="",
    var secondary_text:String="",
    var placeId:String="",
    var propertyImage:String="",
    var propertyName:String="",
    var propertyId:String="",
    var propertyAddress:OwnerAddressModel=OwnerAddressModel(),
    var propertyUnits:List<UnitRecord>?= mutableListOf(),
    var isLocation:Boolean=false
)