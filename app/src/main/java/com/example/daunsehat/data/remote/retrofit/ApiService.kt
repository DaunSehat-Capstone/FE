package com.example.daunsehat.data.remote.retrofit

import com.example.daunsehat.data.remote.response.LoginResponse
import com.example.daunsehat.data.remote.response.RegisterResponse
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

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


interface ApiService {
    @POST("user/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("user/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): RegisterResponse
}