package com.example.daunsehat.utils
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object ImageLoader {
    fun downloadImage(context: Context, imageUrl: String, onImageReady: (Bitmap?) -> Unit) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(imageUrl)
            .build()

        Log.d("ImageLoader", "Downloading image from: $imageUrl")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ImageDownload", "Failed to download image: ${e.message}")
                // Run on the main thread to update UI
                Handler(Looper.getMainLooper()).post {
                    onImageReady(null) // Pass null if download fails
                }
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    Log.e("ImageDownload", "Error: ${response.message}")
                    // Run on the main thread to update UI
                    Handler(Looper.getMainLooper()).post {
                        onImageReady(null) // Pass null if response is not successful
                    }
                    return
                }

                // Check if the response body is null or empty
                val inputStream = response.body?.byteStream()
                if (inputStream == null) {
                    Log.e("ImageDownload", "Input stream is null")
                    Handler(Looper.getMainLooper()).post {
                        onImageReady(null) // Return null if input stream is invalid
                    }
                    return
                }

                // Try to decode the bitmap from the stream
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // If bitmap is null, log the failure and return null
                if (bitmap == null) {
                    Log.e("ImageDownload", "Failed to decode bitmap from input stream")
                    Handler(Looper.getMainLooper()).post {
                        onImageReady(null) // Return null if bitmap decoding fails
                    }
                } else {
                    // Successfully decoded the bitmap
                    Handler(Looper.getMainLooper()).post {
                        onImageReady(bitmap)
                    }
                }
            }
        })
    }
}
