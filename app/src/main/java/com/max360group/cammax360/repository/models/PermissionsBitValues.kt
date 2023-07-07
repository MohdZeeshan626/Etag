package com.max360group.cammax360.repository.models

 class PermissionsBitValues(
    var none: Int = 1,
    var all: Int = 2,
    var view: Int = 4,
    var add: Int = 8,
    var edit: Int = 16,
    var delete: Int = 32,
    var timeLine: Int = 64,
    var sumOfAllValues:Int=125
 )

 class AccountBitValues(
    var basic: Int = 1,
    var superAdmin: Int = 2,
    var admin: Int = 4,
    var owner: Int = 8,
    var user: Int = 16,
    var cContact: Int = 32,
    var cOwner: Int = 64,
    var cResident: Int = 128,
    var cVendor: Int = 256
)