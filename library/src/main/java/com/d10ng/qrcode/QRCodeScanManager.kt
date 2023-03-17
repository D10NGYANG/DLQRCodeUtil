package com.d10ng.qrcode

import android.app.Activity
import android.content.Intent
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
                    QRCodeScanActivity.instant.get()?.finish()
                    this.cancel()
                }
            }
        }
    }

    /**
     * 跳转下一个页面，如果栈中有相同的ACT会只保留最新一个到前台
     * @receiver Activity
     * @param clz Class<*>
     */
    private fun Activity.goTo(clz: Class<*>) {
        val intent = Intent(this, clz)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    /**
     * 扫码结束
     * @param result String
     */
    @Synchronized
    fun scanFinish(result: String = "") {
        CoroutineScope(Dispatchers.IO).launch { scanResultFlow.emit(result) }
    }

    /** 获取扫码结果 */
    fun getScanResultFlow() = scanResultFlow.asSharedFlow()
}