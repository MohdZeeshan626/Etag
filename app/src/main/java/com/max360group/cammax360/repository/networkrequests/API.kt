package com.max360group.cammax360.repository.networkrequests


import androidx.room.Delete
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.repository.models.model.CreateUnitWithoutPropertyRequestModel
import com.max360group.cammax360.repository.models.model.EditJobMemberPermissions
import com.max360group.cammax360.repository.models.model.JobMediaRequestModel
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface API {

    @FormUrlEncoded
    @POST("v1/user/register")
    fun register(
        @Field("firstName") firstName: String,
        @Field("lastName") lastName: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("fcmId") fcmId :String,
        @Field("deviceId") deviceId: String,
        @Field("deviceType") deviceType: String = "ANDROID",
        @Field("productKind") productKind: String = "2"
    ): Observable<Response<PojoUserLogin>>

    @FormUrlEncoded
    @POST("v1/user/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("fcmId") fcmId :String,
        @Field("deviceId") deviceId: String,
        @Field("deviceType") deviceType: String = "ANDROID",
        @Field("productKind") productKind: String = "2"
    ): Observable<Response<PojoUserLogin>>

    @FormUrlEncoded
    @POST("v1/user/passwordForgot")
    fun forgotPassword(
        @Field("email") email: String
    ): Observable<Response<SimpleSuccessResponse>>


    @GET("v1/jobs")
    fun getJobs(
        @Query("skip") skip: Int,
        @Query("limit") limit: Int,
        @Query("sort") sort: String,
        @Query("search") search: String
    ): Observable<Response<JobsListingResponseModel>>

    @GET("v1/user/listAll")
    fun getUserModuleWise(
        @Query("moduleKind") moduleKind: String
    ): Observable<Response<GetUserModuleWiseResponseMode>>

    @POST("v1/jobs")
    fun createJob(
        @Body mCreateJobRequestModel: CreateJobRequestModel
    ): Observable<Response<CreateJobResponseModel>>

    @POST("v1/roles")
    fun saveAsRole(
        @Body mCreateRoleRequestModel: CreateRoleRequestModel
    ): Observable<Response<SimpleSuccessResponse>>

    @GET("v1/roles")
    fun getJobRoles(
        @Query("kindFilters[]") kindValue: String
    ): Observable<Response<RolesListResponseModel>>

    @GET("v1/jobs/{id}/members")
    fun getJobMembers(
        @Path("id") id: String,
        @Query("subModuleKind") kindValue: String

    ): Observable<Response<GetJobMembersSubModuleResponse>>

    @POST("v1/jobs/{jobId}/medias")
    fun addMedia(
        @Path("jobId") id: String,
        @Body mMediaRequestModel: JobMediaRequestModel
    ): Observable<Response<SimpleSuccessResponse>>

    @POST("v1/jobs/{jobId}/medias")
    fun addVideoJob(
        @Path("jobId") id: String,
        @Body mJobVideoRequestModel: JobVideoRequestModel
    ): Observable<Response<SimpleSuccessResponse>>

    @POST("v1/jobs/{jobId}/medias")
    fun addDocsJob(
        @Path("jobId") id: String,
        @Body mJobDocsRequestModel: JobDocsRequestModel
    ): Observable<Response<SimpleSuccessResponse>>

    @GET("v1/jobs/{jobId}")
    fun jobDetail(
        @Path("jobId") id: String,
        @Query("mediaPopulate") mediaPopulate: Boolean,
        @Query("userPopulate") userPopulate: Boolean
    ): Observable<Response<JobDetailResponseModel>>

    @POST("v1/jobs/{jobId}/timelines")
    fun jobTimeline(
        @Path("jobId") id: String,
        @Body mTimeLineRequestModel: TimeLineRequestModel
    ): Observable<Response<TimelineResponseModel>>

    @DELETE("v1/jobs/{jobId}")
    fun deleteJob(
        @Path("jobId") id: String
    ): Observable<Response<SimpleSuccessResponse>>

    @GET("v1/jobs/{jobId}/medias")
    fun getJobMedia(
        @Path("jobId") id: String,
        @Query("kind") kind: String
    ): Observable<Response<JobMediaResponseModel>>

    @GET("v1/conversations")
    fun getConversation(
        @Query("kind") kind: String,
        @Query("jobId") jobId: String,
        @Query("search") search: String,
        @Query("creatorPopulate") creatorPopulate: Boolean = true
    ): Observable<Response<ConversationResponseModel>>

    @GET("v1/jobs/{jobId}/comments")
    fun getAllComments(
        @Path("jobId") jobId: String,
        @Query("search") search: String,
    ): Observable<Response<ConversationResponseModel>>

    @GET("v1/conversations")
    fun getMediaComment(
        @Query("kind") kind: String,
        @Query("jobId") jobId: String,
        @Query("mediaId") mediaId: String,
        @Query("subMediaId") subMediaId: String,
        @Query("search") search: String,
        @Query("creatorPopulate") creatorPopulate: Boolean = true
    ): Observable<Response<MediaCommentResponseModel>>

    @FormUrlEncoded
    @POST("v1/conversations")
    fun createConversation(
        @Field("kind") kind: String,
        @Field("jobId") jobId: String,
        @Field("message") message: String
    ): Observable<Response<SimpleSuccessResponse>>

    @FormUrlEncoded
    @POST("v1/conversations")
    fun createComment(
        @Field("kind") kind: String,
        @Field("jobId") jobId: String,
        @Field("message") message: String,
        @Field("mediaId") mediaId: String,
        @Field("subMediaId") subMediaId: String,
    ): Observable<Response<SimpleSuccessResponse>>

    @PUT("v1/medias/{kind}/{mediaId}/info")
    fun updateMediaInfo(
        @Path("mediaId") mediaId: String,
        @Path("kind") kind: String,
        @Body mUpdateMediaRequetsModel: UpdateMediaRequetsModel
    ): Observable<Response<SimpleSuccessResponse>>

    @PUT("v1/medias/{kind}/{mediaId}/perms")
    fun updateMediaPermissions(
        @Path("mediaId") mediaId: String,
        @Path("kind") kind: String,
        @Body mUpdateMediaPermissionsRequests: UpdateMediaPermissionsRequests
    ): Observable<Response<SimpleSuccessResponse>>

    @GET("v1/jobs/{jobId}/medias/{mediaId}")
    fun photoDetail(
        @Path("jobId") jobId: String,
        @Path("mediaId") mediaId: String,
        @Query("kind") kind: String,
        @Query("subMediaId") subMediaId: String,
    ): Observable<Response<MediaDetailResponseModel>>

    @DELETE("v1/jobs/{jobId}/medias")
    fun deleteMedia(
        @Path("jobId") jobId: String,
        @Query("kind") kind: String,
        @Query("mediaIds[]") subMediaId: List<String>,
    ): Observable<Response<SimpleSuccessResponse>>

    @GET("v1/user/authProfileGet")
    fun getProfile(
    ): Observable<Response<PojoUserLogin>>

    @FormUrlEncoded
    @PUT("v1/user/profileUpdate")
    fun updateProfile(
        @Field("firstName") firstName: String,
        @Field("lastName") lastName: String,
        @Field("pic") profilePic: String,
    ): Observable<Response<SimpleSuccessResponse>>

    @FormUrlEncoded
    @POST("v1/user/passwordUpdate")
    fun updatePassword(
        @Field("oldPassword") oldPassword: String,
        @Field("newPassword") newPassword: String,
        @Field("deviceType") deviceType: String = "ANDROID",
    ): Observable<Response<SimpleSuccessResponse>>

    @PUT("v1/accounts/{accountId}")
    fun updateAccount(
        @Path("accountId") accountId: String,
        @Body mUpdateAccountRequestModel: UpdateAccountRequestModel
    ): Observable<Response<SimpleSuccessResponse>>

    @POST("v1/jobs/{jobId}")
    fun updateJobDescription(
        @Path("jobId") accountId: String,
        @Body mUpdateJobDescriptionRequestModel: UpdateJobDescriptionRequestModel
    ): Observable<Response<SimpleSuccessResponse>>

    @PUT("v1/jobs/{jobId}/memberEdit")
    fun editMemberPermissions(
        @Path("jobId") accountId: String,
        @Body mEditJobMemberPermissions: EditJobMemberPermissions
    ): Observable<Response<SimpleSuccessResponse>>

    @FormUrlEncoded
    @PUT("v1/jobs/{jobId}/memberDelete")
    fun deleteJobMember(
        @Path("jobId") accountId: String,
        @Field("userId") userId: String
    ): Observable<Response<SimpleSuccessResponse>>


    @PUT("v1/jobs/{jobId}/membersAdd")
    fun addJobMember(
        @Path("jobId") accountId: String,
        @Body mAddJobMembersRequestModel: AddJobMembersRequestModel
    ): Observable<Response<SimpleSuccessResponse>>

    @POST("v1/jobs/{jobId}/memberInvite")
    fun inviteMember(
        @Path("jobId") accountId: String,
        @Body mInviteJobMemberRequestModel: InviteJobMemberRequestModel
    ): Observable<Response<SimpleSuccessResponse>>

    @GET("v1/calendarEvents")
    fun getCalenderEvents(
    ): Observable<Response<CalenderEventsResponseModel>>

    @GET("v1/notifications")
    fun getNotifications(
        @Query("skip") skip: Int,
        @Query("limit") limit: Int,
    ): Observable<Response<NotificationResponseModel>>

    @FormUrlEncoded
    @POST("v1/common/invite/{token}")
    fun inviteAcceptReject(
        @Path("token") token: String,
        @Field("status") status: String,
    ): Observable<Response<SimpleSuccessResponse>>

    @POST("v1/notes")
    fun createNote(
        @Body mOwnerCreateNoteRequestModel: OwnerCreateNoteRequestModel
    ): Observable<Response<CreateNotesResponseModel>>

    @GET("v1/property/listAll")
    fun propertiesList(
    ): Observable<Response<PropertiesListResponseModel>>

    @POST("v1/userOwners")
    fun createOwner(
        @Body mCreateOwnerRequestModel: CreateOwnerRequestModel
    ): Observable<Response<CreateOwnerResponseModel>>

    @GET("v1/userOwners")
    fun getOwners(
        @Query("skip") skip: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String
    ): Observable<Response<OwnerListResponseModel>>

    @DELETE("v1/userOwners/{id}")
    fun deleteOwner(
        @Path("id") id: String
    ): Observable<Response<SimpleSuccessResponse>>

    @GET("autocomplete/json")
    fun getPlaces(
        @Query("key") key: String,
        @Query("input") input: String
    ): Call<AutoCompleteListener>

    @GET("v1/integrationCommons/{kind}")
    fun getIntegrationType(
        @Path("kind") kind: String,
        @Query("skip") skip: String,
        @Query("limit") limit: String
    ): Observable<Response<IntegrationCommans>>

    @GET("v1/integrationCommons/listAll")
    fun getIntegrationAll(
    ): Observable<Response<IntegrationAllResponseModel>>

    @FormUrlEncoded
    @PUT("v1/userOwners/{id}/block")
    fun blockOwner(
        @Path("id") kind: String,
        @Field("isActive") isActive: Boolean
    ): Observable<Response<SimpleSuccessResponse>>

    @PUT("v1/userOwners/{id}/invite")
    fun sendInvite(
        @Path("id") kind: String
    ): Observable<Response<SimpleSuccessResponse>>

    @GET("v1/userOwners/{id}")
    fun getUserOwners(
        @Path("id") kind: String
    ): Observable<Response<OwnerDetailResponseModel>>

    @GET("v1/notes")
    fun getNotesList(
        @QueryMap hashMap: HashMap<String,String>,
        @Query("search") search: String,
    ): Observable<Response<NotesHistoryResponseModel>>

    @PUT("v1/userOwners/{id}")
    fun editOwner(
        @Path("id") id: String,
        @Body  mCreateOwnerRequestModel: CreateOwnerRequestModel
    ): Observable<Response<CreateOwnerResponseModel>>

    @POST("v1/propertyUnits")
    fun createUnit(
        @Body  mCreateUnitRequestModel: CreateUnitWithRequestModel
    ): Observable<Response<CreateUnitResponseModel>>

    @POST("v1/propertyUnits")
    fun createUnitWithoutProperty(
        @Body  mCreateUnitRequestModel: CreateUnitWithoutPropertyRequestModel
    ): Observable<Response<CreateUnitResponseModel>>

    @POST("v1/property")
    fun createProperty(
        @Body  mCreatePropertyRequestModel: CreatePropertyRequestModel
    ): Observable<Response<CreatePropertyResponseModel>>

    @PUT("v1/property/{id}")
    fun updateProperty(
        @Path("id") id:String,
        @Body  mCreatePropertyRequestModel: CreatePropertyRequestModel
    ): Observable<Response<SimpleSuccessResponse>>

    @GET("v1/userOwners")
    fun getOwnersAll(
    ): Observable<Response<OwnerListResponseModel>>

    @GET("v1/property")
    fun getProperties(
        @Query("skip") skip: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String
    ): Observable<Response<PropertiesListResponseModel>>

    @GET("v1/property/listAll")
    fun getPropertiesAll(
        @Query("search") search: String
    ): Observable<Response<PropertiesListResponseModel>>

    @FormUrlEncoded
    @PUT("v1/property/{id}/block")
    fun blockUnblockProperty(
        @Path("id") skip: String,
        @Field("isActive") block:Boolean
    ): Observable<Response<SimpleSuccessResponse>>

    @DELETE("v1/property/{id}")
    fun deleteProperty(
        @Path("id") skip: String
    ): Observable<Response<SimpleSuccessResponse>>

    @GET("v1/property/{id}")
    fun getPropertiesDetail(
        @Path("id") skip: String,
        @Query("populatePropertyUnits") populatePropertyUnits: Boolean=true,
        @Query("populateUserOwners") populateUserOwners: Boolean=true
    ): Observable<Response<PropertyDetailResponseModel>>

    @DELETE("v1/propertyUnits/{id}")
    fun deletePropertyUnit(
        @Path("id") skip: String
    ): Observable<Response<SimpleSuccessResponse>>

    @GET("v1/propertyUnits/{id}")
    fun getUnitDetail(
        @Path("id") skip: String
    ): Observable<Response<CreateUnitResponseModel>>

    @PUT("v1/propertyUnits/{id}")
    fun editUnit(
        @Path("id") id:String,
        @Body  mCreateUnitRequestModel: CreateUnitWithRequestModel
    ): Observable<Response<CreateUnitResponseModel>>

}