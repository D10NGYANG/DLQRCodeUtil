package com.d10ng.qrcode

import android.Manifest
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.d10ng.compose.BaseActivity
import com.d10ng.compose.ui.AppText
import com.d10ng.compose.ui.AppTheme
import com.d10ng.compose.view.SolidButtonWithText
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.lang.ref.WeakReference

class QRCodeScanActivity: BaseActivity() {

    companion object {
        var instant: WeakReference<QRCodeScanActivity?> = WeakReference(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instant = WeakReference(this)
        setContent {
            AppTheme(app = app) {
                QRCodeScanView {
                    finish()
                }
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
    val infiniteTransition = rememberInfiniteTransition()
    val pos by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
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