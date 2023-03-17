package com.d10ng.qrcode

import android.graphics.ImageFormat
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import kotlin.math.min

class QRCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit,
) : ImageAnalysis.Analyzer {

    private val supportedImageFormats = listOf(
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888
    )

    private val reader = MultiFormatReader().apply {
        val map = mapOf<DecodeHintType, Collection<BarcodeFormat>>(
            Pair(DecodeHintType.POSSIBLE_FORMATS, arrayListOf(BarcodeFormat.QR_CODE))
        )
        setHints(map)
    }

    private var mYBuffer = ByteArray(0)

    override fun analyze(image: ImageProxy) {
        if (image.format in supportedImageFormats) {
            val height = image.height
            val width = image.width
            // TODO 调整crop的矩形区域，目前是全屏（全屏有更好的识别体验，但是在部分手机上可能OOM）
            // PlanarYUVLuminaceSource only care the Y plane
            val source = PlanarYUVLuminanceSource(image.toYBuffer(), width, height, 0, 0,
                width, height, false)
            image.close()
            val bitmap = BinaryBitmap(HybridBinarizer(source))

            try {
                val result = reader.decode(bitmap)
                onQrCodeScanned.invoke(result.text)
            } catch (e: Exception) {
                //e.printStackTrace()
            }
        } else {
            Log.e("QRCodeAnalyzer", "expect YUV_420_888/YUV_422_888/YUV_444_888, now = ${image.format}")
            image.close()
            return
        }
    }

    private fun ImageProxy.toYBuffer(): ByteArray {
        val yPlane = planes[0]
        val yBuffer = yPlane.buffer
        yBuffer.rewind()
        val ySize = yBuffer.remaining()
        var position = 0
        if (mYBuffer.size != ySize) {
            Log.w("QRCodeAnalyzer", "swap buffer since size ${mYBuffer.size} != $ySize")
            mYBuffer = ByteArray(ySize)
        }
        // Add the full y buffer to the array. If rowStride > 1, some padding may be skipped.
        for (row in 0 until height) {
            yBuffer.get(mYBuffer, position, width)
            position += width
            yBuffer.position(min(ySize, yBuffer.position() - width + yPlane.rowStride))
        }
        return mYBuffer
    }

}