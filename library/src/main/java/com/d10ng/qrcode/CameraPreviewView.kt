package com.d10ng.qrcode

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

@Suppress("DEPRECATION")
@Composable
fun CameraPreviewView(
    modifier: Modifier,
    analyzer: ImageAnalysis.Analyzer
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context)
            val preview = Preview.Builder().build()
            val selector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            val imageAnalysis = ImageAnalysis.Builder()
                // 新版API，替代setTargetResolution，但用了之后，识别二维码效果变差了，尝试了各种参数都不行，目前这种参数已经是最好的效果了
                //.setResolutionSelector(ResolutionSelector.Builder().setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY).build())
                // TODO 旧版API，但是用了之后，识别二维码效果好，但是会有警告，说这个方法已经过时了，预计1.5.0版本会被移除，到时候再算吧
                .setTargetResolution(Size(previewView.width, previewView.height))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), analyzer)
            try {
                ProcessCameraProvider.getInstance(context).get().bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            previewView
        },
        modifier = modifier
    )
}