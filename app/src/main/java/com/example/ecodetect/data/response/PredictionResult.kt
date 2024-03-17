package com.example.ecodetect.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PredictionResult(
    val className: String,
    val type: String,
    val accuracy: String
) : Parcelable