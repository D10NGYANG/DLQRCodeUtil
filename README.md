# DLQRCodeUtil
jetpack compose android QRCode scan

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
    implementation 'com.github.D10NGYANG:DLJetpackComposeUtil:2.0.25'
    // 通用APP工具（含权限管理
    implementation 'com.github.D10NGYANG:DLAppUtil:2.5.4'
    // 二维码扫描
    implementation 'com.github.D10NGYANG:DLQRCodeUtil:0.1.2'
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
scope.launch {
    // 扫描二维码
    val result = QRCodeScanManager.instant.scanQRCode(activity)
    // result 为扫描结果字符串，如果为empty则为扫描失败或者取消扫描
    if (result.isEmpty()) {
        Toast.makeText(activity, "扫描失败或取消", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(activity, "扫描结果：$result", Toast.LENGTH_SHORT).show()
    }
}
```

## 预览
<img src="https://github.com/D10NGYANG/DLQRCodeUtil/blob/master/image/image1.png" width="200"/>