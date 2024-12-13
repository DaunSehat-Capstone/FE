package com.example.daunsehat.utils
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
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

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    onImageReady(null)
                }
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    Handler(Looper.getMainLooper()).post {
                        onImageReady(null)
                    }
                    return
                }

                val inputStream = response.body?.byteStream()
                if (inputStream == null) {
                    Handler(Looper.getMainLooper()).post {
                        onImageReady(null)
                    }
                    return
                }

                val bitmap = BitmapFactory.decodeStream(inputStream)

                if (bitmap == null) {
                    Handler(Looper.getMainLooper()).post {
                        onImageReady(null)
                    }
                } else {
                    Handler(Looper.getMainLooper()).post {
                        onImageReady(bitmap)
                    }
                }
            }
        })
    }
}
