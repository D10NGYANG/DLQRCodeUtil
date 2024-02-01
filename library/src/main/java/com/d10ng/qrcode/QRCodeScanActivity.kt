package com.d10ng.qrcode

import android.Manifest
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.d10ng.app.managers.PermissionManager
import com.d10ng.app.view.lockScreenOrientation
import com.d10ng.app.view.setStatusBar
import com.d10ng.compose.ui.AppShape
import com.d10ng.compose.ui.AppText
import kotlinx.coroutines.launch
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

@Composable
fun QRCodeScanView(
    onClickBack: () -> Unit,
) {
    var hasPermission by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        launch {
            hasPermission = PermissionManager.request(Manifest.permission.CAMERA)
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
        if (hasPermission) {
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