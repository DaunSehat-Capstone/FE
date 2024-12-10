package com.example.daunsehat.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.daunsehat.data.pref.UserModel
import com.example.daunsehat.data.pref.UserPreference
import com.example.daunsehat.data.remote.response.AddArticleResponse
import com.example.daunsehat.data.remote.response.DetailArticleResponse
import com.example.daunsehat.data.remote.response.ErrorResponse
import com.example.daunsehat.data.remote.response.ListArticleItem
import com.example.daunsehat.data.remote.response.LoginResponse
import com.example.daunsehat.data.remote.response.ProfileResponse
import com.example.daunsehat.data.remote.response.UserArticleResponse
import com.example.daunsehat.data.remote.retrofit.ApiService
import com.example.daunsehat.data.remote.retrofit.EditProfileRequest
import com.example.daunsehat.data.remote.retrofit.LoginRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }

    fun loginUser(
        email: String,
        password: String
    ): LiveData<ResultApi<LoginResponse>> = liveData {
        emit(ResultApi.Loading)
        try {
            val response = apiService.loginUser(LoginRequest(email, password))
            emit(ResultApi.Success(response))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string()?.let { errorBody ->
                Gson().fromJson(errorBody, ErrorResponse::class.java).error
            } ?: e.message()
            emit(ResultApi.Error(errorMessage))
        } catch (e: IOException) {
            emit(ResultApi.Error("No internet connection. Please check your network."))
        } catch (e: Exception) {
            emit(ResultApi.Error("An unexpected error occurred: ${e.message}"))
        } catch (e: SocketTimeoutException) {
            emit(ResultApi.Error("Timeout: Server took too long to respond"))
        }
    }

    fun getProfile(): LiveData<ResultApi<ProfileResponse>> = liveData {
        emit(ResultApi.Loading)
        try {
            val token = userPreference.getSession().first().token
            val response = apiService.getProfile("Bearer $token")
            emit(ResultApi.Success(response))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string()?.let { errorBody ->
                Gson().fromJson(errorBody, ErrorResponse::class.java).error
            } ?: e.message()
            emit(ResultApi.Error(errorMessage))
        } catch (e: IOException) {
            emit(ResultApi.Error("No internet connection. Please check your network."))
        } catch (e: Exception){
            emit(ResultApi.Error("An unexpected error occurred: ${e.message}"))
        } catch (e: SocketTimeoutException) {
            emit(ResultApi.Error("Timeout: Server took too long to respond"))
        }
    }

    fun editProfile(name: String, email: String): LiveData<ResultApi<ProfileResponse>> = liveData {
        emit(ResultApi.Loading)
        try {
            val token = userPreference.getSession().first().token
            val response = apiService.editProfile("Bearer $token", EditProfileRequest(name, email))
            val updatedEmail = response.user?.email ?: ""
            val updatedToken = response.token ?: ""
            if (response.token != token) {
                userPreference.saveSession(UserModel(updatedEmail, updatedToken))
            }
            emit(ResultApi.Success(response))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string()?.let { errorBody ->
                Gson().fromJson(errorBody, ErrorResponse::class.java).error
            } ?: e.message()
            emit(ResultApi.Error(errorMessage))
        } catch (e: IOException) {
            emit(ResultApi.Error("No internet connection. Please check your network."))
        } catch (e: Exception){
            emit(ResultApi.Error("An unexpected error occurred: ${e.message}"))
        } catch (e: SocketTimeoutException) {
            emit(ResultApi.Error("Timeout: Server took too long to respond"))
        }
    }

    fun editPhotoProfile(imageFile: File): LiveData<ResultApi<ProfileResponse>> = liveData {
        emit(ResultApi.Loading)
        try {
            val token = userPreference.getSession().first().token
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData("file", imageFile.name, requestImageFile)
            val response = apiService.editPhotoProfile("Bearer $token", multipartBody)
            val updatedEmail = response.user?.email ?: ""
            val updatedToken = response.token ?: ""
            if (response.token != token) {
                userPreference.saveSession(UserModel(updatedEmail, updatedToken))
            }
            emit(ResultApi.Success(response))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string()?.let { errorBody ->
                Gson().fromJson(errorBody, ErrorResponse::class.java).error
            } ?: e.message()
            emit(ResultApi.Error(errorMessage))
        } catch (e: IOException) {
            emit(ResultApi.Error("No internet connection. Please check your network."))
        } catch (e: Exception) {
            emit(ResultApi.Error("An unexpected error occurred: ${e.message}"))
        } catch (e: SocketTimeoutException) {
            emit(ResultApi.Error("Timeout: Server took too long to respond"))
        }
    }


    fun getAllArticle(): LiveData<ResultApi<List<ListArticleItem>>> = liveData {
        emit(ResultApi.Loading)
        try {
            val token = userPreference.getSession().first().token
            val response = apiService.getAllArticle("Bearer $token")
            emit(ResultApi.Success(response))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string()?.let { errorBody ->
                Gson().fromJson(errorBody, ErrorResponse::class.java).error
            } ?: e.message()
            emit(ResultApi.Error(errorMessage))
        } catch (e: IOException) {
            emit(ResultApi.Error("No internet connection. Please check your network."))
        } catch (e: Exception){
            emit(ResultApi.Error("An unexpected error occurred: ${e.message}"))
        } catch (e: SocketTimeoutException) {
            emit(ResultApi.Error("Timeout: Server took too long to respond"))
        }
    }

    fun getUserArticle(): LiveData<ResultApi<List<ListArticleItem>>> = liveData {
        emit(ResultApi.Loading)
        try {
            val token = userPreference.getSession().first().token
            val response = apiService.getUserArticle("Bearer $token")
            emit(ResultApi.Success(response))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string()?.let { errorBody ->
                Gson().fromJson(errorBody, ErrorResponse::class.java).error
            } ?: e.message()
            emit(ResultApi.Error(errorMessage))
        } catch (e: IOException) {
            emit(ResultApi.Error("No internet connection. Please check your network."))
        } catch (e: Exception){
            emit(ResultApi.Error("An unexpected error occurred: ${e.message}"))
        } catch (e: SocketTimeoutException) {
            emit(ResultApi.Error("Timeout: Server took too long to respond"))
        }
    }

    fun getDetailArticle(articleId: String): LiveData<ResultApi<DetailArticleResponse>> = liveData {
        emit(ResultApi.Loading)
        try {
            val token = userPreference.getSession().first().token
            val response = apiService.getDetailArticle("Bearer $token", articleId)
            emit(ResultApi.Success(response))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string()?.let { errorBody ->
                Gson().fromJson(errorBody, ErrorResponse::class.java).error
            } ?: e.message()
            emit(ResultApi.Error(errorMessage))
        } catch (e: IOException) {
            emit(ResultApi.Error("No internet connection. Please check your network."))
        } catch (e: Exception){
            emit(ResultApi.Error("An unexpected error occurred: ${e.message}"))
        } catch (e: SocketTimeoutException) {
            emit(ResultApi.Error("Timeout: Server took too long to respond"))
        }
    }

    fun getSearchArticle(query: String): LiveData<ResultApi<List<ListArticleItem>>> = liveData {
        emit(ResultApi.Loading)
        try {
            val token = userPreference.getSession().first().token
            val response = apiService.searchArticle("Bearer $token", query)
            emit(ResultApi.Success(response))
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string()?.let { errorBody ->
                Gson().fromJson(errorBody, ErrorResponse::class.java).error
            } ?: e.message()
            emit(ResultApi.Error(errorMessage))
        } catch (e: IOException) {
            emit(ResultApi.Error("No internet connection. Please check your network."))
        } catch (e: Exception){
            emit(ResultApi.Error("An unexpected error occurred: ${e.message}"))
        } catch (e: SocketTimeoutException) {
            emit(ResultApi.Error("Timeout: Server took too long to respond"))
        }
    }

    fun deleteUserArticle(articleId: String): LiveData<ResultApi<UserArticleResponse>> = liveData {
        emit(ResultApi.Loading)
        try {
            val token = "Bearer ${userPreference.getSession().first().token}"
            Log.d("DeleteArticlexxx", "Token: $token, ArticleID: $articleId")
            val response = apiService.deleteUserArticle(token, articleId)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(ResultApi.Success(it))
                    Log.d("DeleteArticlexxx", "Success: ${it.message}")
                } ?: run {
                    emit(ResultApi.Error("Response body is null"))
                }
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Log.e("DeleteArticlexxx", "Error Response: $error")
                emit(ResultApi.Error("Error ${response.code()}: $error"))
            }
        } catch (e: HttpException) {
            Log.e("DeleteArticlexxx", "Exception: ${e.localizedMessage}", e)
            emit(ResultApi.Error("An error occurred: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(ResultApi.Error("No internet connection. Please check your network."))
        } catch (e: Exception){
            emit(ResultApi.Error("An unexpected error occurred: ${e.message}"))
        } catch (e: SocketTimeoutException) {
            emit(ResultApi.Error("Timeout: Server took too long to respond"))
        }
    }

    fun addArticle(
        token: String,
        imageFile: File?,
        title: String,
        description: String
    ): LiveData<ResultApi<AddArticleResponse>> = liveData {
        emit(ResultApi.Loading)
        try {
            val requestTitle = title.toRequestBody("text/plain".toMediaType())
            val requestDescription = description.toRequestBody("text/plain".toMediaType())

            if (imageFile != null) {
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody =
                    MultipartBody.Part.createFormData("file", imageFile.name, requestImageFile)
                val successResponse = apiService.addArticle(
                    "Bearer $token",
                    multipartBody,
                    requestTitle,
                    requestDescription
                )
                emit(ResultApi.Success(successResponse))
            } else {
                val successResponse = apiService.addArticle(
                    "Bearer $token",
                    null,
                    requestTitle,
                    requestDescription
                )
                emit(ResultApi.Success(successResponse))
            }
        } catch (e: HttpException) {
            val errorMessage = e.response()?.errorBody()?.string()?.let { errorBody ->
                Gson().fromJson(errorBody, ErrorResponse::class.java).error
            } ?: e.message()
            emit(ResultApi.Error(errorMessage))
        } catch (e: IOException) {
            emit(ResultApi.Error("No internet connection. Please check your network."))
        } catch (e: Exception){
            emit(ResultApi.Error("An unexpected error occurred: ${e.message}"))
        } catch (e: SocketTimeoutException) {
            emit(ResultApi.Error("Timeout: Server took too long to respond"))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }
}