package com.example.daunsehat.data.remote.retrofit

import com.example.daunsehat.data.remote.response.AddArticleResponse
import com.example.daunsehat.data.remote.response.DetailArticleResponse
import com.example.daunsehat.data.remote.response.GuidanceResponse
import com.example.daunsehat.data.remote.response.HistoryPredictResponseItem
import com.example.daunsehat.data.remote.response.ListArticleItem
import com.example.daunsehat.data.remote.response.LoginResponse
import com.example.daunsehat.data.remote.response.PredictResponse
import com.example.daunsehat.data.remote.response.ProfileResponse
import com.example.daunsehat.data.remote.response.RegisterResponse
import com.example.daunsehat.data.remote.response.UserArticleResponse
import com.google.gson.annotations.SerializedName
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

data class RegisterRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("hashed_password")
    val password: String,

    @SerializedName("name")
    val name: String
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

    @POST("user/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): RegisterResponse

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

    @Multipart
    @POST("/predict")
    suspend fun predictPlant(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): PredictResponse

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

    @GET("/predict")
    suspend fun getHistoryPredict(
        @Header("Authorization") token: String
    ): List<HistoryPredictResponseItem>

    @Multipart
    @POST("garticle/")
    suspend fun postGuidanceArticle(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part?,
        @Part("title_guidance") title: RequestBody,
        @Part("body_guidance") body: RequestBody,
        @Part("category_guidance") category: RequestBody
    ): Response<Void>

    @GET("garticle/")
    suspend fun getAllGuidanceArticles(
        @Header("Authorization") token: String
    ): List<GuidanceResponse>

    @GET("garticle/{category}")
    suspend fun getGuidanceArticlesByCategory(
        @Header("Authorization") token: String,
        @Path("category") category: String
    ): List<GuidanceResponse>

    @DELETE("garticle/{id}")
    suspend fun deleteGuidanceArticleById(
        @Header("Authorization") token: String,
        @Path("id") articleId: Int
    ): Response<Void>
}