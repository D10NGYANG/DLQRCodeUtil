# DLQRCodeUtil
jetpack compose android QRCode scan

[![](https://jitpack.io/v/D10NGYANG/DLQRCodeUtil.svg)](https://jitpack.io/#D10NGYANG/DLQRCodeUtil)

## 功能
- [x] 单次扫描二维码并返回结果
- [ ] 识别多个二维码
- [ ] 支持相册二维码扫描

## 添加依赖
1 添加仓库
```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://raw.githubusercontent.com/D10NGYANG/maven-repo/main/repository'}
  }
}
```

2 添加依赖
```gradle
dependencies {
    // jetpack compose 框架
    implementation 'com.github.D10NGYANG:DLJetpackComposeUtil:1.3.5'
    // 权限申请
    implementation "com.google.accompanist:accompanist-permissions:0.30.1"
    // 二维码扫描
    implementation 'com.github.D10NGYANG:DLQRCodeUtil:0.0.7'
}
```
3 混淆
```properties
-keep class com.d10ng.qrcode.** {*;}
-dontwarn com.d10ng.qrcode.**
```
## 使用

在需要的地方调用：
```kotlin
// 启动扫描
QRCodeScanManager.instant.startScanActivity(activity) { scanResult ->
    // scanResult 为扫描结果字符串
}
```

## 预览
<img src="https://github.com/D10NGYANG/DLQRCodeUtil/blob/master/image/image1.png" width="200"/>