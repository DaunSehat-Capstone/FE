package com.example.daunsehat.data.remote.response

import com.google.gson.annotations.SerializedName

data class HistoryPredictResponse(

	@field:SerializedName("HistoryPredictResponse")
	val historyPredictResponse: List<HistoryPredictResponseItem> = emptyList()
)

data class HistoryPredictResponseItem(

	@field:SerializedName("treatment")
	val treatment: String? = null,

	@field:SerializedName("plant_condition")
	val plantCondition: String? = null,

	@field:SerializedName("prediction_id")
	val predictionId: Int? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("plant")
	val plant: String? = null,

	@field:SerializedName("confidence")
	val confidence: String? = null,

	@field:SerializedName("image_plant")
	val imagePlant: String? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null
)
