package com.d10ng.qrcode

import android.app.Activity
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QRCodeScanManager {

    companion object {
        val instant by lazy { QRCodeScanManager() }
    }

    /** 扫码结果 */
    private val scanResultFlow = MutableSharedFlow<String>()

    /** 协程 */
    private var scope = CoroutineScope(Dispatchers.IO)

    /**
     * 打开扫码页面
     * @param act Activity
     * @return String
     */
    suspend fun startScanActivity(act: Activity): String {
        act.goTo(QRCodeScanActivity::class.java)
        val res = scanResultFlow.first()
        QRCodeScanActivity.instant.get()?.finish()
        return res
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
    internal fun scanFinish(result: String = "") {
        scope.launch { scanResultFlow.emit(result) }
    }
}