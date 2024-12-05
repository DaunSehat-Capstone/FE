package com.example.daunsehat.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.daunsehat.data.pref.UserModel
import com.example.daunsehat.data.pref.UserPreference
import com.example.daunsehat.data.remote.response.ErrorResponse
import com.example.daunsehat.data.remote.response.LoginResponse
import com.example.daunsehat.data.remote.retrofit.ApiService
import com.example.daunsehat.data.remote.retrofit.LoginRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
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