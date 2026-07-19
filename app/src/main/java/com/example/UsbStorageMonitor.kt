package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.StatFs
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UsbStorageStatus(
    val isConnected: Boolean = false,
    val mountPoint: String = "",
    val availableSpaceMb: Long = 0,
    val totalSpaceMb: Long = 0
)

/**
 * Service module to monitor USB OTG / external storage connections.
 * Provides real-time updates on mount points and available space.
 */
class UsbStorageMonitor(private val context: Context) {
    private val _usbStatus = MutableStateFlow(UsbStorageStatus())
    val usbStatus: StateFlow<UsbStorageStatus> = _usbStatus.asStateFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("UsbStorageMonitor", "Received intent: ${intent?.action}")
            when (intent?.action) {
                Intent.ACTION_MEDIA_MOUNTED -> {
                    val path = intent.data?.path
                    Log.d("UsbStorageMonitor", "USB Mounted at: $path")
                    if (path != null) {
                        updateStatus(true, path)
                    }
                }
                Intent.ACTION_MEDIA_UNMOUNTED,
                Intent.ACTION_MEDIA_REMOVED,
                Intent.ACTION_MEDIA_BAD_REMOVAL -> {
                    Log.d("UsbStorageMonitor", "USB Detached or Unmounted")
                    _usbStatus.value = UsbStorageStatus(isConnected = false)
                }
            }
        }
    }

    fun startMonitoring() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_MEDIA_MOUNTED)
            addAction(Intent.ACTION_MEDIA_UNMOUNTED)
            addAction(Intent.ACTION_MEDIA_REMOVED)
            addAction(Intent.ACTION_MEDIA_BAD_REMOVAL)
            addDataScheme("file")
        }
        context.registerReceiver(receiver, filter)
    }

    fun stopMonitoring() {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: Exception) {
            Log.e("UsbStorageMonitor", "Failed to unregister receiver: ${e.message}")
        }
    }

    private fun updateStatus(isConnected: Boolean, path: String) {
        if (isConnected) {
            try {
                val stat = StatFs(path)
                val blockSize = stat.blockSizeLong
                val availableBlocks = stat.availableBlocksLong
                val totalBlocks = stat.blockCountLong
                
                val availableSpaceMb = (availableBlocks * blockSize) / (1024 * 1024)
                val totalSpaceMb = (totalBlocks * blockSize) / (1024 * 1024)
                
                _usbStatus.value = UsbStorageStatus(
                    isConnected = true,
                    mountPoint = path,
                    availableSpaceMb = availableSpaceMb,
                    totalSpaceMb = totalSpaceMb
                )
            } catch (e: Exception) {
                Log.e("UsbStorageMonitor", "Error calculating storage space: ${e.message}")
                _usbStatus.value = UsbStorageStatus(isConnected = true, mountPoint = path)
            }
        } else {
            _usbStatus.value = UsbStorageStatus(isConnected = false)
        }
    }
}
