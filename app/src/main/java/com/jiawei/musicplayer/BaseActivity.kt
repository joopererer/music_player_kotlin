package com.jiawei.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.jiawei.musicplayer.utils.PermissionUtils

abstract class BaseActivity: ComponentActivity() {

    lateinit var permissionUtils: PermissionUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestFilePermissionAction()
    }

    /**
     * 动态申请访问文件权限
     */
    private fun requestFilePermissionAction() {
        permissionUtils = PermissionUtils(this)
        permissionUtils.filePermissionCallback {
            hasFilePermission(it)
        }
        permissionUtils.requestFilePermission()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionUtils.onRequestResult(requestCode, permissions, grantResults)
    }

    /**
     * 获取到文件权限的回调方法
     */
    abstract fun hasFilePermission(hasPerssion: Boolean)

}