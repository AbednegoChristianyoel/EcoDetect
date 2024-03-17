package com.example.ecodetect.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ecodetect.data.response.PredictionResult
import com.example.ecodetect.data.response.RecommendationResponse
import com.example.ecodetect.data.response.RecommendationResponseItem
import com.example.ecodetect.ml.TfModelResnet50Optimized
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class EcoDetectRepository {

    fun getRecommendationsByType(jenis: String, context: Context): List<RecommendationResponseItem?>? {
        return try {
            val jsonString = context.assets.open("rekomendasi_daur_ulang.json").bufferedReader().use { it.readText() }
            val typeToken = object : TypeToken<RecommendationResponse>() {}.type
            val recommendationResponse: RecommendationResponse? = Gson().fromJson(jsonString, typeToken)
            recommendationResponse?.recommendationResponse?.filter { it.jenis == jenis }
        } catch (e: Exception) {
            Log.e("JSONParsing", "Error parsing JSON", e)
            null
        }
    }

    suspend fun classifyImage(image: Bitmap, context: Context): LiveData<PredictionResult> {
        return withContext(Dispatchers.Default) {
            val result = MutableLiveData<PredictionResult>()

            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                .add(NormalizeOp(0f, 255f))
                .build()

            // Load TensorFlow Lite model
            val model = TfModelResnet50Optimized.newInstance(context)


            var tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(image)

            tensorImage = imageProcessor.process(tensorImage)

            // Preprocess the image
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(tensorImage.buffer)

            // Run model inference
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val confidences = outputFeature0.floatArray
            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }

            val classes = arrayOf("kaca", "kardus", "kertas", "logam", "organik", "plastik")

            // output
            val className = classes[maxPos]
            val type = if (className in arrayOf("kaca", "kardus", "kertas", "logam", "plastik")) "Sampah daur ulang" else "Sampah organik"
            val maxCon = confidences[maxPos] * 100 // Mengonversi ke persentase
            val accuracy = String.format("%.1f%%", maxCon)

            val predictionResult = PredictionResult(className, type, accuracy)

            // Release model resources
            model.close()

            // Return the classification result
            result.postValue(predictionResult)

            result
        }
    }
}