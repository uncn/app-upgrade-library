# APP 升级库

一个完整的 Android APP 升级库，支持后台下载 APK、实时显示下载进度、自动安装功能。

## ✨ 功能特性

- 📥 **智能下载** - 使用 OkHttp 实现高效的 APK 下载
- 📊 **进度显示** - 实时更新通知栏下载进度
- 🔔 **完成提示** - 下载完成后自动弹出安装通知
- 🚀 **一键安装** - 点击通知后自动启动 APK 安装
- ⚡ **异步处理** - 基于 Kotlin Coroutines，不阻塞主线程
- 🛡️ **安全可靠** - 完善的错误处理和权限管理
- 📱 **兼容性好** - 支持 Android 6.0~15.0

## 🏗️ 项目结构

```
app-upgrade-library/
├── app/                          # 示例应用模块
│   ├── src/main/
│   │   ├── kotlin/com/uncn/demo/
│   │   │   └── MainActivity.kt
│   │   ├── res/
│   │   │   ├── layout/activity_main.xml
│   │   │   └── values/
│   │   │       ├── strings.xml
│   │   │       ├── colors.xml
│   │   │       └── themes.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
│
├── upgrade/                       # 升级库核心模块
│   ├── src/main/
│   │   ├── kotlin/com/uncn/upgrade/
│   │   │   ├── AppUpgradeManager.kt          # 核心管理器
│   │   │   ├── AppUpgradeLogger.kt           # 日志工具
│   │   │   ├── downloader/
│   │   │   │   └── AppDownloader.kt          # 文件下载器
│   │   │   ├── notification/
│   │   │   │   └── AppUpgradeNotificationManager.kt  # 通知管理
│   │   │   └── receiver/
│   │   │       └── AppInstallReceiver.kt     # 安装接收器
│   │   ├── res/xml/
│   │   │   └── file_paths.xml                # FileProvider 配置
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
│
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
└── .gitignore
```

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/uncn/app-upgrade-library.git
cd app-upgrade-library
```

### 2. 在 Android Studio 中打开

- File → Open → 选择项目目录
- 等待 Gradle 构建完成

### 3. 基本使用

```kotlin
val manager = AppUpgradeManager(context)

manager.startDownload(
    downloadUrl = "https://example.com/app-v2.0.apk",
    fileName = "app-v2.0.apk",
    onProgress = { progress ->
        Log.d("UPDATE", "下载进度: $progress%")
    },
    onSuccess = { filePath ->
        Log.d("UPDATE", "下载完成: $filePath")
        // 通知会自动显示安装提示
    },
    onError = { error ->
        Log.e("UPDATE", "下载失败: $error")
    }
)
```

## 📖 详细用法

### 启动下载

```kotlin
val manager = AppUpgradeManager(this)

manager.startDownload(
    downloadUrl = "下载链接",           // 必填：APK 下载地址
    fileName = "app.apk",              // 可选：保存文件名
    onProgress = { progress ->          // 可选：进度回调 (0-100)
        updateUI(progress)
    },
    onSuccess = { filePath ->           // 可选：成功回调
        showSuccess(filePath)
    },
    onError = { errorMsg ->             // 可选：失败回调
        showError(errorMsg)
    }
)
```

### 手动安装 APK

```kotlin
manager.installApk("/path/to/app.apk")
```

### 取消下载

```kotlin
manager.cancelDownload()
```

### 清除通知

```kotlin
manager.dismissNotification()
```

### 释放资源

```kotlin
override fun onDestroy() {
    super.onDestroy()
    manager.release()  // 必须调用
}
```

## 🔧 集成到您的项目

如果要在其他项目中使用此库：

1. **在项目的 `settings.gradle.kts` 中添加**：

```gradle
include(":upgrade")
project(":upgrade").projectDir = File("path/to/upgrade")
```

2. **在 App 的 `build.gradle.kts` 中添加依赖**：

```gradle
dependencies {
    implementation(project(":upgrade"))
}
```

## 📋 权限要求

应用需要以下权限（已在库的 `AndroidManifest.xml` 中声明）：

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

## 🔐 安全特性

- ✅ 使用 `FileProvider` 处理 Android 7.0+ 文件权限
- ✅ 支持 `REQUEST_INSTALL_PACKAGES` 权限处理
- ✅ 完善的异常捕获和错误恢复
- ✅ 安全的通知栏处理

## 🛠️ 技术栈

- **语言**: Kotlin 1.9.10
- **构建**: Gradle 8.1.0
- **最低API**: 21 (Android 5.0)
- **目标API**: 34 (Android 14)
- **依赖**:
  - AndroidX Core: 1.12.0
  - OkHttp: 4.11.0
  - Coroutines: 1.7.3
  - Lifecycle: 2.6.2

## 📱 支持平台

- ✅ Android 5.0 (API 21)
- ✅ Android 6.0 (API 23)
- ✅ Android 7.0 (API 24)
- ✅ Android 8.0+ (API 26+)
- ✅ Android 14 (API 34)
- ✅ Android 15 (API 35)

## 🐛 调试

### 启用调试日志

```kotlin
AppUpgradeLogger.setDebugMode(true)
```

### 查看日志

```bash
adb logcat | grep AppUpgrade
```

## 📞 常见问题

### Q: 如何修改通知样式？
A: 编辑 `AppUpgradeNotificationManager.kt` 中的通知构建代码

### Q: 支持断点续传吗？
A: 当前版本不支持，可在 `AppDownloader.kt` 中扩展实现

### Q: 下载文件保存在哪里？
A: 保存在应用的外部文件目录 `context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)`

### Q: 可以自定义下载文件名吗？
A: 可以，通过 `startDownload()` 的 `fileName` 参数设置

## 📄 许可证

MIT License

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📧 联系方式

如有问题，请在 GitHub Issues 中提出。
