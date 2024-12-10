package com.example.daunsehat.data.remote.retrofit

import com.example.daunsehat.data.remote.response.AddArticleResponse
import com.example.daunsehat.data.remote.response.DetailArticleResponse
import com.example.daunsehat.data.remote.response.ListArticleItem
import com.example.daunsehat.data.remote.response.ListArticleResponse
import com.example.daunsehat.data.remote.response.LoginResponse
import com.example.daunsehat.data.remote.response.ProfileResponse
import com.example.daunsehat.data.remote.response.UserArticleResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

data class LoginRequest(
    val email: String,
    val password: String
)

data class EditProfileRequest(
    val name: String,
    val email: String
)

interface ApiService {

    @POST("user/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("user/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): ProfileResponse

    @PUT("user/profile")
    suspend fun editProfile(
        @Header("Authorization") token: String,
        @Body request: EditProfileRequest
    ): ProfileResponse

    @Multipart
    @POST("/user/upload_image")
    suspend fun editPhotoProfile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part?
    ): ProfileResponse

    @GET("uarticle")
    suspend fun getAllArticle(
        @Header("Authorization") token: String
    ): List<ListArticleItem>

    @GET("/uarticle/user")
    suspend fun getUserArticle(
        @Header("Authorization") token: String
    ): List<ListArticleItem>

    @GET("uarticle/{id}")
    suspend fun getDetailArticle(
        @Header("Authorization") token: String,
        @Path("id") articleId: String
    ): DetailArticleResponse

    @Multipart
    @POST("uarticle/")
    suspend fun addArticle(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part?,
        @Part("title_article") title: RequestBody,
        @Part("body_article") content: RequestBody
    ): AddArticleResponse

    @GET ("/uarticle/search/{search}")
    suspend fun searchArticle(
        @Header("Authorization") token: String,
        @Path("search") search: String
    ): List<ListArticleItem>

    @DELETE("uarticle/{id}")
    suspend fun deleteUserArticle(
        @Header("Authorization") token: String,
        @Path("id") articleId: String
    ): Response<UserArticleResponse>

}