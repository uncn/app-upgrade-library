package com.uncn.upgrade

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.uncn.upgrade.notification.AppUpgradeNotificationManager
import com.uncn.upgrade.downloader.AppDownloader
import kotlinx.coroutines.*
import java.io.File

/**
 * APP 升级管理器
 * 
 * 使用示例：
 * val manager = AppUpgradeManager(context)
 * manager.startDownload(
 *     downloadUrl = "https://example.com/app.apk",
 *     fileName = "app-v1.0.apk",
 *     onProgress = { progress -> println("$progress%") },
 *     onSuccess = { path -> println("下载完成") },
 *     onError = { error -> println("下载失败: $error") }
 * )
 */
class AppUpgradeManager(private val context: Context) {

    private val notificationManager = AppUpgradeNotificationManager(context)
    private val downloader = AppDownloader(context)
    private var downloadJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    fun startDownload(
        downloadUrl: String,
        fileName: String = "app_upgrade.apk",
        onProgress: ((progress: Int) -> Unit)? = null,
        onSuccess: ((filePath: String) -> Unit)? = null,
        onError: ((errorMsg: String) -> Unit)? = null
    ) {
        downloadJob?.cancel()
        downloadJob = scope.launch {
            try {
                notificationManager.showDownloadingNotification()

                val filePath = downloader.downloadFile(
                    downloadUrl = downloadUrl,
                    fileName = fileName,
                    onProgress = { progress ->
                        notificationManager.updateProgress(progress)
                        onProgress?.invoke(progress)
                    }
                )

                AppUpgradeLogger.d("Download completed: $filePath")
                notificationManager.showInstallNotification(filePath)
                onSuccess?.invoke(filePath)
            } catch (e: Exception) {
                AppUpgradeLogger.e("Download failed: ${e.message}", e)
                notificationManager.showErrorNotification(e.message ?: "Download failed")
                onError?.invoke(e.message ?: "Unknown error")
            }
        }
    }

    fun installApk(apkPath: String) {
        try {
            val file = File(apkPath)
            if (!file.exists()) {
                AppUpgradeLogger.e("APK file not found: $apkPath")
                notificationManager.showErrorNotification("APK file not found")
                return
            }

            val apkUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } else {
                Uri.fromFile(file)
            }

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(intent)
            AppUpgradeLogger.d("Starting APK installation: $apkPath")
        } catch (e: Exception) {
            AppUpgradeLogger.e("Failed to install APK: ${e.message}", e)
            notificationManager.showErrorNotification("Failed to install: ${e.message}")
        }
    }

    fun cancelDownload() {
        downloadJob?.cancel()
        downloader.cancelDownload()
        notificationManager.cancelNotification()
        AppUpgradeLogger.d("Download cancelled")
    }

    fun dismissNotification() {
        notificationManager.cancelNotification()
    }

    fun release() {
        cancelDownload()
        scope.cancel()
        notificationManager.release()
    }
}