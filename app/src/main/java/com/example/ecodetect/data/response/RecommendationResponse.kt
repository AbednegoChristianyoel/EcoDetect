package com.example.ecodetect.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class RecommendationResponse(

	@field:SerializedName("recommendationResponse")
	val recommendationResponse: List<RecommendationResponseItem>? = null
) : Parcelable

@Parcelize
data class RecommendationResponseItem(

	@field:SerializedName("metode")
	val metode: List<MetodeItem?>? = null,

	@field:SerializedName("jenis")
	val jenis: String? = null,

	@field:SerializedName("total_methods")
	val totalMethods: Int? = null,

	@field:SerializedName("tipe")
	val tipe: String? = null
) : Parcelable

@Parcelize
data class MetodeItem(

	@field:SerializedName("langkah")
	val langkah: List<String?>? = null,

	@field:SerializedName("url_gambar")
	val urlGambar: String? = null,

	@field:SerializedName("judul")
	val judul: String? = null,

	@field:SerializedName("alat_dan_bahan")
	val alatDanBahan: List<String?>? = null
) : Parcelable
