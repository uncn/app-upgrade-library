package com.uncn.upgrade.downloader

import android.content.Context
import android.os.Environment
import com.uncn.upgrade.AppUpgradeLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

class AppDownloader(private val context: Context) {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private var currentRequest: okhttp3.Call? = null

    suspend fun downloadFile(
        downloadUrl: String,
        fileName: String,
        onProgress: (progress: Int) -> Unit
    ): String = withContext(Dispatchers.IO) {
        val downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: throw IOException("Unable to access download directory")

        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }

        val file = File(downloadDir, fileName)
        if (file.exists()) {
            file.delete()
        }

        try {
            val request = Request.Builder().url(downloadUrl).build()
            currentRequest = okHttpClient.newCall(request)
            val response = currentRequest?.execute()
                ?: throw IOException("Request failed")

            if (!response.isSuccessful) {
                throw IOException("Download failed: ${response.code}")
            }

            val body = response.body ?: throw IOException("Response body is null")
            val contentLength = body.contentLength()

            if (contentLength <= 0) {
                throw IOException("Invalid content length")
            }

            file.outputStream().use { output ->
                body.byteStream().use { input ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalBytesRead = 0L

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        val progress = (totalBytesRead * 100 / contentLength).toInt().coerceIn(0, 100)
                        AppUpgradeLogger.d("Download progress: $progress%")
                        onProgress(progress)
                    }
                }
            }

            AppUpgradeLogger.d("Download completed: ${file.absolutePath}")
            file.absolutePath
        } catch (e: Exception) {
            if (file.exists()) file.delete()
            AppUpgradeLogger.e("Download error: ${e.message}", e)
            throw e
        }
    }

    fun cancelDownload() {
        currentRequest?.cancel()
        AppUpgradeLogger.d("Download cancelled")
    }
}