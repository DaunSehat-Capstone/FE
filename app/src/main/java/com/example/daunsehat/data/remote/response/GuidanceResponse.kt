package com.example.daunsehat.data.remote.response

import com.google.gson.annotations.SerializedName

data class GuidanceResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title_guidance")
    val title: String,

    @SerializedName("body_guidance")
    val body: String,

    @SerializedName("category_guidance")
    val category: String,

    @SerializedName("file_url")
    val fileUrl: String?
)