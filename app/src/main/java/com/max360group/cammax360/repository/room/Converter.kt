package com.max360group.cammax360.repository.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.max360group.cammax360.repository.models.*
import java.util.concurrent.CopyOnWriteArrayList

class Converter {
    //Account converter
    @TypeConverter
    fun fromAccountString(value: String): List<Account> {
        val type = object : TypeToken<List<Account>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromAccountList(list: List<Account>): String {
        val type = object : TypeToken<List<Account>>() {}.type
        return Gson().toJson(list, type)
    }

    //Theme converter
    @TypeConverter
    fun fromThemeString(value: String): Theme {
        var type = object : TypeToken<Theme>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromThemeObject(value: Theme): String {
        var type = object : TypeToken<Theme>() {}.type
        return Gson().toJson(value, type)

    }

    //Jobs converter
    @TypeConverter
    fun fromJobsString(value: String): List<Job> {
        val type = object : TypeToken<List<Job>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromJobsList(value: List<Job>): String {
        val type = object : TypeToken<List<Job>>() {}.type
        return Gson().toJson(value, type)
    }


    @TypeConverter
    fun fromAddressString(value: String): Address {
        val type = object : TypeToken<Address>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromAddressObject(value: Address): String {
        val type = object : TypeToken<Address>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun fromMediaString(value: String): List<Media> {
        val type = object : TypeToken<List<Media>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromMediaList(value: List<Media>): String {
        val type = object : TypeToken<List<Media>>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun fromUserString(value: String): ArrayList<UserX> {
        val type = object : TypeToken<ArrayList<UserX>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromUserList(value: ArrayList<UserX>): String {
        val type = object : TypeToken<ArrayList<UserX>>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun fromDeletedMediaString(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromDeletedMediaList(value: List<String>): String {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun fromDetailString(value: String): Details {
        val type = object : TypeToken<Details>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromDetailObject(value: Details): String {
        val type = object : TypeToken<Details>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun fromJobsUserString(value: String): List<UserX> {
        val type = object : TypeToken<List<UserX>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromJobsUserList(value: List<UserX>): String {
        val type = object : TypeToken<List<UserX>>() {}.type
        return Gson().toJson(value, type)
    }

    //Account converter
    @TypeConverter
    fun fromAccountDetailString(value: String): ArrayList<AccountDetail> {
        val type = object : TypeToken<ArrayList<AccountDetail>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromAccountDetailList(value: ArrayList<AccountDetail>): String {
        val type = object : TypeToken<ArrayList<AccountDetail>>() {}.type
        return Gson().toJson(value, type)
    }

    //Media converter
    @TypeConverter
    fun fromCreatorIdString(value: String): CreatorId {
        val type = object : TypeToken<CreatorId>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromCreatorIdObject(value: CreatorId): String {
        val type = object : TypeToken<CreatorId>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun fromJobMediaString(value: String): ArrayList<JobMedia> {
        val type = object : TypeToken<ArrayList<JobMedia>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromJobMediaList(value: ArrayList<JobMedia>): String {
        val type = object : TypeToken<ArrayList<JobMedia>>() {}.type
        return Gson().toJson(value, type)
    }

    //Roles converter
    @TypeConverter
    fun fromPermissionsString(value: String): Jobs {
        val type = object : TypeToken<Jobs>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromPermissionsObject(value: Jobs): String {
        val type = object : TypeToken<Jobs>() {}.type
        return Gson().toJson(value, type)
    }

    //Owner converter
    @TypeConverter
    fun fromOwnerAddressString(value: String): List<OwnerAddressModel> {
        val type = object : TypeToken<List<OwnerAddressModel>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromOwnerAddressList(value: List<OwnerAddressModel>): String {
        val type = object : TypeToken<List<OwnerAddressModel>>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun fromPhoneNumberString(value: String): List<PhoneNumberModel> {
        val type = object : TypeToken<List<PhoneNumberModel>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromPhoneNumberList(value: List<PhoneNumberModel>): String {
        val type = object : TypeToken<List<PhoneNumberModel>>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun fromOwnerPropertyString(value: String): List<OwnerProperty> {
        val type = object : TypeToken<List<OwnerProperty>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun  fromOwnerPropertyList(value: List<OwnerProperty>): String {
        val type = object : TypeToken<List<OwnerProperty>>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun fromEmailString(value: String): List<EmailsModel> {
        val type = object : TypeToken<List<EmailsModel>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun  fromOwnerEmailList(value: List<EmailsModel>): String {
        val type = object : TypeToken<List<EmailsModel>>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun fromAccessString(value: String): Access {
        val type = object : TypeToken<Access>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromAccessObject(value: Access): String {
        val type = object : TypeToken<Access>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun fromBillingAddressString(value: String): OwnerAddressModel {
        val type = object : TypeToken<OwnerAddressModel>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromBillingAddressObject(value: OwnerAddressModel): String {
        val type = object : TypeToken<OwnerAddressModel>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun fromInviteString(value: String): Invite {
        val type = object : TypeToken<Invite>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromInviteObject(value: Invite): String {
        val type = object : TypeToken<Invite>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun fromRmString(value: String): Rm {
        val type = object : TypeToken<Rm>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromRmObject(value: Rm): String {
        val type = object : TypeToken<Rm>() {}.type
        return Gson().toJson(value, type)
    }

    //Property converter
    @TypeConverter
    fun fromUnitRecordString(value: String): ArrayList<UnitRecord> {
        val type = object : TypeToken<ArrayList<UnitRecord>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun  fromUnitRecordList(value: ArrayList<UnitRecord>): String {
        val type = object : TypeToken<ArrayList<UnitRecord>>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun fromUserOwnerString(value: String): List<UserOwner> {
        val type = object : TypeToken<List<UserOwner>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun  fromUserOwnerList(value: List<UserOwner>): String {
        val type = object : TypeToken<List<UserOwner>>() {}.type
        return Gson().toJson(value, type)
    }
}