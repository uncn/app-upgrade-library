package com.uncn.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.uncn.upgrade.AppUpgradeManager

class MainActivity : AppCompatActivity() {

    private lateinit var upgradeManager: AppUpgradeManager
    private lateinit var btnDownload: Button
    private lateinit var btnCancel: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgress: TextView
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        upgradeManager = AppUpgradeManager(this)

        btnDownload = findViewById(R.id.btnDownload)
        btnCancel = findViewById(R.id.btnCancel)
        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tvProgress)
        tvStatus = findViewById(R.id.tvStatus)

        btnDownload.setOnClickListener {
            startDownload()
        }

        btnCancel.setOnClickListener {
            upgradeManager.cancelDownload()
            tvStatus.text = "已取消下载"
            progressBar.progress = 0
            tvProgress.text = "0%"
        }

        tvStatus.text = "点击下载开始更新"
    }

    private fun startDownload() {
        tvStatus.text = "开始下载..."
        progressBar.progress = 0
        btnDownload.isEnabled = false

        // 使用示例 APK URL（请替换为实际 URL）
        val downloadUrl = "https://example.com/app.apk"

        upgradeManager.startDownload(
            downloadUrl = downloadUrl,
            fileName = "app-update.apk",
            onProgress = { progress ->
                Log.d("UPDATE", "下载进度: $progress%")
                runOnUiThread {
                    progressBar.progress = progress
                    tvProgress.text = "$progress%"
                    tvStatus.text = "正在下载中..."
                }
            },
            onSuccess = { filePath ->
                Log.d("UPDATE", "下载完成: $filePath")
                runOnUiThread {
                    tvStatus.text = "下载完成，请查看通知栏安装"
                    btnDownload.isEnabled = true
                }
            },
            onError = { error ->
                Log.e("UPDATE", "下载失败: $error")
                runOnUiThread {
                    tvStatus.text = "下载失败: $error"
                    btnDownload.isEnabled = true
                    progressBar.progress = 0
                    tvProgress.text = "0%"
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        upgradeManager.release()
    }
}