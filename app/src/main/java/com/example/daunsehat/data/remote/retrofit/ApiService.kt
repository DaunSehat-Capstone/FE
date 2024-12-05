package com.example.daunsehat.data.remote.retrofit

import com.example.daunsehat.data.remote.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val password: String
)

interface ApiService {
    @POST("user/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): LoginResponse
}