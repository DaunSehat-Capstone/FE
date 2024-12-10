package com.example.daunsehat.data.remote.response

import com.google.gson.annotations.SerializedName

data class ListArticleResponse(

	@field:SerializedName("ListArticleResponse")
	val listArticleResponse: List<ListArticleItem> = emptyList(),
)

data class ListArticleItem(

	@field:SerializedName("article_id")
	val articleId: Int? = null,

	@field:SerializedName("body_article")
	val bodyArticle: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("image_url")
	val imageUrl: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("title_article")
	val titleArticle: String? = null,

	@field:SerializedName("image_article")
	val imageArticle: String? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)
