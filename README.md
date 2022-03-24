# DLQRCodeUtil
jetpack compose android QRCode scan

[![](https://jitpack.io/v/D10NGYANG/DLQRCodeUtil.svg)](https://jitpack.io/#D10NGYANG/DLQRCodeUtil)

## 功能
- [x] 单次扫描二维码并返回结果
- [ ] 识别多个二维码
- [ ] 支持相册二维码扫描

## 添加依赖
1 Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
2 Add the dependency
```gradle
dependencies {
    // jetpack compose 框架
    implementation 'com.github.D10NGYANG:DLBasicJetpackComposeApp:+'
    // 二维码扫描
    implementation 'com.github.D10NGYANG:DLQRCodeUtil:0.0.2'
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