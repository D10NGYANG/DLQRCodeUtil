package com.d10ng.qrcode

import android.app.Activity
import com.d10ng.applib.app.goTo
import com.d10ng.coroutines.launchIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class QRCodeScanManager {

    companion object {
        val instant by lazy { QRCodeScanManager() }
    }

    /** 扫码结果 */
    private val scanResultFlow = MutableSharedFlow<String>()
    /** 等待扫码结果协程 */
    private var scope: CoroutineScope? = null
    /** 当前扫描活动 */
    private var curScanAct: Activity? = null

    /**
     * 打开扫码页面
     * @param act Activity
     * @param result Function1<String, Unit>
     */
    @Synchronized
    fun startScanActivity(act: Activity, result: (String) -> Unit) {
        act.goTo(QRCodeScanActivity::class.java)
        scope?.cancel()
        scope = CoroutineScope(Dispatchers.IO).apply {
            launch {
                scanResultFlow.collect {
                    result.invoke(it)
                    curScanAct?.finish()
                    this.cancel()
                }
            }
        }
    }

    /**
     * 扫码结束
     * @param act Activity
     * @param result String
     */
    @Synchronized
    fun scanFinish(act: Activity, result: String = "") {
        curScanAct = act
        launchIO { scanResultFlow.emit(result) }
    }

    /** 获取扫码结果 */
    fun getScanResultFlow() = scanResultFlow.asSharedFlow()
}