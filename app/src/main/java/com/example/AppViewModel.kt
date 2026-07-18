package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppViewModel : ViewModel() {
    private val _language = MutableStateFlow("ar")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _activeTab = MutableStateFlow("dashboard")
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    private val _updateState = MutableStateFlow("idle")
    val updateState: StateFlow<String> = _updateState.asStateFlow()

    private val _lastCheck = MutableStateFlow("Not checked in this session")
    val lastCheck: StateFlow<String> = _lastCheck.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0)
    val downloadProgress: StateFlow<Int> = _downloadProgress.asStateFlow()

    private val _downloadSpeed = MutableStateFlow("0 MB/s")
    val downloadSpeed: StateFlow<String> = _downloadSpeed.asStateFlow()

    private val _eta = MutableStateFlow("—")
    val eta: StateFlow<String> = _eta.asStateFlow()

    private val _history = MutableStateFlow(AppData.initialHistory)
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    private val _checkLogs = MutableStateFlow<List<String>>(emptyList())
    val checkLogs: StateFlow<List<String>> = _checkLogs.asStateFlow()

    private val _revertState = MutableStateFlow("idle")
    val revertState: StateFlow<String> = _revertState.asStateFlow()

    private val _usbInstallState = MutableStateFlow("idle")
    val usbInstallState: StateFlow<String> = _usbInstallState.asStateFlow()

    private val _powerState = MutableStateFlow("on")
    val powerState: StateFlow<String> = _powerState.asStateFlow()

    private val _cpuUsage = MutableStateFlow(42f)
    val cpuUsage: StateFlow<Float> = _cpuUsage.asStateFlow()

    private val _ramUsage = MutableStateFlow(68f)
    val ramUsage: StateFlow<Float> = _ramUsage.asStateFlow()

    private val _storageHealth = MutableStateFlow(95f)
    val storageHealth: StateFlow<Float> = _storageHealth.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                delay(2000)
                _cpuUsage.value = (_cpuUsage.value + (-5..5).random()).coerceIn(10f, 90f)
                _ramUsage.value = (_ramUsage.value + (-2..2).random()).coerceIn(40f, 85f)
            }
        }
    }

    // Additional state holders for pairing, desktop, settings etc can go here
    val settingsPrivilegedOps = MutableStateFlow(false)

    fun revertOS(t: Translations.Translation) {
        if (_revertState.value != "idle") return
        viewModelScope.launch {
            _revertState.value = "reverting"
            delay(1500)
            addHistory(t.strings["revert_os"] ?: "Revert to Previous OS", "Successful", t.strings["revert_os_desc"] ?: "System restored.")
            _revertState.value = "success"
            delay(3000)
            _revertState.value = "idle"
        }
    }

    fun installFromUsb(t: Translations.Translation) {
        if (_usbInstallState.value != "idle") return
        viewModelScope.launch {
            _usbInstallState.value = "checking"
            delay(1000)
            _usbInstallState.value = "installing"
            delay(2000)
            addHistory(t.strings["install_from_usb"] ?: "Install from USB", "Completed", t.strings["usb_pkg_found"] ?: "Found update package on USB")
            _usbInstallState.value = "success"
            delay(3000)
            _usbInstallState.value = "idle"
        }
    }

    fun runUpdateCheck(t: Translations.Translation) {
        viewModelScope.launch {
            _updateState.value = "checking"
            _checkLogs.value = listOf(t.strings["log_start"] ?: "Starting OTA catalog query...")
            delay(1000)
            _checkLogs.value = _checkLogs.value + (t.strings["log_verify_model"] ?: "Verifying device model (BSN32100S)... OK")
            delay(1000)
            _checkLogs.value = _checkLogs.value + (t.strings["log_verify_api"] ?: "Checking API level compatibility... OK")
            delay(1500)
            _checkLogs.value = _checkLogs.value + (t.strings["log_pkg_found"] ?: "Update package 5.2.1-TV found in channel Stable OTA")
            delay(800)
            _checkLogs.value = _checkLogs.value + (t.strings["log_verify_checksum"] ?: "Validating package manifest checksum... Verified")
            delay(1000)
            _updateState.value = "available"
            addHistory(t.strings["hist_check_title"] ?: "Update Check Performed", "Completed", t.strings["hist_check_detail"] ?: "Found update 5.2.1-TV")
        }
    }

    fun setLanguage(lang: String) { _language.value = lang }
    fun setActiveTab(tab: String) { _activeTab.value = tab }
    fun setPowerState(state: String) { _powerState.value = state }
    
    fun setUpdateState(state: String) { _updateState.value = state }
    fun setLastCheck(check: String) { _lastCheck.value = check }
    fun setDownloadProgress(progress: Int) { _downloadProgress.value = progress }
    fun setDownloadSpeed(speed: String) { _downloadSpeed.value = speed }
    fun setEta(eta: String) { _eta.value = eta }

    fun addHistory(title: String, status: String, detail: String) {
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
        val newItem = HistoryItem(
            id = "hist-\${System.currentTimeMillis()}-\${_history.value.size}",
            time = now,
            title = title,
            status = status,
            detail = detail
        )
        val updated = mutableListOf(newItem).apply { addAll(_history.value) }.take(50)
        _history.value = updated
    }
}
