package com.d10ng.qrcode

import android.app.Activity
import com.d10ng.applib.app.goTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QRCodeScanManager {

    companion object {
        val instant by lazy { QRCodeScanManager() }
    }

    /** 扫码结果 */
    private val scanResultFlow = MutableStateFlow("")
    /** 等待扫码结果协程 */
    private var scope: CoroutineScope? = null

    /**
     * 打开扫码页面
     * @param act Activity
     * @param result Function1<String, Unit>
     */
    fun startScanActivity(act: Activity, result: (String) -> Unit) {
        act.goTo(QRCodeScanActivity::class.java)
        scope?.cancel()
        scope = CoroutineScope(Dispatchers.IO).apply {
            launch {
                scanResultFlow.collect {
                    if (it.isNotEmpty()) result.invoke(it)
                    //println("text $result, $it")
                }
            }
        }
    }

    /**
     * 扫码结束
     * @param result String
     */
    fun scanFinish(result: String = "") {
        if (result.isNotEmpty()) {
            scanResultFlow.value = result
        }
        scope?.cancel()
    }

    /** 获取扫码结果 */
    fun getScanResultFlow() = scanResultFlow.asStateFlow()
}