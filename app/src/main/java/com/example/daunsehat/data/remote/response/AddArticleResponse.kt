package com.example.daunsehat.data.remote.response

import com.google.gson.annotations.SerializedName

data class AddArticleResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("article")
	val article: Article? = null
)

data class Article(

	@field:SerializedName("article_id")
	val articleId: Int? = null,

	@field:SerializedName("body_article")
	val bodyArticle: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("title_article")
	val titleArticle: String? = null,

	@field:SerializedName("image_article")
	val imageArticle: String? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)
