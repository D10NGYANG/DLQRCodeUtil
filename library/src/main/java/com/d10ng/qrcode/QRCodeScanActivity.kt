package com.d10ng.qrcode

import android.Manifest
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Cancel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.d10ng.basicjetpackcomposeapp.BaseActivity
import com.d10ng.basicjetpackcomposeapp.compose.AppText
import com.d10ng.basicjetpackcomposeapp.compose.AppTheme
import com.d10ng.basicjetpackcomposeapp.hasPermissions
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding

class QRCodeScanActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            /** 是否拥有相机权限 */
            var hasCamPermission by remember {
                mutableStateOf(hasPermissions(arrayOf(Manifest.permission.CAMERA)))
            }

            /** 启动后获取相机权限 */
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { granted ->
                    hasCamPermission = granted
                }
            )
            LaunchedEffect(Unit) {
                launcher.launch(Manifest.permission.CAMERA)
            }

            AppTheme(app = app) {

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
                    if (hasCamPermission) {
                        CameraPreviewView(
                            modifier = Modifier
                                .fillMaxSize(),
                            analyzer = QRCodeAnalyzer {
                                QRCodeScanManager.instant.scanFinish(it)
                                if (it.isNotEmpty()) {
                                    finish()
                                }
                            }
                        )
                    }
                    Icon(
                        imageVector = Icons.Sharp.Cancel,
                        contentDescription = "返回",
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(16.dp)
                            .clickable { finish() },
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
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        QRCodeScanManager.instant.scanFinish()
    }
}