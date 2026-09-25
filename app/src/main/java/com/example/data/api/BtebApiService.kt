package com.example.data.api

import com.example.data.model.ContactRequest
import com.example.data.model.ContactResponse
import com.example.data.model.CurriculumsResponse
import com.example.data.model.GroupResultResponse
import com.example.data.model.GroupSearchRequest
import com.example.data.model.InstituteListResponse
import com.example.data.model.InstituteResultsResponse
import com.example.data.model.PublicStatsResponse
import com.example.data.model.RoutineDepartmentResponse
import com.example.data.model.RoutineResponse
import com.example.data.model.StudentResultResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BtebApiService {

    @GET("api/results/{roll}")
    suspend fun getIndividualResult(
        @Path("roll") roll: String,
        @Query("exam") exam: String? = null
    ): StudentResultResponse

    @POST("api/results/group")
    suspend fun getGroupResult(
        @Body request: GroupSearchRequest
    ): GroupResultResponse

    @GET("api/institutes")
    suspend fun getInstitutes(
        @Query("search") search: String? = null
    ): InstituteListResponse

    @GET("api/institutes/{code}/results")
    suspend fun getInstituteResults(
        @Path("code") code: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): InstituteResultsResponse

    @GET("api/routine/departments")
    suspend fun getRoutineDepartments(): RoutineDepartmentResponse

    @GET("api/routine/search")
    suspend fun searchRoutine(
        @Query("department") department: String,
        @Query("semester") semester: String
    ): RoutineResponse

    @GET("api/routine")
    suspend fun getRoutineBySubjects(
        @Query("subjects") subjects: String
    ): RoutineResponse

    @GET("api/curriculums")
    suspend fun getCurriculums(): CurriculumsResponse

    @GET("api/stats/public")
    suspend fun getPublicStats(): PublicStatsResponse

    @POST("api/contact")
    suspend fun submitContactForm(
        @Body request: ContactRequest
    ): ContactResponse
}
