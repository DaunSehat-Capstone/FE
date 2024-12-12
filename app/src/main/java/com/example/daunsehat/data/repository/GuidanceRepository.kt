package com.example.daunsehat.data.repository

import com.example.daunsehat.data.remote.response.GuidanceResponse
import com.example.daunsehat.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class GuidanceRepository(private val apiService: ApiService) {

    suspend fun postGuidanceArticle(
        token: String,
        file: MultipartBody.Part?,
        title: RequestBody,
        body: RequestBody,
        category: RequestBody
    ) = apiService.postGuidanceArticle(token, file, title, body, category)

    suspend fun getAllGuidanceArticles(token: String): List<GuidanceResponse> {
        return apiService.getAllGuidanceArticles(token)
    }

    suspend fun getGuidanceArticlesByCategory(token: String, category: String): List<GuidanceResponse> {
        return apiService.getGuidanceArticlesByCategory(token, category)
    }

    suspend fun deleteGuidanceArticleById(token: String, articleId: Int) {
        apiService.deleteGuidanceArticleById(token, articleId)
    }
}