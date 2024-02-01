package com.d10ng.qrcode

import android.Manifest
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.d10ng.compose.ui.AppShape
import com.d10ng.compose.ui.AppText
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.lang.ref.WeakReference

class QRCodeScanActivity : ComponentActivity() {

    companion object {
        var instant: WeakReference<QRCodeScanActivity?> = WeakReference(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instant = WeakReference(this)

        // 锁定屏幕方向
        lockScreenOrientation()
        // 设置状态栏颜色
        setStatusBar()

        setContent {
            QRCodeScanView {
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                QRCodeScanManager.instant.scanFinish()
            }
        })
    }

    override fun onDestroy() {
        instant = WeakReference(null)
        super.onDestroy()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRCodeScanView(
    onClickBack: () -> Unit,
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // 循环动画
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val pos by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "pos"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
    ) {
        if (cameraPermissionState.status.isGranted) {
            CameraPreviewView(
                modifier = Modifier
                    .fillMaxSize(),
                analyzer = QRCodeAnalyzer {
                    QRCodeScanManager.instant.scanFinish(it)
                }
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.sharp_cancel_24),
            contentDescription = "返回",
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .clip(AppShape.RC.Cycle)
                .clickable { onClickBack() },
            tint = Color.White
        )
        Text(
            text = "扫描二维码",
            style = AppText.Medium.Body.v14,
            color = Color.White,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 100.dp)
                .align(Alignment.BottomCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.33f)
                .align(Alignment.Center)
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(pos))
            Image(
                painter = painterResource(id = R.mipmap.scan_line),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

/**
 * 锁定屏幕方向
 * - 除了在Activity中设置当前方法以外还需要，在主题中设置以下内容
 * <resources>
 *     <style name="AppTheme" parent="Theme.MaterialComponents.Light.NoActionBar">
 *         <!-- 锁定布局在发生以下改变时，不重置状态 -->
 *         <item name="android:configChanges">orientation|keyboardHidden|screenSize|locale</item>
 *     </style>
 * </resources>
 * - 还需要在AndroidManifest.xml中为您的activity设置以下内容
 * <activity
 *     android:name=".XActivity"
 *     android:screenOrientation="locked" />
 * @receiver Activity
 * @param isVertical Boolean 是否为竖向
 */
private fun Activity.lockScreenOrientation(isVertical: Boolean = true) {
    val orientation =
        if (isVertical) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    if (requestedOrientation != orientation) {
        requestedOrientation = orientation
    }
}

/**
 * 状态栏设置
 * @receiver Activity
 * @param fullScreen Boolean 是否全屏，沉浸式状态栏
 * @param color Int 状态栏颜色
 * @param darkText Boolean 状态栏字体颜色是否为黑色
 */
private fun Activity.setStatusBar(
    fullScreen: Boolean = true,
    color: Int = 0,
    darkText: Boolean = true
) {
    // 沉浸式状态栏
    WindowCompat.setDecorFitsSystemWindows(window, !fullScreen)
    // 设置状态栏颜色
    window.statusBarColor = color
    // 设置状态栏字体颜色
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
        darkText
}